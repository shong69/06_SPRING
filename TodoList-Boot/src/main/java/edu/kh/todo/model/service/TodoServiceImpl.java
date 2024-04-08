package edu.kh.todo.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.mapper.TodoMapper;

//--------------------------------------------
// @Transactional
//- 트랜잭션 처리를 수행하라고 지시하는 어노테이션
// (== 선언적 트랜잭션 처리)

//- 정상 코드 수행 시 COMMIT

//- 기본값 : Service 내부 코드 수행 중 RuntimeException 발생 시 rollback

//-rollbakcFor : 어떤 예외가 발생했을 때 rollback할 지 지정


//--------------------------------------------

//모든 종류의 예외 발생 시 rollbakc 수행
@Transactional(rollbackFor= Exception.class)
@Service //비즈니스 로직(데이터 가공, 트랜잭션 처리)
public class TodoServiceImpl implements TodoService{
	
	@Autowired //DI
	private TodoMapper mapper;

	//할 일 목록 + 완료된 할 일 개수 조회
	@Override //Service implements했으니까 메서드 오버라이드 하는거임
	public Map<String, Object> selectAll() {
		// 1. 할 일 목록 조회
		List<Todo> todoList =mapper.selectAll();
		
		// 2. 완료된 할 일 개수 조회
		int completeCount = mapper.getCompleteCount();
		
		// Map으로 묶어서 반환
		Map<String, Object> map = new HashMap<>();
		
		map.put("todoList", todoList);
		map.put("completeCount", completeCount);
		
		
		return map;
	}

	//할 일 추가
	@Override
	public int addTodo(String todoTitle, String todoContent) {
		
		//Connection 생성 /반환 X
		//트랜잭션 제어 처리 -> @Transactional 어노테이션
		
		//마이바티스에서 SQL에 전달할 수 있는 파라미터의 개수는 오직 1개
		//-> todoTitle, todoContent를 Todo DTO로 묶어서 전달
		
		Todo todo = new Todo();
		todo.setTodoTitle(todoTitle);
		todo.setTodoContent(todoContent);
		
		
		
		
		return mapper.addTodo(todo);
	}
	
	//할 일 상세 조회
	@Override
	public Todo todoDetail(int todoNo) {
		
		return mapper.todoDetail(todoNo);
	}
	
	//완료 여부 변경
	@Override
	public int changeComplete(Todo todo) {
		return mapper.changeComplete(todo);
	}

	//todo 수정하기
	@Override
	public int todoUpdate(Todo todo) {
		return mapper.todoUpdate(todo);

	}

	//할 일 삭제
	@Override
	public int todoDelete(int todoNo) {
		return mapper.todoDelete(todoNo);
	}

	//전체 할 일 개수 조회
	@Override
	public int getTotalCount() {
		return mapper.getTotalCount();
	}

	//완료된 할 일 개수 조회
	@Override
	public int getCompleteCount() {

		return mapper.getCompleteCount();
	}

	//할 일 목록 조회
	@Override
	public List<Todo> selectList() {
		return mapper.selectAll();  //할 일 목록을 전부 조회하는 SQL문
	}

	
}
