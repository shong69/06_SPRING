package edu.kh.project.common.util;

import java.text.SimpleDateFormat;

//프로그램 전체적으로 사용될 유용한 기능 모음
public class Utility {

	//필드, 메서드 전부 static으로 설정
	
	public static int seqNum = 1; //1~99999를 반복한다
	
	
	public static String fileRename(String originalFilename) {
		
		//20240417102705_00004.jpg (연월일시분초_시퀀스번호.확장자 형태로 반환)
		//SimpleDateFormat : 시간을 원하는 형태의 문자열로 간단히 변경해줌
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//java.util.Date() : 현재 시간을 저장한 자바 객체
		String date = sdf.format(new java.util.Date()); 
		
		//Sting.format(,) : 문자열 형식 정해주는 메서드 
		String number = String.format("%05d", seqNum);
		
		seqNum++;
		
		if(seqNum==100000) seqNum = 1;
		
		//확장자 구하기
		// "문자열".substring(인덱스)
		// - 문자열을 전달된 인덱스부터 끝까지 잘라내서 결과를 반환함
		
		// "문자열".lastIndexOf(".") 
		// - 문자열에서 마지막 "."의 인덱스를 반환한다
		
		String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
		
		//originalFilename = 뚱이.jpg
		//ext = .jpg
		
		return date + "_" + number+ext; //20240417102705_00004.jpg 형식으로 리턴
	}
}
