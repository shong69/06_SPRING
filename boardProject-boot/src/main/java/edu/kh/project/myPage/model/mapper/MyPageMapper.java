package edu.kh.project.myPage.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper //마이바티스 제공 매퍼 인터페이스 -> 인터페이스에서만 작동한다
public interface MyPageMapper {

	
	/** 회원 정보 수정
	 * @param inputMember
	 * @return result
	 */
	int updateInfo(Member inputMember);

	/** 암호화된 비밀번호 db 조회하기
	 * @param memberNo
	 * @return 암호화된 비밀번호
	 */
	String selectPw(int memberNo);

	/**암호화한 새 비밀번호로 수정하기
	 * @param paramMap
	 * @return
	 */
	int changePw(Map<String, Object> paramMap);

	/** 회원 탈퇴
	 * @param memberNo
	 * @return 
	 */
	int secession(int memberNo);

}
