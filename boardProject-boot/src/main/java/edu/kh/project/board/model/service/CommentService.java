package edu.kh.project.board.model.service;

import java.util.List;

import edu.kh.project.board.model.dto.Comment;

public interface CommentService {

	/** 댓글 목록 조회
	 * @param boardNo
	 * @return List<Comment>
	 */
	List<Comment> select(int boardNo);

	/**
	 * @param comment
	 * @return
	 */
	int insert(Comment comment);

}
