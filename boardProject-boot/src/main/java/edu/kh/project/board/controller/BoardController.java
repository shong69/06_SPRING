package edu.kh.project.board.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
