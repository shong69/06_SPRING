package edu.kh.project.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component  //bean 등록
//@Aspect     //공통 관심사가 작성된 클래스임을 명시함(AOP 동작용 클래스임을 선언함)
@Slf4j		//log를 찍을 수 있는 객체 생성 코드 추가(Lombok 제공)
public class TestAspect {
	
	//advice : 끼워 넣을 코드 (메서드)
	//Pointcut : 실제로 Adivice를 적용할 하나의 JoinPoint(지점)
	//포인트 컷을 경로처럼 작성해줘야 한다
	
	
	//<Pointcut 작성 방법>
	// execution( [접근제한자] 리턴타입 클래스명 메서드명 ([파라미터]) )
	// * 클래스명 : 패키지명부터 풀네임으로 작성한다
	
	// 주요 어노테이션 
	// - @Aspect : Aspect를 정의하는데 사용되는 어노테이션으로, 클래스 상단에 작성함
	// - @Before : 대상 메서드 실행 전에 Abvice를 실행함
	// - @After : 대상 메서드 실행 후에 Advice를 실행함
	// - @Around : 대상 메서드 실행 전후로 Advice를 실행함 (Before와 After을 합친거)
	
	//@Before(포인트컷)
	@Before("execution(* edu.kh.project..*Controller*.*(..))") 
	//execution : 메서드 실행 지점을 가리키는 키워드
	//* : 모든 리턴 타입을 나타냄
	//*edu.kh.project: edu.kh.project패키지와 하위에 해당하는 모든 패키지 
	//.. : 0개 이상의 하위 패키지
	//*Controller* : 이름에서 Controller를 포함한 모든 클래스를 대상으로함
	// .* :모든 메서드
	//(..) : 0개 이상의 파라미터 ()는 파라미터를 뜻함
	public void controllerStart() {
		
		log.info("----------testAdvice() 수행됨------------");
	}
	
	@After("execution(* edu.kh.project..*Controller*.*(..))")
	public void controllerEnd(JoinPoint jp) {
		//JoinPoint : AOP 기능이 적용된 대상
		
		//AOP가 적용된 클래스 이름 얻어오기
		String className = jp.getTarget().getClass().getSimpleName();//클래스 명만 얻어오게 하는 getSimpleName()
						 //MainController 로 가져와짐
		
		//실행된 컨트롤러 메서드 이름을 얻어오기
		String methodName = jp.getSignature().getName();
		log.info("------------{}.{} 수행 완료------------", className, methodName);
		
	}
	

	
	
	
}
