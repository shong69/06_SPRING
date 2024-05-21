package edu.kh.project.video;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("video")
public class VideoController {

	@GetMapping("")
	public String videoView() {
		return "video/videoMain";
	}
}