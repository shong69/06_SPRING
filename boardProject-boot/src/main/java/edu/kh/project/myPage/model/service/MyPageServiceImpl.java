package edu.kh.project.myPage.model.service;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class) //모든 예외 발생 시 롤백 해준다. 범위 지정 없을 시 런타임 에러 발생시에만 롤백한다
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService{

	private final MyPageMapper mapper;
	
	//BCrypt 암호화 객체 의존성 주입
	private final BCryptPasswordEncoder bcrypt;

	//회원 정보 수정
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		//서비스 단에서 주소의 String배열을 db에 맞게 가공하기
		//입력된 주소가 있을 경우
		//memberAddress를 A^^^B^^^C 형태로 가공하기
		
		//주소 입력 X -> inputMember.getMemberAddress() -> ",,"가 됨
		if(inputMember.getMemberAddress().equals(",,")) {
			//주소에 null 대입
			inputMember.setMemberAddress(null);
		}else {
			//A^^^B^^^C 형태로 가공하기
			String address = String.join("^^^", memberAddress); //배열의 요소들이 구분자^^^와 함께 문자열이 됨
			
			//주소에 가공된 데이터를 대입하기
			inputMember.setMemberAddress(address);
		}
				
		return mapper.updateInfo(inputMember);
	}

	
	//비밀번호 수정
	@Override
	public int changePw(Map<String, Object> paramMap, int memberNo) {
		
		//bycrypt 사용해서 matches 로 확인하기
		//현재 로그인한 회원의 암호화한 비밀번호를 db에서 조회해오기
		String originPw = mapper.selectPw(memberNo);
		
		//암호화한 비밀번호와 입력받은 내 비밀번호가 같은지 확인하기
		
		if(!bcrypt.matches((String)paramMap.get("currentPw"), originPw)) { //다를 경우 0 리턴
			
			return 0;
		}
		
		
		//맞다면 새 비번을 암호화 하기
		String encPw = bcrypt.encode((String)paramMap.get("newPw"));
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo);
		
		
		return mapper.changePw(paramMap);
	}


	//회원 탈퇴 서비스
	@Override
	public int secession(String memberPw, int memberNo) {
		String originPw = mapper.selectPw(memberNo);
		
		if(!bcrypt.matches(memberPw, originPw)) {
			return 0;
		}
		return mapper.secession(memberNo);
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
}
