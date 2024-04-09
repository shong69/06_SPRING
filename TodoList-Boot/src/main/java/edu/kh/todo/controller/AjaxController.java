package edu.kh.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;


/* @ResposeBody : 컨트롤러 메서드의 반환값을 HTTP 응답 본문에 직접 바인딩하는 역할임을 명시하는 어노테이션
 * 
 * -컨트롤러 메서드의 반환값을 비동기 요청했던 HTML/JS 파일 부분에 값을 돌려보낼 것이다~! 라고 명시함
 * 
 * -forward/redirect로 더이상 인식하지 않게됨
 * 
 * @RequestBody : 비동기 요청 시 전달되는 데이터 중, 
 *   body 부분에 포함된 요청 데이터를 알맞은 Java 객체 타입으로 바인딩하는 어노테이션
 *   - 비동기 요청 시, body에 담긴 값을 알맞은 타입으로 변환해서 매개변수에 저장해준다
 *
 * [HttpMessageConverter] 
 * Spring에서 비동기 통신 시 
 *  - 전달되는 데이터의 자료형
 *  - 응답하는 데이터의 자료형
 *  위 두가지를 알맞은 형태로 가공(변환)해주는 객체이다
 *  
 *  - 문자열, 숫자 <-> TEXT
 *  - DTO <-> JSON
 *  - Map <-> JSON
 *  
 *  (참고)
 *  
 *  HttpMessageConverter가 동작하기 위해서는
 *  Jackson-data-bind 라이브러리가 원래 필요한데
 *  SpringBoot는 모듈에 이게 내장되어 있다.
 *  - Jackon은 Java에서 JSON을 다루는 방법을 제공하는 라이브러리이다.
 *   
 * */





@Controller //요청/응답 제어 역할임을 명시 + Bean 등록
@Slf4j  //로그 찍어줌
@RequestMapping("ajax") //공통 주소 지정
public class AjaxController {

	@Autowired //등록된 Bean 중 같은 타입이거나 상속관계인 Bean을 해당 필드에 의존성 주입함(DI)(new 객체 선언 안해두 됨)
	private TodoService service;
	
	
	@GetMapping("main") //    /ajax/main의 Get 요청 매핑
	public String ajaxMain() {
		
		
		
		
		//접두사 : classpath:templates/  접미사 : .html
		return"ajax/main";
	}
	
	//전체 Todo 개수를 조회함
	//forward / redirect 
	@ResponseBody //값을 호출한 쉑에게 돌려보내줌
	@GetMapping("totalCount")
	public int getTotalCount() {
		
		//전체 할 일 개수 조회 서비스 호출 및 응답
		int totalCount = service.getTotalCount();
		
		return totalCount;
	}
	
	
	@ResponseBody
	@GetMapping("completeCount")
	public int getCompleteCount() {
		
		return service.getCompleteCount();
	}
	
	
	@ResponseBody //비동기 요청 결과로 값 자체를 반환할 것임
	@PostMapping("add")
	public int addTodo(
			//JSON이 파라미터로 전달된 경우 아래 방법으로 얻어오기 불가능하다
			//@RequestParam("todoTitle") String todoTitle, ...
			@RequestBody Todo todo // 요청 body에 담긴 값을 Todo DTO에 저장해준다
			
			) {
		log.debug(todo.toString());
		
		return service.addTodo(todo.getTodoTitle(), todo.getTodoContent());
	}
	
	@ResponseBody
	@GetMapping("selectList")
	public List<Todo> selectList() {
		
		
		List<Todo> todoList = service.selectList();
		return todoList;
		
		//List(자바의 전용 타입임)을 반환하려고 한다
		//-> 원래는 JS가 인식할 수 없음
		//=>HttpMessageConverter가 List에서 JSON으로 형변환을 해줄것이다 == [{},{},{}] JSONArray
		//(HttpMessageConverter가 STring형태로 요청을 보낸 애들한테 보내줌)
	}
	
	
	@ResponseBody //값을 호출한 쉑에게 돌려보내줌
	@GetMapping("detail")
	public Todo selectTodo(@RequestParam("todoNo") int todoNo) { //url을 매개변수로 넘겨주고 있어서 requestparam사용 가능
		//return 자료형 : Todo
		// -> HttpMessageConverter가 String(JSON)형태로 변환해서 반환함
		return service.todoDetail(todoNo);
	}
	
	
	
	
	//Delete 방식의 요청 처리 메서드(비동기 요청만 가능한 어노테이션이다 /delete, put만 그럼)
	@DeleteMapping("delete")
	@ResponseBody
	public int todoDelete(@RequestBody int todoNo) {
		
		
		return service.todoDelete(todoNo);
	}
	
	
	//완료 여부를 변경하는 메서드
	@PutMapping("changeComplete")
	@ResponseBody //비동기요청한 쪽으로 값 자체를 돌려보내줌
	public int changeComplete(@RequestBody Todo todo) { //dto를 활용해서 RequestBody의 여러 파라미터 한번에 가져올 수 있다
		
		return service.changeComplete(todo); //미리 만들어뒀던 메서드 사용하기
	}
	
	
	//할 일 수정
	@PutMapping("update")
	@ResponseBody
	public int todoUpdate(@RequestBody Todo todo) {
		
		return service.todoUpdate(todo);
	}
	
	
	
	
	
	
	
	
	
	
	
}
