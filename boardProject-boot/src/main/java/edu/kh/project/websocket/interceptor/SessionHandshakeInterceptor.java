package edu.kh.project.websocket.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;

/** SessionHandshakeInterceptor 클래스
 *  WebSocketHandler가 동작하기 전/후에 
 *  연결된 클라이언트의 세션을 가로채는 동작을 작성할 클래스
 * */
@Component //Bean으로 등록해야함
public class SessionHandshakeInterceptor implements HandshakeInterceptor {
	
	//핸들러 동작 전에 수행되는 메서드
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		// ServletHttpRequest : HttpServletRequest 의 부모 인터페이스
		// ServletHttpResponse : HttpServlerResponse의 부모 인터페이스
		
		//attributes : 해당 맵에 세팅된 속성(데이터)은
		//				다음에 동작할 Handler 객체에게 전달됨
		//				(HandshakeInterceptor -> Handler로 데이터를 전달하는 역할)
		
		//세션을 얻어와서 attributes에 세팅하고 handler로 전해주기
		//request가 참조하는 객체가 
		//ServletServerHttpRequest로 다운캐스팅이 가능한지? -> 부모자식관계가 맞을 때만 다운캐스팅을 할거임
		if(request instanceof ServletServerHttpRequest) { //ServerHttpRequest의 자식인 ServletServerHttpRequets로 다운캐스팅 후 받아와야 한다
			
			//다운캐스팅
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			
			//웹소켓 동작을 요청한 클라이언트의 세션을 얻어오기 -> 세션을 얻어오기 위해서는 다운캐스팅이 필요함
			HttpSession session = servletRequest.getServletRequest().getSession();
			
			//가로챈 세션을 Handler에게 전달할 수 있게 값을 세팅하기
			attributes.put("session", session);
			
			
			
			
		}
		
		return true;  //가로챌지에 대한 진행 여부를 가름 -> 기본 값인 false에서 true로 바꿔줘야 Handler에게 전달가능
	}
	
	//
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
		
	}
	
}
