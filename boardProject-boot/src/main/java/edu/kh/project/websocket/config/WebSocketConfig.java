package edu.kh.project.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import edu.kh.project.websocket.handler.ChattingWebsocketHandler;
import edu.kh.project.websocket.handler.TestWebsocketHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration //서버 실행 시 작성된 메서드를 모두 수행
@EnableWebSocket //웹소켓 활성화 시키는 설정
public class WebSocketConfig implements WebSocketConfigurer{
							//WebSocketConfigurer
	//Bean으로 등록된 핸드셰이크인터셉터의 자식타입인 SessionHandshakeInterceptor가 주입된다
	private final HandshakeInterceptor handshakeInterceptor;
	
	//웹소켓 처리 동작이 작성된 객체 의존성 주입
	private final TestWebsocketHandler testWebsocketHandler;
	
	//채팅 관련 웹소켓 처리 동작이 작성된 객체 의존성 주입
	private final ChattingWebsocketHandler chattingWebsocketHandler;
	
	
	//웹소켓 핸들러를 등록하는 메서드
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		//addHandler(웹소켓 핸들러, 웹소켓 요청 주소)
		registry.addHandler(testWebsocketHandler, "/testSock")
		// ws://localhost/testSock으로 클라이언트가 요청 시 
		//testWebsocketHandler가 처리하도록 등록하는 과정임
		
				.addInterceptors(handshakeInterceptor)
		//클라이언트 연결 시 HttpSession을 가로채서 핸들러에게 전달하는 역할
				
				.setAllowedOriginPatterns("http://localhost/", 
										"http://127.0.0.1/", 
										"http://192.168.50.200/")
		//웹소켓 요청이 허용되는 ip or 도메인을 지정하는 부분
				.withSockJS();
		//SockJS 지원한다고 
		
		
		///----------------------------
		
		registry.addHandler(chattingWebsocketHandler, "/chattingSock") //js에 적어놨던 경로
				.addInterceptors(handshakeInterceptor)
				.setAllowedOriginPatterns("http://localhost/", 
										"http://127.0.0.1/", 
										"http://192.168.50.200/")
				.withSockJS();
		
	}
	
	
	
	
}
