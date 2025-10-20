package model;

import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Anne
 */

@WebFilter(urlPatterns = {"/userProfile.html","/adminPanel.html","/index-3.html","/restaurant-employees.html","/restaurant-order.html","/restaurant-reservation.html"})
public class SignInCheckFilter implements Filter{
    
     @Override
    public void init(FilterConfig filterConfig){
        
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response= (HttpServletResponse) res;
        
        HttpSession ses= request.getSession(false);
        if(ses != null && ses.getAttribute("user") != null){
            chain.doFilter(req, res);
        }else if(ses != null && ses.getAttribute("admin") != null){
            chain.doFilter(req, res);
        }else{
            response.sendRedirect("signIn.html");
        }
        
    }
    
    
      @Override
    public void destroy(){
        
    }
    
    
}
