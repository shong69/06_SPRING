package edu.kh.project.member.model.service;

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
		//log.debug("result : "+result);
		
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
