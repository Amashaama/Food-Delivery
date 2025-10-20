/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "LordCartItems", urlPatterns = {"/LordCartItems"})
public class LordCartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        Session s = null;
        Transaction tr = null;
        
        try{

        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {

             s = HibernateUtil.getSessionFactory().openSession();

            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();

            if (cartList.isEmpty()) {
                responseObject.addProperty("message", "Your cart is empty");
            } else {
                for (Cart cart : cartList) {
                    cart.getFoodItem().setUser(null);
                    cart.setUser(null);
                }

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Cart items loaded");
                responseObject.add("cartItems", gson.toJsonTree(cartList));

            }

        } else {

            ArrayList<Cart> sessionCarts = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");

            if (sessionCarts != null) {
                if (sessionCarts.isEmpty()) {
                    responseObject.addProperty("message", "Your cart is empty");
                } else {
                    for (Cart cart : sessionCarts) {
                        cart.getFoodItem().setUser(null);
                        cart.setUser(null);
                    }

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Cart items loaded");
                    responseObject.add("cartItems", gson.toJsonTree(sessionCarts));

                }
            } else {
                responseObject.addProperty("message", "Your cart is empty");
            }

        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
        
        }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             tr.rollback();
            
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
         JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        
        JsonObject requestJsonData = gson.fromJson(request.getReader(), JsonObject.class);
        int cartId = requestJsonData.get("cartId").getAsInt();
        int foodItemId = requestJsonData.get("foodItemId").getAsInt();

       

        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {
            Session s = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = s.beginTransaction();
            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("id", cartId));
            c1.add(Restrictions.eq("foodItem.id", foodItemId));
            c1.add(Restrictions.eq("user", user));

            List<Cart> cartList = (List<Cart>) c1.list();

            if (!cartList.isEmpty()) {
                for (Cart cart : cartList) {
                    s.delete(cart);
                    
                }
                
                tr.commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Cart Item removed successfully");
            }else{
                responseObject.addProperty("message", "Item not found in cart");
            }

        }else{
          ArrayList<Cart> sessionCarts  = (ArrayList<Cart>)request.getSession().getAttribute("sessionCart");
          
          if(sessionCarts != null && !sessionCarts.isEmpty()){
              
             boolean removed= sessionCarts.removeIf(cart ->
                 cart.getId() == cartId && cart.getFoodItem().getId()==foodItemId
              
              );
             
             if(removed){
                 responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Item removed from cart.");
             }else{
                   responseObject.addProperty("message", "Item not found in cart.");
             }
              
          }else{
               responseObject.addProperty("message", "Your cart is empty.");
          }
          
          
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}


