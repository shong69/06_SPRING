package edu.kh.project.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.service.BoardService;
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
				RedirectAttributes ra
			) {
		//게시글 상세 조회 서비스 호출
		
		//1) Map으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		//2) 서비스 호출 -> 게시글 하나를 반환받을거임
		Board board = service.selectOne(map);
		
		String path = null;
		
		//조회 결과가 없는 경우
		if(board== null) {
			path = "redirect:/board/"+ boardCode; //보고 있던 목록으로 다시 돌아가기
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
		
		}else { //조회 결과가 있는 경우
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
