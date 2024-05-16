package edu.kh.project.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.Exception.BoardInsertException;
import edu.kh.project.board.model.Exception.ImageDeleteException;
import edu.kh.project.board.model.Exception.ImageUpdateException;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.util.Utility;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
@PropertySource("classpath:/config.properties")  // PropertySource: properties파일을 사용할 수 있게 해줌
public class EditBoardServiceImpl implements EditBoardService{
	
	private final EditBoardMapper mapper;

	@Value("${my.board.web-path}")
	private String webPath;   //  /images/board/
	
	@Value("${my.board.folder-path}")
	private String folderpath;  // /C:/uploadFiles/board/
	
	
	//게시글 작성
	@Override
	public int boardInsert(Board inputBoard, List<MultipartFile> images) throws IllegalStateException, IOException{
		
		// 1. 게시글 부분에 먼저 Board 테이블 insert하기 
		// -> INSERT 결과로 작성된 게시글 번호 (생성된 시퀀스 번호) 반환 받기
		int result = mapper.boardInsert(inputBoard);
		
		//Controller : 주소만 -> service : 주소만 -> mapper.xml : 주소만
		//-> inputBoard에 생성된 시퀀스 번호가 저장된 상태
		
		//삽입 실패 시 
		if(result==0) return 0;
		
		//삽입된 게시글의 번호를 변수로 저장하기
		//->mapper.xml에서 <selectkey>태그를 이용해서 생성된 baordNo가 inputBoard에 저장된 상태(얕은 복사)
		int boardNo = inputBoard.getBoardNo();
		
		//2. 업로드된 이미지가 실제로 존재할 경우
		// 업로드된 이미지만 별도로 저장하여
		//"BOARD_IMG" 테이블에 삽입하는 코드 작성
		// 실제 업로드된 이미지의 정보를 모아둘 list 생성
		List<BoardImg> uploadList = new ArrayList<>();
		
		//images 리스트에서 하나씩 꺼내서 선택된 파일이 있는지 검사하기
		for(int i= 0; i<images.size(); i++) {
			
			//실제로 선택된 파일이 존재하는 경우
			if(!images.get(i).isEmpty()) {
				
				//원본명
				String originalName = images.get(i).getOriginalFilename();
				
				//변경명
				String rename = Utility.fileRename(originalName);
				
				
				//모든 값을 저장할 DTO 생성(BoardImg - Builder 패턴 사용)
				BoardImg img = BoardImg.builder()
							.imgOriginalName(originalName)
							.imgRename(rename)
							.imgPath(webPath)
							.boardNo(boardNo)
							.imgOrder(i)
							.uploadFile(images.get(i))
							.build();
				
				
				uploadList.add(img);
			}
		}
		
		//업로드한 사진리스트에 선택한 파일이 없을 경우
		if(uploadList.isEmpty()) {
			return boardNo;
		}
		
		//선택할 파일이 존재할 경우
		// -> "BOARD_IMG" 테이블에 INSERT + 서버에 파일 저장
		
		//result == 삽입된 행의 개수 == uploadList.size()
		result = mapper.insertUploadList(uploadList);
		
		// 다중 INSERT 성공 확인(uploasdList에 저장된 값이 모두 정상 삽입되었는지)
		if(result == uploadList.size()) {
			for(BoardImg img : uploadList) {
				img.getUploadFile().transferTo(new File(folderpath+img.getImgRename()));
				//new File : 특정 경로를 가지는 File 객체 생성
				//transferTo(경로) : 지정한 경로에 파일 저장해줌
			}
		}else {
			//전체 개수 중 부분적으로 삽입을 실패한 경우 -> 전체 서비스 실패로 판단함
			// -> 이전에 삽입된 내용 모두 rollback
			
			//rollback하는 방법 -> RuntimeException 강제발생 시키면 @Transactional이 롤백해줌
			throw new BoardInsertException("이미지가 정상 삽입되지 않음");
	
		}
		
		
		
		
	
		
		return boardNo;
	}
	
	
	//게시글 수정
	@Override
	public int boardUpdate(Board inputBoard, List<MultipartFile> images, String deleteOrder) throws IllegalStateException, IOException {
		
		// 1. 게시글 (제목/내용) 부분 수정
		int result  = mapper.boardUpdate(inputBoard);
		
		//수정 실패 시 return 
		if(result == 0) {
			return 0;
		}
		//---------------------------------------------------
		
		// 2. 기존 0 -> 삭제된 이미지(delete order)가 있는 경우
		if(deleteOrder != null && !deleteOrder.equals("")) { //null도 아니고 비어있지도 않으면
			Map<String, Object> map = new HashMap<>();
			
			map.put("deleteOrder", deleteOrder);
			map.put("boardNo", inputBoard.getBoardNo());
			
			result = mapper.deleteImage(map); //성공한 행의 개수 혹은 0
			
			//삭제 실패한 경우(부분 실패 포함) -> 롤백
			if(result == 0) {
				throw new ImageDeleteException(); //강제 예외 발생 시키기-사용자정의 예외 작성함
			}
			
		}
		
		// 3. 선택한 파일이 존재할 경우
		//    해당 파일 정도만 모아두는 List 생성
		
		
		//실제 이미지 리스트에 존재하는 것만 뽑아서 새로운 꽉 찬 리스트를 만든것
		List<BoardImg> uploadList  = new ArrayList<>();
		
		for(int i = 0 ; i<images.size();i++) {
			
			if(!images.get(i).isEmpty()) {
				String originalName = images.get(i).getOriginalFilename();
				String rename = Utility.fileRename(originalName);
				
				BoardImg img = BoardImg.builder()
						.imgOriginalName(originalName)
						.imgRename(rename)
						.imgPath(webPath)
						.boardNo(inputBoard.getBoardNo())
						.imgOrder(i)
						.uploadFile(images.get(i))
						.build();
				uploadList.add(img);
				
				// 4. img 객체를 리스트 추가한 다음 
				// 업로드 하려는 이미지 정보(img)를 이용해서 수정 또는 삽입을 수행하기
				
				//1)기존 0 -> 새 이미지로 변경 ==수정하기
				result = mapper.updateImage(img);
				
				if(result == 0) {
					//수정 실패 == 기존 해당 순서(IMG_ORDER)에 이미지가 없었음
					//-> img를 새로 삽입해야 함
					
					// 2) 기존 X -> 새 이미지 추가
					result = mapper.insertImage(img);
				}
			}
			// 수정 또는 삭제가 실패한 경우
			if(result == 0) {
				throw new ImageUpdateException(); //사용자 정의 예외 -> 롤백
			}
				
		}
		
		
		//선택한 파일이 없는 경우
		if(uploadList.isEmpty()) {
			 return result;
		}
		
		//수정, 새 이미지 파일을 서버에 저장하기
		for(BoardImg img : uploadList) {
			img.getUploadFile().transferTo(new File(folderpath + img.getImgRename()));
		}
		
		
		return result;
	}



	
}
