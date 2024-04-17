package edu.kh.project.myPage.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;

public interface MyPageService {

	/** 회원 정보 수정 서비스
	 * @param inputMember
	 * @param memberAddress
	 * @return result 
	 */
	int updateInfo(Member inputMember, String[] memberAddress);

	/** 비밀번호 변경 서비스
	 * @param paramMap
	 * @param memberNo
	 * @return result
	 */
	int changePw(Map<String, Object> paramMap, int memberNo);

	/** 회원 탈퇴 서비스
	 * @param memberPw 
	 * @param memberNo
	 * @return
	 */
	int secession(String memberPw, int memberNo);

	/** 파일 업로드 테스트1
	 * @param uploadFile
	 * @return path
	 * @throws Exception 
	 */
	String fileUpload1(MultipartFile uploadFile) throws Exception;

	/** 파일 업로드 테스트2
	 * @param uploadFile
	 * @param memberNo
	 * @return
	 * @throws IOException 
	 */
	int fileUpload2(MultipartFile uploadFile, int memberNo) throws IOException;

	/** 파일 목록 조회
	 * @return list
	 */
	List<UploadFile> fileList();

	/** 파일 업로드 테스트3(다중 업로드)
	 * @param aaaList
	 * @param bbbList
	 * @param memberNo
	 * @return
	 * @throws Exception 
	 */
	int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo) throws Exception;

	/** 프로필 이미지 변경
	 * @param profileImg
	 * @param loginMember
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws Exception 
	 */
	int profile(MultipartFile profileImg, Member loginMember) throws Exception;

}
