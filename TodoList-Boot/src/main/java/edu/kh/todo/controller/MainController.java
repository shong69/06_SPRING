package edu.kh.todo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j   //로그 객체 자동 생성
@Controller //요청/응답 제어 역할 명시 + Bean 등록 요청
public class MainController {
	
	@Autowired //의존성 주입
	private TodoService service;
	
	@RequestMapping("/") //최상위로 보내는 요청
	public String mainPage(Model model) {
		
		//Service 메서드 호출 후 결과 반환받기
		Map<String, Object> map = service.selectAll();
		
		//map에 담긴 내용 추출
		List<Todo> todoList = (List<Todo>)map.get("todoList");
		int completeCount = (int)map.get("completeCount");
		
		model.addAttribute("todoList", todoList);
		model.addAttribute("completeCount", completeCount);
		
		
		
		
		
		// 접두사 : classpath:/templates  //접미사 : .htmln  ->이쪽으로 forward 하겠다고
		return "common/main";
	}
}
