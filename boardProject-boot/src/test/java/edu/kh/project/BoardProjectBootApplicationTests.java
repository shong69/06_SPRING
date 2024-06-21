package edu.kh.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//springBoot 어플리케이션을 위한 기본 통합 테스트 클래스

@SpringBootTest //springBoot 어플리케이션 컨텍스트()를 로드하여 통합 테스트를 수행함
//어플리케이션의 모든 빈(Bean)을 로드하고, 이를 통해 실제 어플리케이션이
//실행되는 것과 같은 방식으로 테스트를 진행한다
class BoardProjectBootApplicationTests {

	@Test
	//JUnit에게 이 메서드가 테스트 메서드임을 알려줌
	void contextLoads() {
		//어플리케이션 컨텍스트가 제대로 로드되는 것을 확인하는 역할
		//어플리케이션 컨텍스트가 로딩에 실패하면 이 테스트는 실패함
		//정상적 로드되면 이 테스트는 통과함
		//-> Spring Boot 어플리케이션의 기본 설정이 올바른지 확인하기 위해 자동 생성됨
	}

}
