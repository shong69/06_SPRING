package edu.kh.todo.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;







/* @Configuration : 설정용 클래스임을 명시하는 역할
 * - 객체로 생성해서 서버 실행 시 내부 코드를 모두 수행해준다
 * 
 * @PropertySource("경로")
 * -지정된 경로의 properties 파일 내용을 읽어와서 사용한다
 * -사용할 properties 파일이 다수일 경우, 해당 어노테이션을 연속 작성하면 된다
 * 
 * @ConfigurationProperties(prefix="spring.datasource.hikari") 
 * - @PropertiySource로 읽어온 properties 파일의 내용 중
 * 	 접두사가 일치하는 값만 읽어온다.
 * 
 * @Bean : 개발자가 수동으로 생성한 객체의 관리를 Spring에게 넘겨주는 어노테이션
 * 
 * 
 * DataSource : Connection 생성 + Connection Pool을 지원하는 객체를 참조하기 위한 Java 인터페이스
 * 				(DriverManager의 대안. java JNDI 기술이 적용되었다)
 * 
 * 
 * @Autowired : 등록된 Bean 중에서 타입이 일치하거나, 상속관계에 있는 Bean을 지정된 필드에 주입해줌
 * == 의존성 주입(DI, Dependency Injection. IOC 관련 기술)
 * */








@Configuration
@PropertySource("classpath:/config.properties")  //classpath : src/main/resources
public class DBConfig {

	
	
	
	
	//필드에서 지금 내 위치 선언하기
	@Autowired  //DI(의존성 주입)
	private ApplicationContext applicationContext;  //application scope 객체 : 즉 현재 프로젝트를 나타낸다
	
	//spring에서 이미 applicationContext를 Bean에 등록해서 관리중이기 때문에 객체 선언을 하지 않아도 된다.
	//대신 spring한테 얘랑 맞는 타입을 넣어달라고 요청해야 함(의존성 주입) ->어노테이션으로 선언하기
	
	
	
	
	
	
	////////////////////hikariCP 설정///////////////////
	
	
	
	
	@Bean  //properties에서 가져온 내용을 spring이 관리해주도록 함
	@ConfigurationProperties(prefix="spring.datasource.hikari") 
	//이 접미사로 시작하는 모든 라인들을 가지고 오겠다. + 가지고 온 개체를 hikari 객체로 만들어둠 + Bean으로 등록함
	public HikariConfig hikariconfig() {
		
		return new HikariConfig(); 
	}
	
	
	
	
	
	@Bean
	public DataSource dataSource(HikariConfig config) {
		//매개변수 HikariConfig config
		// -> 등록된 Bean 중 HikariConfig 타입의 Bean이 자동으로 매개변수 자리에 주입됨
		
		DataSource dataSource = new HikariDataSource(config);
		
		return dataSource;
	}
	
	
	
	
	//////////////////Mybatis 설정 ////////////////////////
	
	
	public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception {
		
		//sqlsession을 bean으로 만들기 위해 datasource를 매개변수에 넣은거임
		
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		//factory부터 객체로 만들어냄(Bean)
		
		//factoryBean에 datasource에 대한 정보 세팅하기 (by setting HikariDataSourcein sessionFactoryBean)
		sessionFactoryBean.setDataSource(dataSource);
		
		//mappper.xml(SQL넣어두는 파일)이 모이는 경로를 지정함
		//->Mybatis 코드 수행 시 mapper.xml을 읽을 수 있음
		// sessionFactoryBean.setMapperLocations("현재프로젝트.자원.어떤파일");
		
		sessionFactoryBean.setMapperLocations(
				applicationContext.getResources("classpath:/mappers/**.xml"));  /// **.xml : xml로 끝나는 모든 파일
		
		
		//setTypeAliasesPackage : 해당 패키지 내의 모든 클래스에 별칭을 등록해줌. 해당 패키지는 클래스만으로 불러도 되도록 함
		//- Mybatis는 특정 클래스 지정 시 패키지명.클래스명을 모두 작성해야 함->너무길어
		// => 긴 이름을 짧게 부르도록 별칭 설정해줌
		
		//setTypeAliasesPackage("패키지") 이용 시 클래스 파일명이 별칭으로 등록됨
		//ex)edu.kh.todo.mode.dto.Todo -->Todo (별칭 등록)
		sessionFactoryBean.setTypeAliasesPackage("edu.kh.todo");
		
		
		
		
		
		//마이바티스 설정 파일 경로 지정하기
		//mybatis-config.xml 파일 생성하기만 하고 등록을 안했으니까 sessionFactoryBean에 등록해야 함
		sessionFactoryBean.setConfigLocation(
				applicationContext.getResource("classpath:/mybatis-config.xml"));
		
		
		//설정 내용이 모두 적용된 객체를 반환하기
		return sessionFactoryBean.getObject();
				
		
	}
	
	
	
	
	
	// SqlSessionTemplate : Connection + DBCP + Mybatis + 트랜잭션 제어처리
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sessionFactory) {
		
		
	return new SqlSessionTemplate(sessionFactory);
	
	
	}
	
	
	
	
	
	
	// DataSourceTransactionManager : 트랜잭션 매니저(제어 처리)
	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		
		
	return new DataSourceTransactionManager(dataSource);
	
	
	}
	
	

	
	
	
}
