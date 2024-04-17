package edu.kh.project.myPage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


//@Builder : 빌더 패턴을 이용해 객체 생성 및 초기화를 쉽게 진행함
// -> 기본 생성자가 생성되지 않음 -> @NoArgs... 선언해줘야 한다


@Getter
@Setter
@NoArgsConstructor //기본 생성자 - mybatis의 조회 결과를 담을 때 기본생성자를 사용해 객체를 만든다
@AllArgsConstructor //모든 필드 초기화 (이 어노테이션 사용할 때 NoArgs..안써도 되지만 위의 내용 이유로 생성함)
@ToString
@Builder //사용은 객체 생성시에 한다
public class UploadFile {

	private int fileNo;
	private String filePath;
	private String fileOriginalName;
	private String fileRename;
	private String fileUploadDate;
	private int memberNo;
	private String memberNickname;
}
