package edu.kh.project.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/* 스프링 예외 처리 방법(우선순위 별로 작성)
 * 
 * 1. 메서드에서 직접 처리 (try-cathch, throws)
 * 
 * 2. 컨트롤러 클래스에서 클래스 단위로 모아서 처리
 * 	  (@Exception Handler 어노테이션을 지닌 메서드를 작성)
 * 
 * 3. 별도 클래스를 만들어 프로젝트 단위로 모아서 처리
 *    (@ControllerAdvice 어노테이션을 지닌 클래스를 작성)
 * */

//@ControllerAdvice
// - 전역적 예외 처리


@ControllerAdvice
public class ExceptionController {
	//컨트롤러에 전역적 예외처리를 해주는 어노테이션을 붙였으므로 해당 프로젝트의 
	//모든 에러는 여기로 오게 됨
	
	//에러페이지로 연결해주면 된다
	
	
	
	
	//@ExceptionHandler(예외 종류)
	
	//예외 종류 : 메서드별로 처리할 예외를 지정
	//			ex) @ExceptionHandler(SQLException.class) - SQL 관련 예외만 처리
	//		    	@@ExceptionHandler(IOException.class) - 입출력 관련 예외만 처리
	//				@ExceptionHandler(Exception.class) - 모든 예외를 처리하겠음
	
	
	
	@ExceptionHandler(NoResourceFoundException.class) //자원을 찾을수 없다 -> 404에러 클래스
	public String notFound() {
		return "error/404";
	}
	
	//예외 처리하는 메서드에서 사용 가능한 매개변수(==Controller에서 사용하는 모든 매개변수를 작성 가능하다)
	//모든 종류의 예외를 처리하는 클래스
	@ExceptionHandler(Exception.class)
	public String allExceptionHandler(Exception e, Model model) {  //Exception e : 발생한 정보의 예외를 담고 있음
		
		e.printStackTrace(); //예외에 대한 상세정보가 뜨도록 발자국 남김
		model.addAttribute("e", e);
		
		return "error/500";
	}
}
