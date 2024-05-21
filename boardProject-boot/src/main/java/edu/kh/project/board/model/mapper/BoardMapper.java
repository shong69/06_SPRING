package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	/** 게시판 종류 조회
	 * @return boardTypeList
	 */
	List<Map<String, Object>> selectBoardTypeList(); //조회된 행 하나하나가 map으로 만들어지고, 그 후 list로 묶이게 된다

	/** 지정된 게시판에서 삭제되지 않은 게시글의 수 조회
	 * @param boardCode
	 * @return listCount
	 */
	int getListCount(int boardCode);

	/** 특정 게시판의 지정된 페이지 목록 조회하기
	 * @param boardCode
	 * @param rowBounds
	 * @return
	 */
	List<Board> selectBoardList(int boardCode, RowBounds rowBounds);

	/** 게시글 상세 조회
	 * @param map
	 * @return board
	 */
	Board selectOne(Map<String, Integer> map);

	/** 좋아요 해제(DELETE)
	 * @param map
	 * @return
	 */
	int deleteBoardLike(Map<String, Integer> map);

	/** 좋아요 체크(INSERT)
	 * @param map
	 * @return
	 */
	int insertBoardLike(Map<String, Integer> map);

	/** 게시글 좋아요 개수 조회
	 * @param temp
	 * @return count
	 */
	int selectLikeCount(int temp );

	/** 게시글 조회수 1 증가
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);

	/** 게시글 조회수 보기
	 * @param boardNo
	 * @return
	 */
	int selectReadCount(int boardNo);

	/** 검색 조건이 맞는 게시글 수 조회
	 * @param paramMap
	 * @return count
	 */
	int getSearchCount(Map<String, Object> paramMap);

	/**검색한 게시글을 목록 조회
	 * @param paramMap
	 * @param rowBounds
	 * @return List<Board>
	 */
	List<Board> selectSearchList(Map<String, Object> paramMap, RowBounds rowBounds);

	/** DB 이미지 파일명 목록 조회
	 * @return List<String>
	 */
	List<String> selectDbImgageList();

}
