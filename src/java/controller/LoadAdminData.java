/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.xml.ws.security.opt.impl.util.SOAPUtil;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.OrderStatus;
import hibernate.Status;
import hibernate.User;
import hibernate.UserType;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
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
@WebServlet(name = "LoadAdminData", urlPatterns = {"/LoadAdminData"})
public class LoadAdminData extends HttpServlet {

    LocalDate today = LocalDate.now();
    int currentMonth = today.getMonthValue();
    int currentYear = today.getYear();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        try{

        if (request.getSession().getAttribute("admin") != null) {

            Session s = HibernateUtil.getSessionFactory().openSession();

            // most sold item
            Criteria c1 = s.createCriteria(OrderItem.class);
            List<OrderItem> orderItemsList = c1.list();
            
           
            
            responseObject.add("orderItemList",gson.toJsonTree(orderItemsList));

            Map<FoodItem, Integer> itemCountMap = new HashMap<>();

            Map<User, Integer> customerOrderMap = new HashMap<>();

            double dailyEarnings = 0;
            double monthlyEarnings = 0;

            for (OrderItem item : orderItemsList) {
                FoodItem food = item.getFoodItem();
                int qty = item.getQty();

                if (itemCountMap.containsKey(food)) {
                    itemCountMap.put(food, itemCountMap.get(food) + qty);
                } else {
                    itemCountMap.put(food, qty);
                }

                User customer = item.getOrders().getUser();

                if (customerOrderMap.containsKey(customer)) {
                    customerOrderMap.put(customer, customerOrderMap.get(customer) + qty);
                } else {
                    customerOrderMap.put(customer, qty);
                }

                Date orderDate = item.getOrders().getCreated_at();
                LocalDate localDate = orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                double price = item.getFoodItem().getPrice();
                double total = qty * price;

                if (localDate.equals(today)) {
                    dailyEarnings += total;
                }

                if (localDate.getMonthValue() == currentMonth && localDate.getYear() == currentYear) {
                    monthlyEarnings += total;
                }

            }

            FoodItem mostSold = null;
            int maxQty = 0;

            for (Map.Entry<FoodItem, Integer> entry : itemCountMap.entrySet()) {
                if (entry.getValue() > maxQty) {
                    maxQty = entry.getValue();
                    mostSold = entry.getKey();

                }
            }

            User bestCustomer = null;
            int maxCustomerQty = 0;

            for (Map.Entry<User, Integer> entry : customerOrderMap.entrySet()) {
                if (entry.getValue() > maxCustomerQty) {
                    maxCustomerQty = entry.getValue();
                    bestCustomer = entry.getKey();
                }
            }

            if (mostSold != null) {

                responseObject.addProperty("mostSoldItem", mostSold.getTitle());
                responseObject.addProperty("mostSoldQty", maxQty);
            }

            if (bestCustomer != null) {
                responseObject.addProperty("bestCustomer", bestCustomer.getFirst_name() + " " + bestCustomer.getLast_name());
                responseObject.addProperty("bestCustomerQty", maxCustomerQty);
            }

            responseObject.addProperty("dailyEarnings", dailyEarnings);
            responseObject.addProperty("monthlyEarnings", monthlyEarnings);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            responseObject.addProperty("today", sdf.format(new Date()));

            // load fooditems table
            Criteria c2 = s.createCriteria(FoodItem.class);
            List<FoodItem> foodItemList = c2.list();

            Criteria c3 = s.createCriteria(Status.class);
            List<Status> statusList = c3.list();

            if (!foodItemList.isEmpty()) {
                responseObject.add("foodItemList", gson.toJsonTree(foodItemList));
            }
            if (!statusList.isEmpty()) {
                responseObject.add("statusList", gson.toJsonTree(statusList));
            }
            
            // load users
            
            Criteria c4 = s.createCriteria(User.class);
            UserType userType = (UserType) s.get(UserType.class, 2);
            c4.add(Restrictions.ne("userType", userType));
            List<User> userList = c4.list();
            
            if(!c4.list().isEmpty()){
                responseObject.add("userList", gson.toJsonTree(userList));
            }
            
            responseObject.addProperty("status", true);
            
            Criteria c5 = s.createCriteria(OrderStatus.class);
            List<OrderStatus> orderStatusList = c5.list();
            
            if(!orderStatusList.isEmpty()){
            
            responseObject.add("orderStatusList", gson.toJsonTree(orderStatusList));
            }
        } else {
            responseObject.addProperty("message","1");
        }

         }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             
            
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
