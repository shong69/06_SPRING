package edu.kh.project.myPage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class) //모든 예외 발생 시 롤백 해준다. 범위 지정 없을 시 런타임 에러 발생시에만 롤백한다
@RequiredArgsConstructor
@PropertySource("classpath:/config.properties")  //config.properties의 변수 참조한다는 의미
public class MyPageServiceImpl implements MyPageService{

	private final MyPageMapper mapper;
	
	//BCrypt 암호화 객체 의존성 주입
	private final BCryptPasswordEncoder bcrypt;
	
	@Value("${my.profile.web-path}")
	private String profileWebPath;
	
	@Value("${my.profile.folder-path}")
	private String profileFolderPath;

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


	//파일 업로드 테스트1
	@Override
	public String fileUpload1(MultipartFile uploadFile) throws Exception {
		
		//MultipartFile이 제공하는 메서드
		// - getSize() : 파일의 크기
		// - isEmpty() : 업로드한 파일이 없을 경우 true를 반홚
		// - getOriginalFileName() : 원본 파일명
		// - transferTo(경로작성) : 
		//	 메모리 또는 임시저장 경로에 업로드된 파일을 원하는 경로에 전송하는 일(서버의 어떤 폴더에 저장할 지 지정)
		//실제로 db에 우리가 올린 이미지에 대한 정보가 잘 올라갔으면 진짜로 올려주는 곳임
		
		
		//업로드한 파일이 없을 경우
		if(uploadFile.isEmpty()) { 
			return null;
		}
		
		
		
		//업로드 한 파일이 있을 경우
		
		//C:/uploadFiles/test/파일명 으로 서버에 저장
		uploadFile.transferTo(
					new File("C:\\uploadFiles\\test\\"+uploadFile.getOriginalFilename())); //파일 객체 생성후 경로 작성해야 함
				
		
		
		//웹에서 해당 파일에 접근할 수 있는 경로를 반환해줄거임

		//서버 : C:\\uploadFiles\\test\\a.jpg 
		//웹 접근 주소 : /myPage/file/a.jpg - --> 이 둘이 같도록 지정해 줄거임
		
		
		return "/myPage/file/" + uploadFile.getOriginalFilename ();
	}


	//파일 업로드 테스트2(+DB)
	@Override
	public int fileUpload2(MultipartFile uploadFile, int memberNo) throws IOException {

		//업로드 된 파일이 없다면
		// ==선택된 파일이 없을 경우
		if(uploadFile.isEmpty()) {
			return 0;
		}
		
		/* BLOB : DB의 파일 유형
		 * -> DB에 파일 저장이 가능은 하지만 부하를 줄이기 위해
		 * 
		 * 1) DB에는 서버에 저장할 파일 경로를 저장한다
		 * 
		 * 2) DB 삽입/수정 성공 후 서버에 파일을 저장함
		 * 
		 * 3) 만약 파일 저장 실패 시 -> 예외 발생
		 *   -> @Transactional을 이용해서 rollback 수행함
		 * 
		 * 
		 * */
		//1. 서버에 저장할 파일 경로 만들기
		
		// 파일이 저장될 서버 폴더 경로 작성
		String folderPath = "C:\\uploadFiles\\test\\";
		
		//클라이언트가 접근할 수 있는 주소도 작성 (+ fileConfig에서 레지스트리 등록이 되어 있어서 사용 가능함)
		String webPath = "/myPage/file/";
		
		//2. DB에 전달할 데이터를 DTO로 묶어서 INSERT 호출하기
		//webPath, memberNo, 원본 파일명, 변경된 파일명
		String fileRename = Utility.fileRename(uploadFile.getOriginalFilename());
		//getOriginalFilename() : 
		//	Return the original filename in the client's filesystem with String format. 

		
//		UploadFile uf = new UploadFile();
//		uf.setMemberNo(memberNo);
//		uf.setFilePath(webPath);
//		uf.setFileOriginalName(uploadFile.getOriginalFilename());
//		uf.setFileRename(fileRename); -> 이거 말고 builder 패턴 사용해서 설정하기도 가능함

		//Builder 패턴을 이용하기 -> upload 파일 객체를 생성 가능
		//장점 1. 반복되는 참조변수명, set구문을 생략할 수 있음
		//장점 2. method chaining을 이용해서 한 줄로 작성 가능함
		
		UploadFile uf = UploadFile.builder()
						.memberNo(memberNo)
						.filePath(webPath)
						.fileOriginalName(uploadFile.getOriginalFilename())
						.fileRename(fileRename)
						.build();
		
		//uf 안에 있는 내용을 mapper에 넣기
		int result = mapper.insertUploadFile(uf);
		
		
		//3. 삽입(INSERT) 성공 시 파일을 지정된 서버 폴더에 저장하기
		
		//삽입 실패 시
		if(result==0) return 0;
		
		//삽입성공시
		//C:\\uploadFiles\\test\\변경한파일명으로 파일을 서버컴퓨터에 저장하기
		uploadFile.transferTo(new File(folderPath + fileRename)); //new File() 안에 경로를 적어야 함
								//20240417102705_00004.jpg 
		
		
		// -> IOException ==CheckedException 발생 -> 예외 처리 필수
		
		//@Transactional은 런타임에러(Unchecked Exception의 대표임)만 잡기 때문에 
		//(rollbackFor = Exception.class) 적어줘야 함(checked exception 도 롤백 하도록)
		return result; //1리턴
	}


