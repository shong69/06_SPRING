package edu.kh.todo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;
/**
 * 
 */
@Slf4j
@Controller
@RequestMapping("todo") //"todo"로 시작하는 모든 요청 매핑
public class TodoController {
	
	@Autowired
	private TodoService service;
	
	@RequestMapping("/")
	public String mainPage(Model model) {
		//의존성 주입(DI) 확인(진짜 Service 객체 들어옴!)
		log.debug("service: "+ service);
		//Service 메서드 호출 후 결과 반환받기
		Map<String, Object>map =service.selectAll();
		
		//map에 담긴 내용 추출
		List<Todo> todoList = (List<Todo>)map.get("todoList");
		int completeCount = (int)map.get("completeCount");
		
		//Model : 값 전달용 객체(request scope) + session 변환 가능
		model.addAttribute("todoList", todoList);
		model.addAttribute("completeCount", completeCount);
		
		return "common/main";
	}
	
	@PostMapping("add")
	public String addTodo(
			@RequestParam("todoTitle") String todoTitle,
			@RequestParam("todoContent") String todoContent,
			RedirectAttributes ra
			) {
		
		//RedirectAttributes : 리다이렉트 시 값을 일회성으로 전달하는 객체
		//RedirectAttributes.addFlashAttribute("key", value) 형식으로 잠깐 세션에 속성을 추가해줌
		
		//[원리]
		// 응답 전 : request scope
		// redirect 중 : Session scope로 이동
		// 응답 후 : request scope 복귀
		
		
		//서비스 메서드 호출 후 결과 반환받기
		int result = service.addTodo(todoTitle, todoContent);
		
		//삽입 결과에 따라 message값 지정
		String message = null;
		
		if(result>0) message="할 일 추가 성공!";
		else 		 message="할 일 추가 실패,,";
		
		//리다이렉트 후 일회성으로 사용할 데이터를 속성으로 추가
		ra.addFlashAttribute("message", message);
		
		return "redirect:/";
		
	}
	
	//상세 조회
	@GetMapping("detail")
	public String todoDetail(@RequestParam("todoNo") int todoNo,
							Model model,
							RedirectAttributes ra) {
		
		
		Todo todo = service.todoDetail(todoNo);
		
		
		//조회되는 결과에 따라 return의 path를 다르게 처리해줄거임
		String path = null;
		
		if(todo!=null) { //조회 결과 있음
			
			//forward : todo/detail
			path = "todo/detail";  //접두사(classpath:/templates), 접미사(.html) 없이 적힌 상태
			
			//request scope 값 세팅
			model.addAttribute("todo", todo); 
			
		}else {  //조회결과 없을 경우
			
			path="redirect:/";  //메인페이지로 리다이렉트
			
			//메시지를 세션에 계속 올려두기 좀 어색->redirect 사용하기
			//RedirectAttributes : 리다이렉트 시 데이터를 request scope -> (잠시)session scope로
			//전달할 수 있는 객체(응답 후 request scope로 복귀)
			
			ra.addFlashAttribute("message", "해당 할 일이 존재하지 않습니다.");
		}
		
		return path;
	}
	
	/** 완료 여부 변경
	 * @param todo : 커맨드 객체 (@ModelAttribute 생략됨)
	 * 				-todoNo, complete 두 필드가 세팅된 상태
	 * @param ra
	 * @return redirect:detail?todoNo=" + 할 일 번호(상대경로)
	 */
	@GetMapping("changeComplete")
	public String changeComplete(Todo todo, RedirectAttributes ra) { //modelAttribute 기능 사용
		
		//변경 서비스 호출
		int result = service.changeComplete(todo);
		
		
		//변경 성공 시 : "변경 성공!"
		//변경 실패 시 : "변경 실패!"
		String message = null;
		
		if(result >0) message="변경 성공!";
		else 		  message="변경 실패!";
		
		ra.addFlashAttribute("message",message);

		//현재 요청주소 : /todo/changeComplete
		//응답할 주소 : /todo/detail
		//->todoNo까지 붙어있는 detail 주소로 가야 한다
		return "redirect:detail?todoNo=" + todo.getTodoNo(); //url 경로와 똑같이 적어준다(상대경로임)

		
	}
	
	
	
	/**수정화면으로 전환
	 * @return todo/update
	 */
	@GetMapping("update")
	public String todoUpdate(@RequestParam("todoNo") int todoNo,
			Model model) {
		//-> todoNo 상세조회 서비스 호출 필요==수정화면에 출력할 이전 내용으로 활용
		//todoDetail 호출
		Todo todo = service.todoDetail(todoNo);
		
		
		model.addAttribute("todo", todo);
		
		
		
		
		
		
		return "todo/update"; //todo폴더의 update.html로 포워드 
		
	}
	


	/**수정하기 버튼 클릭으로 수정
	 * @param todo : 커맨드 객체(전달받은 파라미터가 자동으로 DTO의 필드에 세팅된 객체)
	 * @param ra
	 * @return path
	 */
	@PostMapping("update")
	//오버로딩이 성립됨 : 매개변수의 타입, 개수가 다르면 되기 때문에 메서드명 같아도 ㄱㅊ
	public String todoUPdate(Todo todo, RedirectAttributes ra) {
		/*todoContent, todoTitle, todoNo을 requestParam으로 안가져 온 이유
		 * ->mybatis에서는 오직 1개만 가져올 수 있기 때문*/
		
		
		//수정 서비스 호출
		int result = service.todoUpdate(todo);
		
		String path = "redirect:";
		
		String message= null;
		
		if(result >0) {
			//상세 조회 페이지로 리다이렉트
			path += "/todo/detail?todoNo="+todo.getTodoNo();
			message = "수정 성공";
		}else {
			//다시 수정화면으로 리다이렉트
			path += "/todo/update?todoNo="+todo.getTodoNo();
			message = "수정 실패..";
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return path;
	}

	/** 할 일 삭제
	 * @param todoNo : 삭제할 할 일 번호
	 * @param ra
	 * @return 메인페이지로 돌아감
	 */
	@GetMapping("delete")
	public String todoDelete(@RequestParam("todoNo") int todoNo,
							RedirectAttributes ra) {
		
		int result = service.todoDelete(todoNo);
		
		String path = null;
		String message="";
		
		if(result>0) { //delete 성공
			
			path ="/";
			message="삭제 성공";
					
		}else { //delete 실패
			
			path="/todo/detail?todoNo="+todoNo;
			message="삭제 실패";
		}
		
		ra.addFlashAttribute("message", message);
		return "redirect:" + path;
	}
	
	
	
}
