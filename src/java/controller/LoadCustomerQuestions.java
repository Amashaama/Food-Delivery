/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.Questions;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "LoadCustomerQuestions", urlPatterns = {"/LoadCustomerQuestions"})
public class LoadCustomerQuestions extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {

            String parameter = request.getParameter("id");

            if (Util.isInteger(parameter)) {

                Session s = HibernateUtil.getSessionFactory().openSession();

                FoodItem foodItem = (FoodItem) s.get(FoodItem.class, Integer.parseInt(parameter));

                Criteria c2 = s.createCriteria(Questions.class);
                c2.add(Restrictions.eq("foodItem", foodItem));
                c2.add(Restrictions.eq("question_status", "Answered"));
                List<Questions> questionList = c2.list();

                if (!questionList.isEmpty()) {

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Product questions successfully loaded");
                    responseObject.add("questionList", gson.toJsonTree(questionList));
                }

            }

        } catch (Exception e) {
            responseObject.addProperty("message", "An unexpected error occurred");

        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }

}
