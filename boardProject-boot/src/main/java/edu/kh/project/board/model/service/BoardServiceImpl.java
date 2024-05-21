package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.board.model.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor //필드에 final 붙인걸로 생성자에 @autowired 붙인것과 같은 효과를 내게 해줌
public class BoardServiceImpl implements BoardService{

	private final BoardMapper mapper;

	//게시판 종류 조회
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		return mapper.selectBoardTypeList();
	}

	//특정 게시판에 지정된 페이지 목록을 조회
	@Override
	public Map<String, Object> selectBoardList(int boardCode, int cp) {
		
		// 1. 지정된 게시판(boardCode)에서 
		//    삭제되지 않은 전체 게시글 수를 조회하기 ->페이지네이션 만들기 위해
		int listCount  = mapper.getListCount(boardCode);		
		
		// 2. 1번의 결과 + cp를 이용해서 
		// pagination 객체를 생성하기
		// * Pagination 객체 : 게시글 목록 구성에 필요한 값을 저장한 객체
		Pagination pagination = new Pagination(cp, listCount);
		
		
		// 3. 특정 게시판의 지정된 페이지 목록을 조회하기
		/* ROWBOUNDS 객체(Mybatis 제공 객체)
		 * - 지정된 크기 만큼 건너뛰기(offset)
		 *   제한된 크기(limit)만큼의 행을 조회하는 객체
		 * 	 
		 * -->페이징 처리가 굉장히 간단해짐!
		 * 
		 * 
		 * */
		
		int limit =  pagination.getLimit();
		int offset = (cp-1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		/* Mapper 메서드 호출 시
		 *  - 첫번째 매개변수 -> sql에 전달할 파라미터
		 *  - 두번째 매개변수 -> 두번째부터는 무조건 rouBounds객체 자리임
		 * */
		List<Board> boardList  = mapper.selectBoardList(boardCode, rowBounds);
		
		
		// 4. 목록 조회 결과 + pagination 객체를 map으로 묶어서 결과로 반환
		Map<String , Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		
		
		
		// 5. 결과 반환
		return map;
		
		
		
	}

	//게시글 상세 조회
	@Override
	public Board selectOne(Map<String, Integer> map) {
		
		//여러 SQL을 실행하는 방법
		// 1. 하나의 Service 메서드에서 여러 mapper 메서드 호출하기
		// 2. 수행하려는 SQL이 
		//	  1) 모두 select이고
		//    2) 먼저 조회된 결과 중 일부를 이용해서 나중에 수행되는 SQL의 조건으로 삼을 수 있을 때
		//  -> Mybatis의 동적SQL을 사용 
		//  -> <resultMap>, <collection> 태그를 이용해 Mapper 메서드 1회 호출로 여러 select 한번에 수행 가능함
		log.debug("test : "+mapper.selectOne(map));
		return mapper.selectOne(map);
	}

	//게시글 좋아요 체크/해제
	@Override
	public int boardLike(Map<String, Integer> map) {
		
		int result = 0;
		
		//1. 좋아요 체크된 상태(체크됐으면 likeCheck==1)
		//-> BOARD_LIKE 테이블에 DELETE 하기
		
		if(map.get("likeCheck")==1) {
			
			result = mapper.deleteBoardLike(map);
			
		}else {
			//2. 좋아요가 해제된 상태인 경우 (likeCheck == 0)
			// -> BOARD_LIKE 테이블에 INSERT하기
			
			result = mapper.insertBoardLike(map);
		}
		
		//3. 다시 해당 게시글의 좋아요 개수를 조회해서 반환하기
		if(result >0) {
			return mapper.selectLikeCount(map.get("boardNo"));
		}
		
		//실패한 경우 -1 리턴
		return -1;
	}

	//조회수 증가
	@Override
	public int updateReadCount(int boardNo) {
		
		// 1. 조회수를 1 증가시키는 mapper 호출
		int result = mapper.updateReadCount(boardNo);
		
		//2. 증가한 현재 조회수를 다시 조회
		if(result > 0) {
			return mapper.selectReadCount(boardNo);
		}
		
		return -1; //실패한 경우 -1 반환
	}

	//게시글 검색 서비스 (게시글 목록 조회 참고하기)
	@Override
	public Map<String, Object> searchList(Map<String, Object> paramMap, int cp) {
	
		//paramMap (key, query, boardCode)
		
		// 1. 지정된 게시판(boardCode)에서
		//	  검색 조건에 맞으면서
		//    삭제되지 않은 전체 게시글 수를 조회하기 ->페이지네이션 만들기 위해
		int listCount  = mapper.getSearchCount(paramMap);
		
		// 2. 1번의 결과 + cp를 이용해서 
		// pagination 객체를 생성하기
		// * Pagination 객체 : 게시글 목록 구성에 필요한 값을 저장한 객체
		Pagination pagination = new Pagination(cp, listCount);
		
		
		// 3. 특정 게시판의 지정된 페이지 목록을 조회하기
		/* ROWBOUNDS 객체(Mybatis 제공 객체)
		 * - 지정된 크기 만큼 건너뛰기(offset)
		 *   제한된 크기(limit)만큼의 행을 조회하는 객체
		 * 	 
		 * -->페이징 처리가 굉장히 간단해짐!
		 * */
		
		int limit =  pagination.getLimit();
		int offset = (cp-1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		/* Mapper 메서드 호출 시
		 *  - 첫번째 매개변수 -> sql에 전달할 파라미터
		 *  - 두번째 매개변수 -> 두번째부터는 무조건 rouBounds객체 자리임
		 * */
		List<Board> boardList  = mapper.selectSearchList(paramMap, rowBounds);
		
		
		// 4. 목록 조회 결과 + pagination 객체를 map으로 묶어서 결과로 반환
		Map<String , Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);
			
			
			
			
			// 5. 결과 반환
			return map;

	}

	//DB 이미지 파일 목록 조회
	@Override
	public List<String> selectDbImageList() {
		
		return mapper.selectDbImgageList();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
