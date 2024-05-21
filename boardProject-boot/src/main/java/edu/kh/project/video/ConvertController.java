package edu.kh.project.video;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ConvertController {

	private final ConvertService convertService;
	
//	public String convertToHls(
//			@PathVariable String date,
//			@PathVariable String fileName) {
		
//		convertService.convertToHls(date, fileName);
//		return "success";
		
//	}
}
