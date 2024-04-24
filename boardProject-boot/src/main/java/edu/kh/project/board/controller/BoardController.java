package edu.kh.project.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {

	private final BoardService service;
	
	/**
	 * @param boardCode : 게시판 종류 구분 번호
	 * @param cp : 현재 조회 요청한 페이지 값(없으면 1임) 
	 * @return
	 * 
	 * - /board/xxx " /board 이하 1레벨 자리에 숫자로 된 요청 주소가 작성돼있을 때만 동작하도록 정규표현식 작성
	 * 
	 * [0-9] : 한 칸에 0~9 사이 숫자가 입력 가능하다는 정규표현식
	 * + : 하나 이상(여러칸 가능)
	 * [0-9]+ : 모든 숫자 가능
	 */
	@GetMapping("{boardCode:[0-9]+}") 
	public String selectBoardList(
			@PathVariable("boardCode") int boardCode,   //boardCode값이 들어오면 매개변수로 들어와서 해당 값 사용 가능
			@RequestParam(value="cp", required=false, defaultValue="1") int cp,  //cp : correct page
			Model model
			) { 
		
		log.debug("boardCode : "+ boardCode);
		
		//조회 서비스 호출 후 결과 반환
		Map<String, Object> map = service.selectBoardList(boardCode, cp);
		
		
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		
		log.debug("boardList: " + map.get("boardList"));
		log.debug("pagination: " + map.get("pagination"));
		
		
		return "board/boardList";  //boardList.html로 포워드하기
	}
	
	//상세조회 요청 주소
	//주소 생김새 /board/1/1989?cp=1
	
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}") //{boardCode:[0-9]+} : boardCode를 숫자로만 받겠다는 pathVariable
	public String boardDetail(
				@PathVariable("boardCode") int boardCode,
				@PathVariable("boardNo")   int boardNo,
				Model model,
				RedirectAttributes ra,
				@SessionAttribute(value="loginMember", required=false) Member loginMember,
				HttpServletRequest req, //요청에 담긴 쿠키 얻어오기
				HttpServletResponse resp //새로운 쿠키를 만들어서 응답하기
			) {
		//@SessionAttribute(value="loginMember", required=false) required=false로 로그인 안 한 상태로 상세조회 가능하게
		// - @SessionAttribute : Session에서 속성 값 얻어오기
		// - value="loginMember" : 속성의 key 값 loginMember
		// - required=false : 필수 X -> 해당 속성값이 없다면 null 반환할 수 있도록 한다
		
		
		//게시글 상세 조회 서비스 호출
		
		//1) Map으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode); //게시판 번호
		map.put("boardNo", boardNo); //글 번호
		
		//로그인 상태인 경우에만 memberNo 추가하기
		if(loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}
		
		
		//2) 서비스 호출 -> 게시글 하나를 반환받을거임
		Board board = service.selectOne(map);
		
		String path = null;
		
		//조회 결과가 없는 경우
		if(board== null) {
			path = "redirect:/board/"+ boardCode; //보고 있던 목록으로 다시 돌아가기
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
		
		}else { //조회 결과가 있는 경우 
			/***********************쿠키를 이용한 조회수 증가(시작)****************************/
			
			// 1. 비회원 또는 로그인한 회원의 글이 아닌 경우 
			//    글쓴이를 뺀 다른 사람이 본 경우에만 증가
			if(loginMember == null || 
					loginMember.getMemberNo() != board.getMemberNo()) {
				
				//요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();
				
				Cookie c = null;
				
				for(Cookie temp:cookies) {
					
					//요청에 담긴 쿠키에 "readBoardNo"가 존재할 때
					if(temp.getName().equals("readBoardNo")) {
						c = temp;
						break;
					}
					
				}
				
				
				int result = 0; //조회수 증가 결과를 저장할 변수
				//"readBoardNo"가 쿠키에 없을 때
				if(c == null) {
					
					//새 쿠키 생성하기("readBoardNo", [게시글번호])
					c=new Cookie("readBoardNo", "["+boardNo+"]");
					result = service.updateReadCount(boardNo); //조회수 업데이트
					
					
					
				} else {//"readBoardNo"가 쿠키에 있을 때
					
					// "readBoardbo" : [2][30][400][2000]->String
					//1)현재 게시글을 처음 읽은 경우
									//indexOf : 찾아서 있으면 인덱스 번호 반환, 없으면 -1반환
					if(c.getValue().indexOf("["+boardNo+"]")==-1) { 
						
						//해당 글 번호를 쿠키에 누적 + 서비스 호출
						c.setValue(c.getValue()+"[" + boardNo + "]");
						result = service.updateReadCount(boardNo);
					}
				}
				//조회수 증가 성공 / 조회 성공 시
				if(result > 0) {
					//먼저 조회된 board의 readCount 값을 resulst 값으로 변환하라
					board.setReadCount(result);
					
					log.debug("result  : " +result);
					
					
					//쿠키 적용 경로
					c.setPath("/"); //최상위 주소로 경로 지정해, 최상위 경로 이하의 경로 요청 시 쿠키 서버로 전달한다
					
					//수명 지정
					
					//현재 시간을 얻어오기
					LocalDateTime now = LocalDateTime.now();
					
					//다음날 자정
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
					
					//다음날 자정까지 남은 시간 계산(초단위로)
					
					long secondsUntilNextDay = Duration.between(now, nextDayMidnight).getSeconds(); //초단위로 계산해서 변수에 부여
					
					//쿠키 수명 설정
					c.setMaxAge((int)secondsUntilNextDay);
					
					resp.addCookie(c); //응답객체를 이용해서 클라이언트에게 전달하기
				}
			
			
				
			}
			
			
			
			
			/***********************쿠키를 이용한 조회수 증가(끝)****************************/
			path = "board/boardDetail";
			
			//board - 게시글 일반 내용 + imageList + commentList 다 들어있음
			model.addAttribute("board", board);
			
			//조회된 이미지 목록(imageList가 있을 경우)
			if(!board.getImageList().isEmpty()) {
				
				BoardImg thumbnail = null;
				
				//imageList의 0번 인덱스 == 가장 빠른 순서(imgOrder순 조회)
				//이미지 목록의 첫번째 행이 순서 0 == 썸네일인 경우
				if(board.getImageList().get(0).getImgOrder()==0) { //0번째 이미지를 꺼냄 get(0)
					thumbnail = board.getImageList().get(0); 
					//썸네일에 0번째 인덱스 이미지 실어주기
				}
				model.addAttribute("thumbnail", thumbnail);
				model.addAttribute("start", thumbnail != null? 1:0);
				// 썸네일이 있으면 start가 1 -> 
			}
		}
		
		
		
		return path; //board/boardDetail.html 로 forward
	}
	
	
	/** 게시글 좋아요 체크/해제
	 * @param map
	 * @return count
	 */
	@ResponseBody
	@PostMapping("like")
	public int boardLike(@RequestBody Map<String, Integer> map) {
		
		return service.boardLike(map);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
