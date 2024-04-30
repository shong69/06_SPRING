package edu.kh.project.board.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.service.CommentService;
import lombok.RequiredArgsConstructor;

/* @RestController : REST API 구출을 위해 사용하는 컨트롤러
 *  = @Controller + @ResponseBody 사용한 것과 같은 효과
 *    (요청/응답 제어 + bean 등록 ) + (응답 본문으로 데이터 자체를 반환)
 *    
 *   -> 모든 응답을 응답 본문(ajax)으로 반환하는 컨트롤러
 *   
 * 
 * */
//@Controller // Controller 명시 + Bean으로 등록
@RestController  //비동기 요청만 받는 컨트롤러임을 선언
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {

	//받을 요청이 전부 fetch를 통한 비동기 요청임
	// -> "comment" 요청이 오면 해당 컨트롤러에서 잡아서 처리할거임
	
	//@ResponseBody를 매번 메서드에 추가했었음
	//-> RestController 사용시 위 어노테이션 안써도 됨
	private final CommentService service;
	
	
	/** 댓글 목록 조회
	 * @param boardNo
	 * @return
	 */
	@GetMapping("") //comment만 있는 get 요청이니까 빈 문자열로 적는다
	public List<Comment> select(@RequestParam("boardNo") int boardNo) {
		
		//HttpMessageConverter가
		//List -> JSON(문자열)로 변환해서 응답해준다
		return service.select(boardNo);
	}
	
	
	/** 댓글/답글 등록
	 * @return
	 */
	@PostMapping("")
	public int insert(@RequestBody Comment comment) {
		
		return service.insert(comment);
	}
	
	
	/** 댓글 수정
	 * @param comment
	 * @return
	 */
	@PutMapping("")
	public int update(@RequestBody Comment comment) {
		return service.update(comment);
	}
	
	
	@DeleteMapping("")
	public int delete(@RequestBody int commentNo) {
		return service.delete(commentNo);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
