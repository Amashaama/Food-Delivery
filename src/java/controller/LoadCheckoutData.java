/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.City;
import hibernate.DeliveryType;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
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
@WebServlet(name = "LoadCheckoutData", urlPatterns = {"/LoadCheckoutData"})
public class LoadCheckoutData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();

        responseObject.addProperty("status", false);

        try {

            User sessionUser = (User) request.getSession().getAttribute("user");

            if (sessionUser == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseObject.addProperty("message", "401");
            } else {

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria c1 = s.createCriteria(Address.class);
                c1.add(Restrictions.eq("user", sessionUser));
                c1.addOrder(Order.desc("id"));

                if (c1.list().isEmpty()) {
                    responseObject.addProperty("message", "Your account details are incomplete. Please fill your shipping address");
                } else {
                    Address address = (Address) c1.list().get(0);
                    address.getUser().setEmail(null);
                    address.getUser().setPassword(null);
                    address.getUser().setVerification(null);
                    address.getUser().setId(-1);
                    address.getUser().setCreated_at(null);

                    responseObject.add("userAddress", gson.toJsonTree(address));
                    responseObject.addProperty("status", true);

                }

                Criteria c2 = s.createCriteria(City.class);
                c2.addOrder(Order.asc("name"));
                List<City> cityList = c2.list();
                responseObject.add("cityList", gson.toJsonTree(cityList));

                //get fooditems above > 0
                Criteria availableFoodItems = s.createCriteria(FoodItem.class);
                availableFoodItems.add(Restrictions.gt("qty", 0));

                List<FoodItem> availableList = availableFoodItems.list();

                Criteria c3 = s.createCriteria(Cart.class);
                c3.add(Restrictions.eq("user", sessionUser));
                c3.add(Restrictions.in("foodItem", availableList));
                List<Cart> cartList = c3.list();
                if (cartList.isEmpty()) {
                    responseObject.addProperty("status", false);
                    responseObject.addProperty("message", "Empty cart");
                } else {
                    for (Cart cart : cartList) {
                        cart.setUser(null);
                        cart.getFoodItem().setUser(null);
                    }

                    Criteria c4 = s.createCriteria(DeliveryType.class);
                    List<DeliveryType> deliveryTypes = c4.list();

                    responseObject.add("deliveryTypes", gson.toJsonTree(deliveryTypes));
                    responseObject.add("cartList", gson.toJsonTree(cartList));

                    responseObject.addProperty("status", true);

                }
                s.close();

            }

        } catch (Exception e) {
            responseObject.addProperty("message", "An unexpected error occurred");

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
