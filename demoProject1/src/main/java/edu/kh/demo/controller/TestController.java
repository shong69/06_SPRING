package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// Java 객체 : new 연산자에 의해 Heap 영역에
//			클래스에 작성된 내용대로 생성되는 것

//instance : 개발자가 만들고 관리하는 객체

//Bean : Spring Container(Spring)이 만들고 관리하는 객체

@Controller // : 요청, 응답을 제어하는 컨트롤러의 역할을 명시 + Bean으로 등록해줌( == 객체로 생성해서 스프링이 관리해줌)
public class TestController {
	//기존 Servlet : 클래스 단위로 하나의 요청만 처리가 가능했음
	//Spring : 메서드 단위로 요청 처리가 가능해짐
	
	
	
	
	//@RequestMapping("요청주소")
	// - 요청 주소를 처리할 메서드를 매핑하는 어노테이션
	
	
	// 1) 메서드에 작성했을 때 : 
	// - 요청 주소와 메서드를 매핑해줌
	// - GET/POST 가리지 않고 매핑함(속성을 통해서 지정 가능함)
	
	/* ******************** */
	//Spring Boot Controller에서 특수한 경우를 제외하고는 
	//매핑주소 제일 앞에 슬래시(/) 작성 안한다
	/* ******************** */
	
	@RequestMapping("test")  //  /test 요청 시 처리할 메서드를 매핑해줌(get,post가리지 않고)
	public String testMethod() {
		System.out.println("/test 요청 받음");
		
		/*Controller 메서드의 반환형이 String이어야 하는 이유
		 * - 메서드에서 반환되는 문자열이
		 * forward할 html 파일의 경로가 되기 때문
		 * */
		
		/*
		 * Thymeleaf : JSP 대신 사용하는 템플릿 엔진. 리턴 자리에 접두사, 접미사를 붙여줌
		 * viewResolver가 리턴 값으로 URL을 받아 웹 애플리케이션 디렉토리에서 해당 파일을 찾는다.
		 * 
		 *  접두사 : classpath:/templates/        -> (classpath : src/main/resources) 
		 *  접미사 : .html  
		 * */
		//src/main/resources/templates/<  test  >.html
		
		return "test"; //forward한 것(접두사 + 반환값 + 접미사 경로의 html로 forward해줌)
	} 
	
	
	
	// 2) 클래스에 작성할 때 :
	// - 공통주소를 매핑한다
	// ex) /todo/insert, /todo/select , /todo/update 
	/* 
	@RequestMapping("todo")
	@Controller
	public class TodoController {
		@RequestMapping("insert")  //->todo/insert 매핑
		public String insert(){}
		
		@RequestMapping("select")  //->todo/select 매핑
		public String select(){}
		
		@RequestMapping("update")  //->todo/update 매핑
		public String update(){}
	
	}
		*/

	
}