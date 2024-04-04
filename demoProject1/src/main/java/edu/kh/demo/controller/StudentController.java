package edu.kh.demo.controller;


import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;


@RequestMapping("student")
public class StudentController {
	
	@PostMapping("select")
	public String selectStudent(HttpServletRequest req, @ModelAttribute Student student) {
	
		//post로 form에서 입력한 값 받아와야 함
		String name = req.getParameter("name");
		int age = Integer.parseInt(req.getParameter("age"));
		String address = req.getParameter("addr");
	
		student.setStdName(name);
		student.setStdAge(age);
		student.setStdAddress(address);
		
		req.setAttribute("stdName", student.getStdName());
		req.setAttribute("stdAge", student.getStdAge());
		req.setAttribute("stdAddress", student.getStdAddress());
		
		return "student/select";
	
	}

//... 이하 생략


}
