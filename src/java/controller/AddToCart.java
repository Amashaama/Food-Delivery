/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        Session s = null;
        Transaction tr = null;

        try {

            String prId = request.getParameter("prId");
            String qty = request.getParameter("qty");

            if (!Util.isInteger(prId)) {
                responseObject.addProperty("message", "Invalid product Id");
            } else if (!Util.isInteger(qty)) {
                responseObject.addProperty("message", "invalid Quantity");
            } else if (qty.equals("0")) {
                responseObject.addProperty("message", "Quantity Can Not Be Zero");
            } else {

                 s = HibernateUtil.getSessionFactory().openSession();
                 tr = s.beginTransaction();

                FoodItem foodItem = (FoodItem) s.get(FoodItem.class, Integer.valueOf(prId));

                if (foodItem == null) {
                    responseObject.addProperty("message", "Item not found");
                } else {
                    User user = (User) request.getSession().getAttribute("user");

                    if (user != null) {

                        Criteria c1 = s.createCriteria(Cart.class);
                        c1.add(Restrictions.eq("user", user));
                        c1.add(Restrictions.eq("foodItem", foodItem));

                        if (c1.list().isEmpty()) {

                            if (Integer.parseInt(qty) <= foodItem.getQty()) {
                                Cart cart = new Cart();
                                cart.setFoodItem(foodItem);
                                cart.setQty(Integer.parseInt(qty));
                                cart.setUser(user);

                                s.save(cart);
                                tr.commit();
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "Food Item added to cart.");

                            } else {
                                responseObject.addProperty("message", "Insufficient FoodItem quantity");
                            }

                        } else {

                            Cart cart = (Cart) c1.uniqueResult();
                            int newQty = cart.getQty() + Integer.parseInt(qty);

                            if (newQty <= foodItem.getQty()) {
                                cart.setQty(newQty);
                                s.update(cart);
                                tr.commit();
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", " Food Item cart updated...");
                            } else {
                                responseObject.addProperty("message", "Insufficient FoodItem quantity");
                            }

                        }

                    } else {

                        HttpSession ses = request.getSession();

                        if (ses.getAttribute("sessionCart") == null) {

                            if (Integer.parseInt(qty) <= foodItem.getQty()) {

                                ArrayList<Cart> sessCarts = new ArrayList<>();
                                Cart cart = new Cart();
                                cart.setUser(null);
                                cart.setFoodItem(foodItem);
                                cart.setQty(Integer.parseInt(qty));
                                sessCarts.add(cart);
                                ses.setAttribute("sessionCart", sessCarts);
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "FoodItem added to cart");

                            } else {
                                responseObject.addProperty("message", "Insufficient FoodItem quantity");
                            }
                        } else {

                            ArrayList<Cart> sessionList = (ArrayList<Cart>) ses.getAttribute("sessionCart");

                            Cart foundedCart = null;
                            for (Cart cart : sessionList) {
                                if (cart.getFoodItem().getId() == foodItem.getId()) {
                                    foundedCart = cart;
                                    break;
                                }
                            }

                            if (foundedCart != null) {
                                int newQty = foundedCart.getQty() + Integer.parseInt(qty);
                                if (newQty <= foodItem.getQty()) {

                                    foundedCart.setQty(newQty);
                                    responseObject.addProperty("status", true);
                                    responseObject.addProperty("message", " Food Item cart updated...");

                                } else {
                                    responseObject.addProperty("message", "Insufficient FoodItem quantity");
                                }

                            } else {

                                if (Integer.parseInt(qty) <= foodItem.getQty()) {

                                    foundedCart = new Cart();
                                    foundedCart.setQty(Integer.parseInt(qty));
                                    foundedCart.setUser(user);
                                    foundedCart.setFoodItem(foodItem);
                                    sessionList.add(foundedCart);
                                    responseObject.addProperty("status", true);
                                    responseObject.addProperty("message", "FoodItem added to cart");
                                } else {
                                    responseObject.addProperty("message", "Insufficient FoodItem quantity");
                                }

                            }

                        }

                    }

                }

            }

        } catch (Exception e) {
            
             responseObject.addProperty("message", "An unexpected error occurred");
             tr.rollback();
             
        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }

   

}
