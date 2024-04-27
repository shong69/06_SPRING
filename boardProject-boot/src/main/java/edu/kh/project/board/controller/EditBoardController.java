package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.board.model.service.EditBoardService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("editBoard")
@Slf4j
public class EditBoardController {
	
	private final BoardService boardService;
	private final EditBoardService service;

	/** 게시글 작성 화면 전환
	 * @param boardCode
	 * @return board/boardWrite
	 */
	@GetMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(
			@PathVariable("boardCode") int boardCode
			) {
		
		return "board/boardWrite";
	}

	/** 게시글 작성
	 * @param boardCode : 어떤 게시판에 작성할 글인지 구분
	 * @param inputBoard : 입력된 값(제목, 내용) 세팅되어있음(커맨드 객체)
	 * @param loginMember : 로그인한 회원 번호를 얻어오는 용도
	 * @param images : 제출된 file 타입 input 태그 데이터들(이미지 파일,...)
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(
				@PathVariable("boardCode") int boardCode,
				@ModelAttribute Board inputBoard,
				@SessionAttribute("loginMember") Member loginMember,
				@RequestParam("images") List<MultipartFile> images,
				RedirectAttributes ra
			) throws IllegalStateException, IOException{
		
		/*	List<MultipartFile> images
		 *  - 5개 모두 업로든 => 0~4번 인덱스에 파일 저장됨
		 *  - 5개 모두 업로드 X => 0~4번 인덱스에 파일 저장 X
		 *  - 2번 인덱스만 업로드 => 2번 인덱스만 파일 저장, 0/1/3/4번 인덱스는 저장 X
		 *  
		 *  
		 *  [문제점]
		 * - 파일이 선택되지 않은 input태그 값을 서버에 저장하려고 하면 오류가 발생함
		 * 
		 *  [해결방법]
		 * - 무작정 서버에 저장하지 않고 
		 * - 제출된 파일이 있는지 확인하는 로직 추가 구성
		 * 
		 *  + List 요소의 index 번호 == IMG_ORDER와 같음
		 * */
		
		// 1. boardCode, 로그인한 회원 번호를 inputBoard에 세팅
		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		
		// 2. 서비스 메서드 호출 후 결과 반환받기
		// -> 성공 시 [상세 조회]를 요청할 수 있또록
		// 삽입된 게스글 번호를 반환받기
		
		int boardNo = service.boardInsert(inputBoard, images);
		
		// 3. 서비스 수행 결과에 따라 messgae, 리다이렉트 경로 저장
		String path = null;
		String message = null;
		
		if(boardNo > 0) {
			
			path = "/board/" + boardCode + "/" + boardNo; //상세 조회
			message = " 게시글이 작성되었습니다";
		}else {
			path = "insert";
			message = "게시글 작성 실패";
		}
		
		ra.addFlashAttribute("message", message);		
		
		return "redirect:"+path;
		
	}
	
	
	
	
	
	
	
	
	
	/** 게시글 수정 화면으로 전환
	 * @param boardCode : 게시판 종류의 번호 
	 * @param boardNo : 게시글 번호
	 * @param loginMember : 세션에 올라간 로그인 회원 -> 로그인 한 회원이 작성한 글 맞는지 검사할거임
	 * @param model : forward 시 request scope로 값 전달 용도
	 * @param ra : redirect 시 request scope로 값 전달 용도
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update") //pathVariable로 boardCode 받아오고 모든 숫자 받겠다
	public String boardUpdate(
				@PathVariable("boardCode") int boardCode,
				@PathVariable("boardNo") int boardNo,
				@SessionAttribute("loginMember") Member loginMember,
				Model model, //request scope
				RedirectAttributes ra
			) {
		
		//수정화면에 출력할 기존 제목/내용/이미지 조회
		// -> 게시글 상세조회 하기
		
		Map<String, Integer> map = new HashMap<>();
		
		map.put("baordCode", boardCode);
		map.put("boardNo", boardNo);
		
		//BoardService.selectOne(map) 호출 ->반환값 Board
		Board board = boardService.selectOne(map);
		
		String message = null;
		String path = null;
		
		if(board == null) {
			message = "해당 게시글이 존재하지 않습니다";
			path = "redirect:/"; //메인페이지로 보내기
			ra.addFlashAttribute("message", message);
			
		} else if(board.getMemberNo() != loginMember.getMemberNo()) {
			message = "자신이 작성한 글만 수정할 수 있습니다!";
			
			//해당 글 상세조회 화면으로 리다이렉트
			path = String.format("redirect:/baord/%d/%d", boardCode, boardNo); //fString처럼 형식에 맞게 값 삽입 가능
			
			ra.addFlashAttribute("message", message);
		}else { //나머지 경우 -> 맞게 들어온 경우
			path = "board/boardUpdate"; //templates/board/boardUpdate.html 로 forward됨
			model.addAttribute("board", board);
		}
		
		
		
		return path;
	}
	
	/**게시글 수정
	 * @param boardCode 	: 게시판 종류 번호 
	 * @param boarNo 		: 수정할 게시글 번호
	 * @param inputBoard 	: 커맨드 객체(제목, 내용 세팅됨)
	 * @param loginMember 	: 로그인한 회원 번호 이용(로그인 == 작성자)
	 * @param images 		: 제출된 input type="file" 모든 요소
	 * @param ra			: redirect 시 request scope 값 전달
	 * @param deleteOrder	: 삭제된 이미지 순서가 기록된 문자열 (1,2,3)
	 * @param queryString	: 수정 성공 시 이전 파라미터 유지(cp)
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(
				@PathVariable("${boardCode}") int boardCode,
				@PathVariable("${boardNo}") int boarNo,
				@ModelAttribute Board inputBoard, //커맨드 객체
				@SessionAttribute("loginMember") Member loginMember,
				@RequestParam("images") List<MultipartFile> images, //name=image인 요소들 파일 형태 리스트로 가져오기
				RedirectAttributes ra,
				@RequestParam(value = "deletOrder", required=false) String deleteOrder, //삭제된 이미지의 순서 기억하는 deleteOrder
				@RequestParam(value = "queryString", required=false, defaultValue = "") String queryString
			) throws IllegalStateException, IOException {
		
		// 1. 커맨드 객체 inputBoard 제목, 내용 + boardCode + boardNo + memberNo 세팅하기
		inputBoard.setBoardCode(boardCode);
		inputBoard.setBoardNo(boarNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		//-> inputboard(제목, 내용, boardCode, boardNo, memebrNo)
		
		
		// 2. 게시글 수정 서비스를 호출한 다음에 결과 반환받기
		int result = service.boardUpdate(inputBoard, images, deleteOrder);
		
		
		
		// 3. 서비스 결과에 따라 응답 제어하기
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "게시글이 수정되었습니다.";
			path = String.format("/board/%d/%d%s", boardCode, boarNo, queryString);  //   /board/1/2000?cp=2
		} else {
			message = "수정실패";
			path = "update"; //상대경로, 현재 주소에서 맨 뒤에만 update로 바꿈(수정화면 전환 리다이렉트함)
						
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
	}
}
