package edu.kh.project.myPage.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.service.MyPageService;
import lombok.RequiredArgsConstructor;

@SessionAttributes({"loginMember"}) //세션 목록에서 한번 봐야 세션의 속성을 꺼내올 수 있다
@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor //자동으로 생성자에 @Autowired 내부적으로 붙게 해줌(final인 경우에만)
public class MyPageController {
	
	private final MyPageService service;
	
	
	
	/** 내 정보 조회/수정 화면으로 전환
	 * @param loginMember : 세션에 존재하는 loginMember을 얻어와 매개변수에 대입
	 * @param model : 데이터 전달용 객체(기본 request scope)
	 * @return myPage/myPage-info로 요청 위임
	 */
	@GetMapping("info") // /myPage/info (GET)
	public String info(
				@SessionAttribute("loginMember") Member loginMember, //세션에 올라가있는 속성 
				Model model
			) { 
		//주소를 ^^^로 구분하여 넣었기 때문에 DB에 접근해서 처리 후 꺼내야 한다
	
		//주소만 꺼내오기
		String memberAddress = loginMember.getMemberAddress();
		//(주소 필수 아님) 주소가 있는 경우에만 동작하기
		if(memberAddress != null) {
			
			//구분자 ^^^를 기준으로 memberAddress 값을 쪼개 String[]로 반환하기
			String[] arr = memberAddress.split("\\^\\^\\^");  //escape문자 \\와 같이 쓰기(안쓰면 인식 안됨)
			//String[] java.lang.String.split(String regex) -> regex 정규표현식으로 쓰고 있음
			
			//"04540^^^서울 중구 남대문로 120^^^3층 E 강의장"
			//->["04540", "서울 중구 남대문로 120", "3층 E 강의장"]
			model.addAttribute("postcode", 		arr[0]);
			model.addAttribute("address", 		arr[1]);
			model.addAttribute("detailAddress", arr[2]);
		}
		
		
		
		//templates/myPage/myPage-info.html
		return "myPage/myPage-info";
	}
	

	/** 프로필 이미지 변경 화면 이동
	 * @return
	 */
	@GetMapping("profile")
	public String profile() {
	
		return "myPage/myPage-profile";
	}
	
	
	/**비밀번호 변경 화면으로 이동
	 * @return
	 */
	@GetMapping("changePw")
	public String changePw() {
		return"myPage/myPage-changePw";
	}
	
	
	
	/** 회원 탈퇴 화면 이동
	 * @return
	 */
	@GetMapping("secession")
	public String secession() {
		return"myPage/myPage-secession";
	}
	
	
	/** 회원정보 수정
	 * @param inputMember : 제출된 회원 닉네임, 전화번호, 주소(,로 구분됨)
	 * @param loginMember : 로그인한 회원의 정보(회원번호 사용할거임)
	 * @param memberAddress : 회원 주소 만 따로 받은 String[]
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달 가능함
	 * @return redirect:/info
	 */
	@PostMapping("info")
	public String updateInfo(Member inputMember,
					@SessionAttribute("loginMember") Member loginMember,
					@RequestParam("memberAddress") String[] memberAddress,
					RedirectAttributes ra
			) {
		
		
		//inputMember에 로그인한 회원의 회원번호 추가하기
		int memberNo = loginMember.getMemberNo();
		
		inputMember.setMemberNo(memberNo);
		
		//회원정보 수정 서비스 호출
		int result = service.updateInfo(inputMember, memberAddress);
		
		String message = null;
		
		if(result>0) {
			message="회원 정보 수정 성공!";
			
			//세션에 올라간 loginMember에 수정 내용 업데이트 하기
			//loginMember는 세션에 저장된 로그인한 회원정보가 저장된 객체를 참조하는 상태
			
			//-> loginMember을 수정하면 세션에 저장된 로그인한 회원 정보가 수정되는 것이다
			// == 세션과 db의 정보 동일하게
		
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress()  );
			
		}else {
			message="회원 정보 수정 실패..";
		}
		
		ra.addFlashAttribute("message", message);
		
		
		
		return"redirect:info";  //상대경로. myPage/info에서 마지막에 적힌 info만 바뀐다
	}
	



	/** 비밀번호 변경
	 * @param paramMap : 모든 파라미터를 맵으로 저장
	 * @param loginMember : 세션에 등록된 로그인한 회원의 정보
	 * @param ra : 
	 * @return
	 */
	@PostMapping("changePw")
	public String changePw(
				@RequestParam Map<String, Object> paramMap,
				@SessionAttribute("loginMember")Member loginMember,
				RedirectAttributes ra
			) {
		
		//로그인한 회원의 번호 얻어오기
		int memberNo = loginMember.getMemberNo();
		
		//현재 + 새 + 회원번호를 서비스로 전달하기
		int result = service.changePw(paramMap, memberNo);
		
		String path = null;
		String message=null;
		
		if(result>0) {
			path = "/myPage/info";
			message = "비밀번호가 변경 되었습니다";
		}else {
			path = "/myPage/changePw";
			message="현재 비밀번호가 일치하지 않습니다";
		}

		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		
	}
	
	
	
	/** 회원 탈퇴
	 * @param memberPw : 입력받은 비밀번호
	 * @param loginMember : 로그인한 회원 정보(세션)
	 * @param status : 세션 완료 용도의 객체 SessionAttributes를 관리해줌. 해당 등록된 세션을 완료시켜줄거임
	 * @param ra 
	 * @return
	 */
	@PostMapping("secession")
	public String secession(
				@RequestParam("memberPw") String memberPw,
				@SessionAttribute("loginMember") Member loginMember,
				SessionStatus status, 
				RedirectAttributes ra
			) {
		
		//서비스 호출
		int memberNo = loginMember.getMemberNo();
		
		int result = service.secession(memberPw, memberNo);
		
		String path = null;
		String message=null;
		
		if(result>0) {
			message="탈퇴 되었습니다.";
			path="/";
			
			status.setComplete();
		} else {
			message="비밀번호가 일치하지 않습니다";
			path="secession";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	
	//----------------------------------------------------------------
	
	/*파일 업로드 테스트*/
	
	@GetMapping("fileTest")
	public String fileTest() {		
		return"myPage/myPage-fileTest";
	}
	
	
	/* SPRING(서버단)에서 파일 업로드를 처리하는 방법
	 * 
	 * - enctype = "multipart/form-data"로 클라이언트 요청 시 
	 * 	 (문자, 숫자, 파일 등이 섞여있는 요청일때)
	 * 	 이를 MultipartResolver(FileConfig에 정의함)를 이용해서 섞여있는 파라미터를 분리해야 한다
	 * 
	 * 문자열/숫자 -> String으로 분리
	 * 파일 	   -> MultipartFile로 분리
	 * 
	 * */
	
	
	/**
	 * @param uploadFile : 업로드한 파일 + 파일에 대한 내용 및 설정 내용
	 * @return
	 */
	@PostMapping("file/test1")
	public String fileUpload1(//@RequestParam("memberName") String memberName,
							  @RequestParam("uploadFile") MultipartFile uploadFile,
							  RedirectAttributes ra) {
		
		String path;
		try {
			path = service.fileUpload1(uploadFile);
			

			//파일이 저장돼서 웹에서 접근할 수 있는 경로가 반환되었을 때(null이 아닐때)
			if(path != null) {
				ra.addFlashAttribute("path", path);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		return "redirect:/myPage/fileTest";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