	//파일 목록 조회
	@Override
	public List<UploadFile> fileList() {
		
		return mapper.fileList();
	}


	//여러 파일 업로드
	@Override
	public int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo) throws Exception {
		
		//1. aaaList 처리하기
		int result1 = 0; 
		
		//업로드 된 파일이 없을 경우를 제외하고 업로드하기
		//n개의 input중 제출하지 않은 것이 있는 경우 , 해당 input을 제외하고 DB에 추가할 것임
		for(MultipartFile file : aaaList) {
			
			if(file.isEmpty()) { //현재 접근하고 있는 파일이 없으면 다음 파일로 넘어감
				continue;
				
			}
			//fileUpload2 메서드 호출하기(재활용)
			//->파일 하나를 업로드 + DB에 INSERT
			result1 +=fileUpload2(file, memberNo); //성공한 행의 개수 n개 (==1개 올리니까 1) 혹은 0개 돌아옴
			
		}
		
		//2. bbbList 처리하기
		int result2 = 0;
		for(MultipartFile file : bbbList) {
			
			if(file.isEmpty()) {
				continue;
			}
			result2 +=fileUpload2(file, memberNo);
		}
		
		
		
		return result1 + result2;
	}


	//프로필 이미지 변경
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws Exception {
		//수정할 경로
		String updatePath = null;
		
		//변경명 저장
		String rename = null;
		
		//업로드한 이미지가 있는지
		// - 있다면 수정할 경로를 조합하기(리네임한 파일명, 클라이언트에서 접근할 수 있는 경로)
		if(!profileImg.isEmpty()) {
			//updatePath 조합해줄 거임
			
			//1. 파일명 rename해주기
			rename = Utility.fileRename(profileImg.getOriginalFilename());
			
			//2. /myPage/profile/변경된 파일명
			updatePath = profileWebPath + rename;
		}		
			//수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체
		Member mem = Member.builder()
					.memberNo(loginMember.getMemberNo())
					.profileImg(updatePath)
					.build();
			
		int result = mapper.profile(mem);
			
		
		if(result>0) {  //DB에 수정 성공 시
			
			//프로필 이미지를 없앤 경우(null로 수정한 경우) 를 제외하기 
			// -> 업로드한 이미지가 있는 경우
			if(!profileImg.isEmpty()) {
				//파일을 서버 지정된 폴더에 저장한다
				profileImg.transferTo(new File(profileFolderPath + rename));
			}
			
			//세션 회원 정보에서 프로필 이미지 경로를 업데이트한 경로로 변경
			loginMember.setProfileImg(updatePath);
			
		}
				
		return result;
	}
	


	
	
	
	
	
	
	
	
	
}
