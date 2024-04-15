package edu.kh.project.email.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.email.model.service.EmailService;
import lombok.RequiredArgsConstructor;



@Controller
@RequestMapping("email")
@RequiredArgsConstructor //final필드이나 @NotNull 필드에 자동으로 의존성 주입 (@Autowired 생성자 방식 코드 자동 완성)
public class EmailController {
	
	
	private final EmailService service;
	
	@ResponseBody
	@PostMapping("signup")
	public int signup(@RequestBody String email) {
		String authKey = service.sendEmail("signup", email);
		
		if(authKey != null) { //인증번호가 반환되서 돌아옴 == 이메일 보내기 성공
			return 1;
			
		}
		
		//이메일 보내기 실패
		return 0; 
	}
	
	@ResponseBody
	@PostMapping("checkAuthKey")
	public int checkAuthKey(@RequestBody Map<String, Object> map) {
		
		//입력받은 이메일, 인증번호가 DB에 있는지 조회
		//이메일, 인증번호가 둘다 유효할 경우 ==1
		//아니면 0 반환

		return service.checkAuthKey(map);
	}
	
	
	
	
}

/* @Autowierd를 이용한 의존성 주입 방법은 3가지 존재
 *  
 *  1) 필드
 *  2) setter
 *  3) 생성자(권장)
 *  
 * Lombok 라이브러리에서 제공하는 
 * @RequiredArgsConstructor 를 이용하면
 * 
 * 필드 중
 * 1) 초기화 되지 않는 final이 붙은 필드
 * 2) 초기화 되지 않은 @NotNull이 붙은 필드
 * 
 * 1,2에 해당하는 필드에 대한 
 * @Autowired 생성자 구문을 자동 완성
 * 
 * 
 * 
 * 
 * 
 * */
 
//1)필드에 의존성 주입하는 방법(권장X)
//@Autowired //의존성 주입DI
//private EmailService service;

//2)setter에 의존성을 주입
//private EmailService service;

//Autowired
//public void setService(EmailService service){
// this.service = service;
//}

//3) 생성자에 의존성 주입
// private EmailService service;
// private MemberService service2;

//@Autowired
//public EmailController(){
//  this.service = service;
//  this.service2 = service2;
//}
