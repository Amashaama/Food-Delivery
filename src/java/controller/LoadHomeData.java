/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.Status;
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
@WebServlet(name = "LoadHomeData", urlPatterns = {"/LoadHomeData"})
public class LoadHomeData extends HttpServlet {

    private static final int ACTIVE_STATUS_ID = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {

            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c1 = s.createCriteria(Category.class);
            List<Category> categoryList = c1.list();
            responseObject.add("categoryList", gson.toJsonTree(categoryList));

            Criteria c2 = s.createCriteria(FoodItem.class);
            c2.addOrder(Order.desc("id"));

            Status status = (Status) s.get(Status.class, LoadHomeData.ACTIVE_STATUS_ID);
            c2.add(Restrictions.eq("status", status));

            c2.add(Restrictions.gt("qty", 0));

            c2.setFirstResult(0);
            c2.setMaxResults(6);

            List<FoodItem> foodItemList = c2.list();

            for (FoodItem food : foodItemList) {
                food.setUser(null);
            }

            responseObject.add("foodItemList", gson.toJsonTree(foodItemList));
            responseObject.addProperty("status", true);
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));

        } catch (Exception e) {
            responseObject.addProperty("message", "An unexpected error occurred");

        }

    }

}
