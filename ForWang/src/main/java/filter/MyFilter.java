package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import dao.LoginDao;

@Component(value="myFilter")
public class MyFilter implements Filter {

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request=(HttpServletRequest) arg0;
		HttpServletResponse response=(HttpServletResponse) arg1;
		HttpSession session=request.getSession();
		String uri=request.getRequestURI();
		System.out.println(request.getContextPath());
		System.out.println(request.getRequestURI());
		if(request.getRequestURI().contains("login.xhtml")||pass_url(uri.substring(request.getContextPath().length()))||request.getRequestURI().contains("login")||pass_resource(request.getRequestURI()))
		{
			arg2.doFilter(arg0, arg1);
		}
		else if(session.getAttribute("user_role")==null)
		{
			response.sendRedirect("login.xhtml");
		}
		else{
			arg2.doFilter(arg0, arg1);
		}
	}
	private boolean pass_resource(String url){
		if(url.endsWith(".js")||url.endsWith(".css"))
			return true;
		return false;
	}
	private boolean pass_url(String url)
	{
		System.out.println("url: "+url);
		if(url.equalsIgnoreCase("/fun")||url.equalsIgnoreCase("/GenImg")||url.equalsIgnoreCase("/check_lessons")){
			return true;
		}
		return false;
	}
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
}
