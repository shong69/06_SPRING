package edu.kh.project.member.model.service;

import java.util.List;

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

	/** 닉네임 유효성 검사
	 * @param memberNickname
	 * @return
	 */
	int checkNickname(String memberNickname);

	/** 회원 가입 서비스
	 * @param inputMember
	 * @param memberAddress
	 * @return result
	 */
	int signup(Member inputMember, String[] memberAddress);

	/** 빠른 로그인 서비스
	 * @param memberEmail
	 * @return loginMember
	 */
	Member quickLogin(String memberEmail);

	/** 회원 목록 조회
	 * @return List<Member>
	 */
	List<Member> memberList();

	/** 특정 회원 비밀번호 초기화
	 * @param memberNo
	 * @return int
	 */
	int resetPw(String memberNo);

	/**특정회원 탈퇴 복구
	 * @param memberNo
	 * @return
	 */
	int restoreMember(String memberNo);

}
