/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "CheckCartQty", urlPatterns = {"/CheckCartQty"})
public class CheckCartQty extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();
        
       
        try{
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        
        if(request.getSession().getAttribute("user") != null){
            
            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", request.getSession().getAttribute("user")));
            
            List<Cart> checkQtyCartItems = c1.list();
            
            responseObject.addProperty("status", true);
            
            responseObject.add("checkQtyCartItems", gson.toJsonTree(checkQtyCartItems));
            
            
            
        }else{
            responseObject.addProperty("message", "1");
        }
        
        }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             
            
        }
        
        
        
    }

   

}
