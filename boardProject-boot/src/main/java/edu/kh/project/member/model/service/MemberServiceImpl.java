package edu.kh.project.member.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor=Exception.class)  
				//해당 클래스 메서드 종료 시까지 
				//- 예외(RuntimeException)가 발생하지 않으면 commit 
				//- 예외(RuntimeException)가 발생하면 rollback 해줌
@Service //비즈니스 로직 처리 명시 + Bean 등록
@Slf4j
public class MemberServiceImpl implements MemberService {

	//등록된 Bean 중에서 같은 타입/상속관계인 Bean을 자동으로
	//의존성 주입(DI)해줌
	@Autowired
	private MemberMapper mapper;

	
	//BCrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	
	//로그인 서비스
	@Override
	public Member login(Member inputMember) {
		//테스트
		
		//bcrypt.encode(문자열(평문 비밀번호) : 문자열을 암호화하여 반환하기
		//String bcryptPassword = bcrypt.encode(inputMember.getMemberPw()); 
		//log.debug("bcryptPassword : "+bcryptPassword);
		
		//boolean result = bcrypt.matches(inputMember.getMemberPw(), bcryptPassword);

		
		//1. 이메일이 일치하면서 탈퇴하지 않은 회원 조회
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		//2. 만약 일치하는 이메일이 없어서 조회 결과가 null인 경우 
		if(loginMember==null) return null;
		
		//3. 입력 받은 비밀번호(inputMember.getMemebrPw() 평문)과
		//암호화된 비밀번호(loginmember.getmemberPw()암호문)가 일치하는지 확인하기
		
		//일치하지 않는 경우로 확인하기
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}
		
		//로그인 결과에서 비밀번호 제거하기
		loginMember.setMemberPw(null);
		

		return loginMember;
	}



	//이메일 중복 검사
	@Override
	public int checkEmail(String memberEmail) {
		
		return mapper.checkEmail(memberEmail);
	}


	//닉네임 중복 검사
	@Override
	public int checkNickname(String memberNickname) {
		return mapper.checkNickname(memberNickname);
	}


	//회원 가입 서비스
	@Override
	public int signup(Member inputMember, String[] memberAddress) {
		
		//주소가 입력되지 않으면
		// inputMember.getMemberAddress() -> ",,"(도로명 주소, 상세주소)
		// memberAddress -> [,,]
		
		
	
		//주소가 입력된 경우
		if(!inputMember.getMemberAddress().equals(",,")) {
			String address= String.join("^^^",memberAddress);
			
			//String.join("구분자" , 배열)
			/// -> 배열의 모든 요소 사이에 '구분자'를 추가하여 하나의 문자열을 만들어 반환하는 메서드
			
			//구분자로 "^^^"를 쓴 이유 : 
			//-> 주소, 상세주소에 없는 특수문자 작성
			//->나중에 다시 3분할 할 때 구분자로 이용할 예정
			
			
			//inputMember  주소로 합쳐진 주소를 세팅
			inputMember.setMemberAddress(address);
		}else{ //주소 입력 안된 경우
			inputMember.setMemberAddress(null); //null 저장
			
		}
		
		// 이메일, 비밀번호(pass02!), 닉네임, 전화번호, 주소
		// 비밀번호를 암호화하여 inputMember에 세팅하기
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		
		inputMember.setMemberPw(encPw);
		
		//회원가입 mapper 메서드 호출하기
		
		
		
		
		
		
		
		
		return mapper.signup(inputMember);
	}


	//빠른 로그인
	// -> 일반 로그인에서 비밀번호 비교만 제외함
	@Override
	public Member quickLogin(String memberEmail) {
		
		Member loginMember = mapper.login(memberEmail);
		
		if(loginMember== null) {
			return null;
		}
		//조회된 로그인 멤버의 암호화비밀번호를 null로 변경하기
		loginMember.setMemberPw(null);
		
		return loginMember;
	}



	//회원 목록 조회
	@Override
	public List<Member> memberList() {
		List<Member> memberList = mapper.memberList();
		
		if(memberList==null) {
			return null;
		}
		return memberList;
	}



	//비밀번호 초기화
	@Override
	public int resetPw(String memberNo) {
		
		String memberPw = bcrypt.encode("pass01!");
		Map<String, String> map = new HashMap<>();
		map.put("memberNo", memberNo);
		map.put("memberPw", memberPw);
		return mapper.resetPw(map);
	}


	//탈퇴 복구
	@Override
	public int restoreMember(String memberNo) {
		
		
		//복구한 경우 1
		//아닌 경우 0 -> 탈퇴 안 한 경우+없는 멤버 넘버인 경우
		return mapper.restoreMember(memberNo);
	}
	
	
	/* Bcrypt 암호화 (Spring Security 제공)
	 * 
	 * - 입력된 문자열(비번)에가다가 salt 추가 후 암호화
	 * - A 회원 : 1234 -> $12!asdfg
	 * 	 B 회원 : 1234 -> $df90od
	 * 
	 * - 비밀번호 확인 방법
	 * -> ByCrptPasswordEncoder.mathces(평문 비밀번호, 암호화된 비밀번호)
	 * 	->평문비밀번호와 암호화된 비밀번호가 같은 경우 true 아니면 false 반환
	 * 
	 * *로그인, 비밀번호 변경, 탈퇴 등 비밀번호가 입력되는 경우
	 * 	DB에 저장된 암호화된 비밀번호를 조회해서
	 *  matches() 메서드로 비교해야 한다
	 * 
	 * 
	 * 
	 * (참고)
	 * sha 방식 암호화
	 * - A 회원 : 1234 -> $abcd
	 * 	 B 회원 : 1234 -> $abcd (암호화 시 변경된 내용이 같음)
	 * 
	 * */

	
}
