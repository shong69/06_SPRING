package edu.kh.project.board.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.Exception.ImageDeleteException;
import edu.kh.project.board.model.Exception.ImageUpdateException;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.util.Utility;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class EditBoardServiceImpl implements EditBoardService{

	private final EditBoardMapper mapper;

	
	
	//게시글 수정
	@Override
	public int boardUpdate(Board inputBoard, List<MultipartFile> images, String deleteOrder) {
		
		// 1. 게시글 (제목/내용) 부분 수정
		int result  = mapper.boardUpdate(inputBoard);
		
		//수정 실패 시 return 
		if(result ==0) {
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
				
				// 수정 또는 삭제가 실패한 경우
				if(result == 0) {
					throw new ImageUpdateException(); //사용자 정의 예외 -> 롤백
				}
				
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
