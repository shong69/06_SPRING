package edu.kh.project.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


/* @SessionAttributes({"key","key",..})
 *  - Model에 추가된 속성 중
 *    key값이 일치하는 속성을 Session scope로 변경해준다 
 * */


@SessionAttributes({"loginMember"}) //loginMember 모델 속성을 세션에서 유지해준다
@Controller
@Slf4j
@RequestMapping("member")
public class MemberController {

	@Autowired //Bean 중에 관리되도록 자동 주입(DI, dependency injection) -> @Service 등록이 돼야 인식 가능함
	private MemberService service; 
	
	/* [로그인]
	 * - 특정 사이트에 아이디/비밀번호 등을 입력해서
	 * 	 해당 정보가 있으면 조회/서비스 이용
	 * 
	 * - 로그인 한 정보를 session에 기록하여
	 *   로그아웃 또는 브라우저 종료 시까지
	 *   해당 정보를 계속 이용할 수 있게 함
	 *     
	 * */
	
	/** 로그인
	 * @param inputMember : 커맨드 객체 (@ModelAttribute 생략)(memberEmail, memberPw 세팅된 상태)
	 * @param ra : 리다이렉트 시 request scope로 데이터를 전달하는 객체
	 * @param model : 데이터 전달용 객체(기본 request scope)
	 * @return "redirect:/"
	 */
	@PostMapping("login")
	public String login(Member inputMember, 
						RedirectAttributes ra,
						Model model,
						@RequestParam(value="saveId", required=false) String saveId,
						HttpServletResponse resp
						) {
		//체크박스
		// - 체크가 된 경우 : "on"
		// - 체크가 안 된 경우 : null
		
		//로그인 서비스 호출
		Member loginMember = service.login(inputMember);
		
		//로그인 실패 시 
		if(loginMember==null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다");
		}
		
		//로그인 성공 시 
		if(loginMember != null) {
			//Session scope에 loginMember올려주자
			model.addAttribute("loginMember", loginMember);  //1단계 : request scope에 세팅됨
			
			
			//2단계 : 클래스 위에 SessionAttributes()어노테이션 때문에 Session scope로 이동됨
			
			
			// *************************************
			
			// 로그인 성공 후 아이디 저장(Cookie 받아서)
			// 쿠키 객체 생성(K:V)
			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail()); //쿠키에 이메일 저장하기
			// saveId = user01@kh.or.kr
			
			//클라이언트가 어떤 요청을 할 때 쿠키가 첨부될 지 지정하기
			
			//ex) "/" : IP 또는 domain, localhost라는 뜻
			// 			"/" 뒤에 또 "/" -->mainPage + 하위의 주소 모두를 뜻함
			cookie.setPath("/"); //메인페이지 하위의 모든 페이지의 요청에서 쿠키가 첨부된다.
			
			
			//만료 기간 지정
			// 아이디를 저장하기로 했는지 지정한 saveId 값 확인해서 지정하기
			if(saveId != null) { //아이디 저장 체크 시
				cookie.setMaxAge(60 * 60 * 24 * 30); //30일 (초단위로 만료기간 지정된다)
			}else { //미체크 시
				//기존의 지정됐던 쿠기 기간을 삭제
				cookie.setMaxAge(0); //클라이언트 쿠키 삭제
			}
			
			// 응답 객체에 쿠키 추가 -> 클라이언트로 전달함
			resp.addCookie(cookie);
		}
		
		return"redirect:/"; //메인페이지 재요청
	}
	
	
	
	
	
	/** 빠른 로그인 버튼
	 * @param memberEmail
	 * @param ra
	 * @param model
	 * @return redirect:/
	 */
	@GetMapping("quickLogin")
	public String shortcut(@RequestParam("memberEmail") String memberEmail,
						RedirectAttributes ra,
						Model model) {
	
		Member loginMember = service.quickLogin(memberEmail);
		
		if(loginMember == null) {
			ra.addFlashAttribute("message", "해당 이메일이 존재하지 않습니다.");
			
		}else {
			model.addAttribute("loginMember", loginMember);
		}
		
		return "redirect:/";
		
	}
	
	
	/** 회원 목록 조회
	 * @param ra
	 * @return memberList
	 */
	@ResponseBody
	@GetMapping("memberList")
	public List<Member> memberList(RedirectAttributes ra) {
		
		List<Member> memberList = service.memberList();
		
		if(memberList==null) {
			ra.addFlashAttribute("message", "멤버가 존재하지 않습니다.");
			return null;
		}
			
		
		//HttpMessageConverter가 JSONArray로(문자열)로 자동 변경 가능하기 때문에
		//List<Member>선언을 하지 않아도 된다.
		
		
		return memberList;
	}
	
	
	
	
	
	
	
	
	/** 로그아웃 : Session에 저장된 로그인된 회원의 정보를 없애는 것(만료/무효화 등)
	 * 
	 * @param SessionStatus : 세션을 완료(없앰) 시키는 역할의 객체
	 * 					- @SessionAttributes로 등록된 세션을 만료시킨다
	 * 					- 서버에서 기존 세션 객체가 사라짐과 동시에 새로운 세션 객체가 생성되어
	 * 					  클라이언트와 연결해준다
	 * @return redirect:/
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		status.setComplete(); //세션 완료시킴(없앰)
		return "redirect:/"; //메인 페이지 리다이렉트
	
	}
	
	
	
	/** 회원가입페이지 이동 메서드
	 * @return member/signup
	 */
	@GetMapping("signup")
	public String signupPage() {
		
		return "member/signup";
	}
	
	
	/** 이메일 중복 검사
	 * @param memberEmail
	 * @return
	 */
	@ResponseBody //응답 본문으로 요청 fetch로 돌려보내주기
	@GetMapping("checkEmail")
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		
		return service.checkEmail(memberEmail);
	}
	
	
	/**닉네임 중복 검사
	 * @param memberNickname
	 * @return 중복되면 1, 아니면 0
	 */
	@ResponseBody //비동기 요청한 쪽으로 돌아간다
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		
		return service.checkNickname(memberNickname);
	}
	
	
	/** 회원가입
	 * @param inputMember : 입력된 회원정보(memberEmail, memberPw,
	 * 		memberNickname, memberTel,(memberAddress->따로 받아서 처리)
	 * @param memberAddress 입력한 주소 input 3개의 값을 배열로 전달[우편번호, 도로명/지번주소, 상세주소]
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달하는객체
	 * @return 
	 */
	@PostMapping("signup")
	public String signup(Member inputMember,
		@RequestParam("memberAddress") String[] memberAddress,
		RedirectAttributes ra) {
		
		//회원 가입 서비스 호출
		int  result = service.signup(inputMember, memberAddress);
		
		
		String path = null;
		String message = null;

		if(result>0) { //회원가입 성공
			
			message = inputMember.getMemberNickname() + "님의 가입을 환영합니다. :)";
			path="/";
			
		}else { //회원가입 실패
			
			message = "회원 가입 실패..";
			path="signup";
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
		
	}
	
	/** 특정회원 비밀번호 초기화
	 * @param memberNo
	 * @return
	 */
	@ResponseBody
	@GetMapping("resetPw")
	public int resetPw(@RequestParam("memberNo") String memberNo) {
		
		return service.resetPw(memberNo);
	}
	
	/** 특정회원 탈퇴 복구
	 * @param memberNo
	 * @return
	 */
	@GetMapping
	@ResponseBody
	public int restoreMember(@RequestParam("memberNo") String memberNo) {
		
		return service.restoreMember(memberNo);
	}

	
}
