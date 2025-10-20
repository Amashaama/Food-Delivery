/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.OrderStatus;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Anne
 */
@WebServlet(name = "ChangeOrderItemStatus", urlPatterns = {"/ChangeOrderItemStatus"})
public class ChangeOrderItemStatus extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
         JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        Session s = null;
        Transaction tr = null;
        
        try{
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);
       
        String oid = requestObject.get("oid").getAsString();
        String sid = requestObject.get("status").getAsString();

        s = HibernateUtil.getSessionFactory().openSession();

        OrderItem orderItem = (OrderItem) s.get(OrderItem.class, Integer.parseInt(oid));
        OrderStatus orderStatus = (OrderStatus) s.get(OrderStatus.class, Integer.parseInt(sid));

        if (orderItem != null) {

            orderItem.setOrderStatus(orderStatus);

            s.merge(orderItem);
            s.beginTransaction().commit();
            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Order Item Status Chaneged");

        } else {
            responseObject.addProperty("message", "Order Item not founded");
        }
        
        }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             tr.rollback();
            
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
