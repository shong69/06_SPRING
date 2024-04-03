package edu.kh.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.demo.model.dto.Student;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller //요청,응답 제어 역할 명시 + Bean 등록
@RequestMapping("example")  //공통 주소 명시
@Slf4j   //lombok 라이브러리가 제공하는 로그 객체 자동 생성 어노테이션
public class ExampleController {

	
	
	/* Model
	 * - Spring에서 데이터 전달 역할을 하는 객체
	 * 
	 * - org.springframework.ui 패키지
	 * 
	 * - 기본 Scope : request 이다
	 * 
	 * - @SessionAttributes 와 함께 사용할 경우, session Scope로 변환된다
	 * 
	 * [기본 사용법]
	 * Model.addAttribute("key", value);
	 * 
	 * */
	
	
	
	
	@GetMapping("ex1") //  /example/ex1의 GET 방식 요청 매핑
	public String ex1(HttpServletRequest req, Model model) {
		
		//scope 내장객체 범위
		//page < request < Session <Application
		
		req.setAttribute("test1","HttpServletRequest로 전달한 값");
		
		model.addAttribute("test2", "Model로 전달한 값");
		
		
		
		//단일 값(숫자, 문자열) Model을 이용하여 html로 전달하기
		model.addAttribute("productName", "종이컵");
		model.addAttribute("price", 2000);
		
		
		//복수 값(배열, List) Model을 이용해서 html로 전달
		List<String> fruitsList = new ArrayList<>();
		fruitsList.add("사과");
		fruitsList.add("딸기");
		fruitsList.add("바나나");
		
		model.addAttribute("fruitsList", fruitsList);
		
		
		
		//DTO객체를 Model을 이용해서 html로 전달하는 방법
		Student std = new Student();
		std.setStudentNo("12345");
		std.setName("홍길동");
		std.setAge(22);
		
		model.addAttribute("std", std);
		
		
		//List<Student>객체를 Model을 이요해서 html로 전달하기
		List<Student> stdList = new ArrayList<>();
		stdList.add(new Student("1111", "김일번", 20));
		stdList.add(new Student("2222", "최이번", 20));
		stdList.add(new Student("3333", "백삼번", 20));
		
		model.addAttribute("stdList",stdList);
		
		
		
		return "example/ex1";  //templates/example/ex1.html 요청 위임해줌
	}
	
	
	
	@PostMapping("ex2")
	public String ex2(Model model) {
		
		//request -> inputName="홍길동", inputAge=23, color=[Red,Green,Blue]
		
		model.addAttribute("str", "<h1>테스트 중 &times </h1>");
		
		return "example/ex2";
	}
	
	
	@GetMapping("ex3")
	public String ex3(Model model) {
		//model : 데이터 전달용 객체 (Request Scope)
		
		model.addAttribute("boardNo", 10);
		
		model.addAttribute("key", "제목");
		model.addAttribute("query","검색어");
		
		return "example/ex3";
	}
	
	
	
	/* @PathVariable : 주소 중 일부분을 변수값처럼 사용함
	 ** - 해당 어노테이션으로 얻어온 값은 request Scope에 세팅
	 *
	 * 메서드의 Mapper에 변수를 주고, 파라미터에서 선언한다.
	 * 주소 중 {number} 부분의 값을 가져와, 매개변수에 저장한다
	 *  + request scope에도 저장하고 있음
	 * */
	@GetMapping("ex3/{number}")
	public String pathVariableTest(
			@PathVariable("number") int number) {
		
		log.debug("number : "+number);
		
		return "example/testResult";
	}
	
	
	@GetMapping("ex4")
	public String ex4(Model model) {
		
		Student std = new Student("67890", "잠만보", 22);
		
		model.addAttribute("std", std);
		model.addAttribute("num", 100);
		
		return "example/ex4";
	}
	
	@GetMapping("ex5")
	public String ex5(Model model) {
		
		//Model : spring에서 값 전달 역할을 하는 객체
		//기본적으로 request Scope + session으로 확장 가능함
		
		model.addAttribute("message", "타임리프 + JAVA SCRIPT 사용 연습");
		model.addAttribute("num1", 23455);
		Student std = new Student();
		std.setStudentNo("2222");
		model.addAttribute("std", std);
		return "example/ex5";
	}
	
	
	
	
}
