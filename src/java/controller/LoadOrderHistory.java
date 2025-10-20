/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.Orders;
import hibernate.User;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "LoadOrderHistory", urlPatterns = {"/LoadOrderHistory"})
public class LoadOrderHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        try{

        Session s = HibernateUtil.getSessionFactory().openSession();

        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {
            Criteria c1 = s.createCriteria(Orders.class);
            c1.add(Restrictions.eq("user", user));
            c1.addOrder(Order.desc("id"));
            List<Order> orderList = c1.list();

            if (!orderList.isEmpty()) {
                Criteria c2 = s.createCriteria(OrderItem.class);
                c2.add(Restrictions.in("orders", orderList));
                List<OrderItem> orderItemsList = c2.list();

                responseObject.add("orderList", gson.toJsonTree(orderList));
               responseObject.addProperty("allOrdersCount", c1.list().size());
                responseObject.add("orderItemsList", gson.toJsonTree(orderItemsList));
                responseObject.addProperty("status", true);
            }
             
        }
        
        }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             
            
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
