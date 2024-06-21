package edu.kh.project.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

//JUnit : Java 개발 표준 테스트 프레임워크
//코드의 정상 동작을 확인하기 위해서 테스트를 작성하고 실행함
public class CalculatorTest {

	// 주요 어노테이션
	// @Test: 이 메서드가 테스트 메서드임을 나타냄
	// @BeforeEach: 각 테스트 메서드가 실행되기 전에 실행되는 메서드를 정의함
	// @AfterEach: 각 테스트 메서드가 실행된 후에 실행되는 메서드를 정의함
	// @BeforeAll: 모든 테스트 메서드가 실행되기 전에 한 번 실행되는 메서드를 정의함
	// @AfterAll: 모든 테스트 메서드가 실행된 후에 한 번 실행되는 메서드를 정의함
	// 주요 메서드 (assert : 단언하다/주장하다, (프로그래밍에서) 가정하다)
	
	// assertEquals : 두 값이 같은지 확인, 같지 않으면 테스트가 실패함
	// assertTrue : 조건이 참인지 확인, 거짓이면 테스트가 실패함
	// assertFalse : 조건이 거짓인지 확인, 참이면 테스트가 실패함
	// assertNotNull : 객체가 null이 아닌지 확인, null이면 테스트가 실패함
	// assertThrows : 특정 예외가 발생하는지 확인, 예외가 발생하지 않으면 테스트가 실패함

	//인스턴스 생성
	private Calculator calculator = new Calculator();
	
	
	
	@Test
	public void testAdd() {
		//assertEquals(예상값, 실제값);
		assertEquals(5, calculator.add(2, 3)); 
		//calculator.add(par,par)을 수행했을 때 앞에 넣은 인자가 나오느냐고 시험함
	}
	
	
	@Test
	public void testSubtract() {

		assertEquals(1, calculator.subtract(3, 2)); 
	}
	
	
	@Test
	public void testMultiply() {
		
		assertEquals(6, calculator.multiply(3, 2)); 
	}
	
	
	@Test
	public void testDivide() {
		
		assertEquals(2, calculator.divide(4, 2)); 
	}
	
	
	
	@Test
	public void tesDivideByZero() {
		
		//assertThrows(기대되는 예외 클래스, 예외가 발생할 것으로 예상되는 코드);
		assertThrows(IllegalArgumentException.class, 
					()-> calculator.divide(5, 0)); 

	}
	
	@Test
	public void testExam() {
		assertTrue(calculator.exam());
	}
	
}
