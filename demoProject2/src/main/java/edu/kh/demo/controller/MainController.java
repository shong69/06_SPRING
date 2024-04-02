package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//Controller : 요청에 따라 알맞은 서비스 호출 여부를 제어함 
//				+ 호출한 서비스의 결과에 따라 응답 여부 제어함

@Controller //IOC(제어의 역전) 일어남. (요청, 응답, 제어의 역할을 명시하고 Bean으로 등록해줌)
public class MainController {
	
	
	@RequestMapping("/")   //최상위페이지에서 주소 요청 시 이 메서드와 매핑됨(메인페이지 지정시 슬래쉬 쓸 수 있음)
	public String mainPage() {
		
		//forward : 요청 위임
		//thymleaf : Spring Boot에서 사용하는 템플릿 엔진
		
		//thymleaf를 이용한 html 파일로 forward 시
		//사용되는 접두사, 접미사가 존재함
		
		//접두사 : classpath:/templates/  접미사 : .html
		//src/main/resources/templates/common/main.html
		return"common/main";
		
		
		/*최상위 루트에 접속하자마자 비즈니스 로직이 돌아야하는 경우(바로 db 붙이는 경우)에 forward 시켜주는 거임 
		 * */
	}
}
