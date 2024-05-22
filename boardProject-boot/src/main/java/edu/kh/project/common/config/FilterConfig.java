package edu.kh.project.common.config;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.kh.project.common.filter.LoginFilter;

/*만들어놓은 loginFilter 클래스가 언제 적용될지 설정하기*/
@Configuration //서버가 켜질 때 해당 클래스 내 모든 메서드가 실행된다 -> 프로젝트 전반적으로 설정이 적용된 상태로 굴러가게됨
public class FilterConfig {

	@Bean //반환된 객체를 Bean으로 등록 : LoginFilter로 타입을 제한함
	public FilterRegistrationBean<LoginFilter> loginFilter() {
		// FilterRegistrationBean : 필터를 Bean으로 등록해주는 객체
		
		FilterRegistrationBean<LoginFilter> filter
			= new FilterRegistrationBean<>();
		
		//사용할 필터 객체 추가하기
		filter.setFilter(new LoginFilter());
		
		// /myPage/* : myPage로 시작하는 모든 요청
		String[] filteringURL = {"/myPage/*", "/editBoard/*","/chatting/*"}; //myPage로 시작하는 모든 주소가 배열에 들어간다
		
		//필터가 동작할 URL을 세팅하기
		//Arrays.asList(filteringURL) : 매개변수 배열을 List로 변환해줌
		filter.setUrlPatterns(Arrays.asList(filteringURL));
		
		// 필터 이름 지정
		filter.setName("loginFilter");
		
		//필터 순서 지정
		filter.setOrder(1); //첫번째로 동작할것이다
		
		
		return filter; //반환된 객체가 필터를 생성해서 Bean으로 등록해준다
		
	}
}
