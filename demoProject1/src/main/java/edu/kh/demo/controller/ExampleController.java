package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

//Bean : 스프링이 주도권을 가지고 만들거나 관리하는 객체

@Controller  //Bean으로 등록해줌(+요청, 응답을 제어하는 역할인 Controller임을 명시해줌)
@Slf4j
public class ExampleController {
	
	/*요청 주소를 매핑하는 방법
	 * 
	 * 1)@RequestMapping("주소") 
	 * 
	 * 2)@GetMapping("주소") : GET(조회) 방식 요청 매핑
	 *   @PostMapping("주소") : POST(삽입) 방식 요청 매핑
	 *   @PutMapping("주소") : PUT(수정) 방식 요청 매핑 
	 *   @DelteMapiing("주소") : DELETE(삭제) 방식 요청 매핑 - http프로토콜에서 crud를 맡는 역할들이다
	 * 
	 * */
	@GetMapping("example") //example GET 방식 요청 매핑
	public String exampleMethod() {

		//forward 하려는 html 파일의 경로를 작성함
		//단, ViewResolver가 제공하는 Thymeleaf의 접두사, 접미사를 제외하고 작성한다.(classpath:templates랑 .html)
		log.debug("");
		return"example";  //src/main/resources/templates/example.html
	}
	
}
