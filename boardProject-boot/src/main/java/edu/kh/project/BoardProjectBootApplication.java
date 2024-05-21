package edu.kh.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
					//spring security에서 제공하는 로그인페이지 이용 안하겠다는 선언
@SpringBootApplication(exclude= {SecurityAutoConfiguration.class})
@EnableScheduling //스프링 스케줄러를 이용하기 위한 활성화 어노테이션
public class BoardProjectBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardProjectBootApplication.class, args);
	}

}
