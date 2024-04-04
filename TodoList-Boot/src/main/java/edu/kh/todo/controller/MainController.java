package edu.kh.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j   //로그 객체 자동 생성
@Controller //요청/응답 제어 역할 명시 + Bean 등록 요청
public class MainController {
	
	@RequestMapping("/") //최상위로 보내는 요청
	public String mainPage() {
		
		// 접두사 : classpath:/templates  //접미사 : .htmln  ->이쪽으로 forward 하겠다고
		return "common/main";
	}
}
