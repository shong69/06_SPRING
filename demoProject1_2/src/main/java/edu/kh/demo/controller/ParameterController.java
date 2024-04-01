package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
//Bean : 스프링이 만들과 관리하는 객체
@Controller //요청, 응답 제어 역할 명시 + Bean 등록
@RequestMapping("param")    // /parmam으로 시작하는 모든 요청을 받아들이는 Controller //log를 이용한 메세지 출력 시 사용(lombok 라이브러리에서 제공됨)
@Slf4j
public class ParameterController {
	
	@GetMapping("main")  // /param/main GET방식 요청 매핑
	public String paramMain() {
		
		//접두사 : src/main/resources/templates/ 
		//접미사 : .html
		//src/main/resources/templates/param/param-main.html
		return"param/param-main";
	}
	
	
	/*
	 * 1. HttpServletRequest.getParameter("key") 이용
	 * 	
	 * HttpServletRequest : 요청 클라이언트의 정보, 제출된 파라미터 등을 저장한 객체
	 * 	-클라이언트 요청 시 생성
	 * 
	 * 
	 * ArgumentResolver(전달인자 해결사) : 
	 * 파라미터에 원하는 객체가 있을 시 전달인자 해결사를 작성하여
	 * 전달인자 해결사에 객체를 대입하여 사용한다.
	 * ->Spring의 Controller 메서드 작성 시 매개변수에 원하는 객체를 작성하면 
	 * 	존재하는 객체를 바인딩하거나 새로 생성하여 바인딩한다.
	 * 
	 * */
	
	
	
	@PostMapping("test1")  // /param/test1의 POST방식 매핑에 사용되는 메서드
	public String paramtest1(HttpServletRequest req) {
		
		String inputName = req.getParameter("inputName");
		String inputAddress = req.getParameter("inputAddress");
		int inputAge = Integer.parseInt(req.getParameter("inputAge")); 
		//req로 넘어오며 String형이 되기 때문에 int로 바꾸기 위해 wrapper 클래스인 Integer로 감싸서 형변환하기
		
		//디버깅하여 콘솔창에 로그 확인하기
		//debug : 코드 오류를 해결하는 것 ->코드 오류 없지만 정상 수행이 안될 때
		// ->값이 잘못된 경우 값을 추적해야 함 ->디버깅 고고
		
		
		log.debug("inputName :" + inputName);
		log.debug("inputAddress :" + inputAddress);
		log.debug("inputAge: " + inputAge);
		
		/* Spring에서 Redirect(재요청)하는 방법
		 * -Controller에 메서드 반환 값에  "redirect:요청주소"; 작성하기
		 * */
		return "redirect:/param/main";
	}
	
	
	/*2. @RequestParam 어노테이션을 이용 -낱개 파라미터 얻어오기
	 * - request 객체를 이용한 파라미터 전달 어노테이션
	 * - 매개변수 앞에 해당 어노테이션을 작성하면, 매개변수에 값이 주입됨
	 * - 주입되는 데이터는 매개변수의 타입에 맞게 형변환/파싱이 자동으로 수행됨
	 * 
	 * [기본 작성법]
	 * @RequestParam("key") 자료형 매개변수명
	 * 
	 * [속성 추가 작성법]
	 * @RequestParam(value = "name(key)", required= false, defaultValue="1")
	 * 
	 * value : 전달받은 input태그의 name 속성값
	 * required : 입력된 name 속성값의 파라미터 필수 여부를 지정함(기본값 true)
	 * -> required = true인 파라미터가 존재하지 않는다면 400BadRequest 에러가 발생함
	 * defaultValue : 파라미터 중 일치하는 name 속성값이 없을 경우 대입할 값을 지정함
	 * 				->required = false인 경우 사용한다
	 * */
	@PostMapping("test2")
	public String paramTest2(@RequestParam("title") String title,
							@RequestParam("writer") String writer,
							@RequestParam("price") int price,
							@RequestParam(value ="publiser", required=false, defaultValue="ABC출판사") String publisher) {
		
		log.debug("title : " + title);
		log.debug("writer : " + writer);
		log.debug("price : " + price);
//		
		
		return "redirect:/param/main";
	}
}

