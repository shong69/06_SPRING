package edu.kh.project.websocket.handler;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kh.project.chatting.model.dto.Message;
import edu.kh.project.chatting.model.service.ChattingService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChattingWebsocketHandler extends TextWebSocketHandler{

	private final ChattingService service;
	private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
	
	//클라이언트와 연결이 완료되고, 통신할 준비가되면 실행하는 메서드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		
		log.info("{} 연결됨",session.getId()); //누구랑 연결됐는지 로그로 남기기
	}
	 
	//클라이언트와 통신이 끝났을 때 실행하는 메서드
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		
		log.info("{} 연결끊김",session.getId());
	}
	
	
	
	// 클라이언트에게 메세지를 받았을 때 실행됨
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		//메세지로 들어온 데이터를 가공하기
		//message - JS에서 전달받은 내용 == JSON 형태로 가공된 object {"senderNo":"1", "targetNo":"2", "chattingNo":"8","messageContent":"hi"}
		
		//컨트롤러에서는 커먄드객체로 알아서 dto에 세팅해줬음
		//dto형태로 변환하기
		
		//Jackson에서 제공하는 객체
		ObjectMapper objectMapper = new ObjectMapper();
		
		Message msg = objectMapper.readValue(message.getPayload(), Message.class);//getPayload 탑제된 데이터 꺼내줌
		//읽어온 message의 데이터를 Message dto의 클래스로 지정해서 설정해줌
		
		//Message 객체 확인
		log.info("msg : {}", msg);
		
		
		//DB 삽입 서비스 호출
		int result = service.insertMessage(msg);
		
		if(result>0) {
			
			//메세지 전송 시간을 세팅하고 클에게 전하기
			SimpleDateFormat sdf = new SimpleDateFormat("yyy.MM.dd hh:mm");
			
			msg.setSendTime(sdf.format(new Date())); //msg에 현재 시간 세팅
			
			//채팅하고 있는 상대방을 꺼내오기(전역변수로 선언된 sessions에는 접속중인 모든 회원의 세션 정보가 담겨 있음)
			for(WebSocketSession s : sessions) {
				
				//가로챈 세션 꺼내기
				HttpSession temp = (HttpSession)s.getAttributes().get("session"); 
				//s에서 session이란 어트리뷰트를 가진 거 꺼내고 캐스팅 하기
				
				//로그인된 회원 정보 중 회원 번호를 꺼내오기
				int loginMemberNo = ((Member)temp.getAttribute("loginMember")).getMemberNo();
				
				//로그인 상태인 회원 중 targetNo가 일치하는 회원에게 메시지 전달하기
				if(loginMemberNo == msg.getTargetNo() || loginMemberNo == msg.getSenderNo()) {
					//보낸 사람이거나 받은 대상인 경우 메세지를 전달한다
					
					//다시 DTO Object를 JSON으로 변환하여 js보내기
					String jsonData = objectMapper.writeValueAsString(msg);
					
					s.sendMessage(new TextMessage(jsonData));
				}
			}
						
			
		}
	}
}
