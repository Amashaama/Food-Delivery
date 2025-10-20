/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.Status;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "ChangeFoodItemStatus", urlPatterns = {"/ChangeFoodItemStatus"})
public class ChangeFoodItemStatus extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Session s = null;
        Transaction tr = null;
        try {
            JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

            responseObject.addProperty("status", false);
            String fid = requestObject.get("foodId").getAsString();
            String sid = requestObject.get("status").getAsString();

            s = HibernateUtil.getSessionFactory().openSession();

            FoodItem foodItem = (FoodItem) s.get(FoodItem.class, Integer.parseInt(fid));
            Status status = (Status) s.get(Status.class, Integer.parseInt(sid));
            if (foodItem != null) {
                foodItem.setStatus(status);

                s.merge(foodItem);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "FoodItem Status Chaneged");

            } else {
                responseObject.addProperty("message", "FoodItem not founded");
            }
        } catch (Exception e) {
            responseObject.addProperty("message", "An unexpected error occurred");
            tr.rollback();

        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
