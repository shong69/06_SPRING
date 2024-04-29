package test;

import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.HandlerInterceptor;

public class CategoryInterceptor implements HandlerInterceptor{

	@Autowired
	private ProductService service;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
	
		ServletContext application = request.getServletContext();
		
		if(application.getAttribute("categoryList") == null) {
		
			List<Map<String, Object>> categoryList= service.selectCategoryList();
			
			application.setAttribute("categoryList", categoryList);
	
		}
	
		return HandlerInterceptor.super.preHandle(request, response, handler);
	
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	
	}

}





public class InterceptorConfig implements WebMvcConfigurer{

	@Bean
	public CategoryInterceptor categoryInterceptor() {
		return new CategoryInterceptor();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	
		registry.addInterceptor( categoryInterceptor() )
		.addPathPatterns("/**")
		.excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico");
	
	}

}

[product-mapper.xml]

<select id="selectCategoryList" returnType>

SELECT CATEGORY_NO "categoryNo", CATEGORY_NAME "categoryName"

FROM CATEGORY

ORDER BY CATEGORY_NO

</select>


