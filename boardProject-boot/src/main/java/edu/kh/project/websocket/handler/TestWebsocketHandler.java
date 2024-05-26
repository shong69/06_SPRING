package edu.kh.project.websocket.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

/** 000WebsocketHanler 클래스
 * 
 * 웹소켓 동작 시 수행할 구문을 작성하는 클래스
 * */
@Slf4j
@Component
public class TestWebsocketHandler extends TextWebSocketHandler  {
	
	private Set<WebSocketSession> sessions 
				= Collections.synchronizedSet(new HashSet<>());
	//세션이 중복되서 들어오지 않도록 Set에 넣음
	//WebsocketSession : 클라이언트와 서버간의 개별적인 연결을 나타내는 객체. 클-서 간의 전이중 통신을 담당하는 객체다
	//					SessionHandshakeInterceptor가 가로채서 연결한 클라이언트의 HttpSession을 가지고 있음
	//					(attributes에 추가한 값임)
	//Collections.synchronizedSet() : 동기화된 Set 상태임/ 일관성을 가지고, 순서를 가진 Set
	//								다수의 클라이언트가 서버에 요청을 보내며 세션이 꼬이는 것을 방지하고자
	//								멀티 쓰레드 환경에서 하나의 컬렉션에 여러 쓰레드가 접근하며 의도치 않은 문제가 발생되지 않게 하기 위해
	//								동기화를 진행해, 순서대로 한 컬렉션에 쓰레드들이 접근할 수 있도록 변경
	
	//클라이언트와 연결 완료 + 통신할 준비가 되면 실행하는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//매개변수인 session은 sessionHandshakeInterceptor가 가로채온 것임
		//연결된 클라이언트의 websocketSession 정보를 가로채서 Set에 추가하기
		// == 웹소켓에 연결된 클라이언트 정보를 한 곳에 모아둘거임
		
		sessions.add(session);
	}
	
	//클라이언트와 연결이 종료되면 실행
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		//웹소켓 연결이 끊긴 클라이언트 정보를 Set에서 제거하기
		sessions.remove(session);
	}
	
	
	//클라이언트로부터 텍스트 메시지를 받았을 때 실행
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		//다수의 클라이언트들에게서 보낸 메세지를 서로 볼 수 있도록 할거임
		//TextMessage : 웹소켓으로 연결된 클라이언트가 전달한 텍스트(내용)가 담겨있는 객체
		
		log.info("전달받은 메시지 : {}", message.getPayload()); 
										//getPayload() : 통신 시 탑제된 데이터, 즉 메세지를 말함
		
		
		//전달받은 메세지를 현재 해당 웹소켓에 연결된 모든 클라이언트에게 보내기
		for(WebSocketSession s:sessions) {
			s.sendMessage(message); //세션에 있는 각 클라이언트들에게 메세지를 보내줌
		}
	}
}
/*
WebSocketHandler 인터페이스 :
	웹소켓을 위한 메소드를 지원하는 인터페이스
  -> WebSocketHandler 인터페이스를 상속받은 클래스를 이용해
    웹소켓 기능을 구현
WebSocketHandler 주요 메소드
     
  void handlerMessage(WebSocketSession session, WebSocketMessage message)
  - 클라이언트로부터 메세지가 도착하면 실행
 
  void afterConnectionEstablished(WebSocketSession session)
  - 클라이언트와 연결이 완료되고, 통신할 준비가 되면 실행
  void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
  - 클라이언트와 연결이 종료되면 실행
  void handleTransportError(WebSocketSession session, Throwable exception)
  - 메세지 전송중 에러가 발생하면 실행
----------------------------------------------------------------------------
TextWebSocketHandler : 
	WebSocketHandler 인터페이스를 상속받아 구현한
	텍스트 메세지 전용 웹소켓 핸들러 클래스
  handlerTextMessage(WebSocketSession session, TextMessage message)
  - 클라이언트로부터 텍스트 메세지를 받았을때 실행
  
BinaryWebSocketHandler:
	WebSocketHandler 인터페이스를 상속받아 구현한
	이진 데이터 메시지를 처리하는 데 사용.
	주로 바이너리 데이터(예: 이미지, 파일)를 주고받을 때 사용.
*/
