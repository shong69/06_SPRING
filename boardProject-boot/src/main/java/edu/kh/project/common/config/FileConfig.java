package edu.kh.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;



@Configuration
@PropertySource("classpath:/config.properties") //외부 파일의 설정을 이용하겠다
public class FileConfig implements WebMvcConfigurer{
	
	//WebMvcConfigurer : Spring MVC 프레임워크에서 제공하는 인터페이스 중 하나로,
	// 				스프링 구성을 커스터마이징하고, 확장하기 위한 메서드를 제공한다.
	//주로 웹 어플리케이션의 설정을 조정하거나 추가하는데 사용된다.
	
	
	
	
	//config 파일의 설정들 사용할 수 있도록 끌어오기
	//파일 업로드 임계값
	@Value("${spring.servlet.multipart.file-size-threshold}")
	private long fileSizeThreshold;
	
	//요청당 파일 최대 크기
	@Value("${spring.servlet.multipart.max-request-size}")
	private long maxRequestSize;
	
	//개별 파일당 최대 크기
	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize;
	
	//임계값 초과 시 임시 저장 폴더 경로
	@Value("${spring.servlet.multipart.location}")
	private String location;

	//---------------------------------------------
	//프로필 이미지
	
	@Value("${my.profile.resource-handler}")
	private String profileResourceHandler;
	
	@Value("${my.profile.resource-location}")
	private String profileResourceLocation;

	
	
	//요청 주소에 따라 서버컴퓨터의 어떤 경로에 접근할 지 설정해준다
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		
		registry.addResourceHandler("/myPage/file/**") //클라이언트의 요청 주소 패턴 
		.addResourceLocations("file:///C:/uploadFiles/test/");
		
		//클라이언트가 첫번째 경로 이하의 모든 패턴으로 이미지를 요청할 때
		//서버에서는 요청을 연결해서 처리해줄 서버 폴더 경로를 연결해준 것이다.
		
		
		//프로필 이미지 요청 -> 서버 폴더로 연결해준다
		registry
		.addResourceHandler(profileResourceHandler)   //   /myPage/profile/** 로 시작하는 모든 이미지는
		.addResourceLocations(profileResourceLocation);   //   file:///C:/uploadFiles/profile/ 에다가 넣어주겠다
		
		
		// file"///C: -> 파일 시스템의 루트 디렉토리(최상위 경로)이다
		// file:// 은 URL 스킴(Scheme), 파일 시스템의 리소스
		//  /C:는 windows 시스템에서 C드라이브를 가리킴
		// file"///C: -> C드라이브의 루트 디렉토리를 의미함
		
		
		
	}
	
	
	
	
	/*MultipartResolver 설정하기*/
	@Bean
	public MultipartConfigElement configElement() {
		//MultipartConfigElement
		//파일 업로드를 처리하는데 사용되는 MultipartConfigElement를 구성하고 반환해줌
		//파일 업로드를 위한 구성 옵션을 설정하는데 사용한다
		//업로드 파일의 최대 크기, 메모리에서의 임시 저장 경로 등을 설정해준다
		
		//-> 서버 경로 작성해야함(보안 중요) 
		//--> 이 문서 내에 직접 경로 작성하지 않고 config.properties에 작성해 불러올거임
		
		
		//멀티파트를 위한 설정을 해줄 공장이 필요함
		MultipartConfigFactory factory = new MultipartConfigFactory();
		
		//팩토리에 설정값들 넣어주기
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold)); //long타입이기 때문에 byte 형태로 바꿔주었다
		
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
		
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));
		
		factory.setLocation(location);
		
		return factory.createMultipartConfig(); //찍어내기
		
		
		
		//이 config를 사용해서 multipartResolver를 만들어줘야 한다 -> Bean으로 등록해줘야 함(spring이 관리하도록)	
		
	}
	
	//빈으로 등록하며 위에서 만든 multipartconfig를 자동으로 이용할 수 있게 된다
	//multipartResolver가 추가되어야 multipartConfigElement를 작동시킬 수 있다?
	@Bean
	public MultipartResolver multipartResolver() {
		//MultipartResolver : MultipartFile을 처리해주는 해결사..
		//MultipartResolver는 클라이언트로 받은 multipart요청을 처리하고,
		// 이 중에서 업로드 된 파일을 추출하여 multipartFile 객체러 제공해주는 역할을 한다
		
		StandardServletMultipartResolver multipartResolver
			= new StandardServletMultipartResolver(); 
		
		
		return multipartResolver;
	}
	
	
	
	
	
	
	
	
	
	
}
