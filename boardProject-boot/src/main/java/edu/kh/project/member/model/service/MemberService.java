package edu.kh.project.member.model.service;

import edu.kh.project.member.model.dto.Member;

public interface MemberService {

	/** 로그인 서비스 기능
	 * @param inputMember
	 * @return loginMemberp
	 */
	Member login(Member inputMember);

	/** 이메일 유효성 검사 
	 * @param memberEmail
	 * @return int
	 */
	int checkEmail(String memberEmail);

}
