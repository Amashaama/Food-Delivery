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
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
@WebServlet(name = "SaveQuestion", urlPatterns = {"/SaveQuestion"})
public class SaveQuestion extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject questionData = gson.fromJson(request.getReader(), JsonObject.class);
        String question = questionData.get("question").getAsString();
        String productId = questionData.get("id").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Session s = HibernateUtil.getSessionFactory().openSession();

        if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "You have to Sign In to ask questions");
        } else if (question.isEmpty()) {
            responseObject.addProperty("message", "Please type your question");
        } else {

            User user = (User) request.getSession().getAttribute("user");

            FoodItem foodItem = (FoodItem) s.get(FoodItem.class, Integer.parseInt(productId));

            Questions questions = new Questions();
            questions.setFoodItem(foodItem);
            questions.setQuestion(question);
            questions.setUser(user);
            questions.setDate(new Date());
            questions.setQuestion_status("Pending");

            s.save(questions);

            s.beginTransaction().commit();
            s.close();
            responseObject.addProperty("status", true);
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject answerJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        String answer = answerJsonObject.get("answer").getAsString();
        String qId = answerJsonObject.get("itemId").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Session s = HibernateUtil.getSessionFactory().openSession();

        if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "Please sign in");
        } else if (answer.isEmpty()) {
            responseObject.addProperty("message", "Please type your answer");
        } else {
            Questions question = (Questions) s.get(Questions.class, Integer.parseInt(qId));

            if (question != null) {

                question.setAnswer(answer);
                question.setQuestion_status("Answered");

                s.merge(question);
                s.beginTransaction().commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Answer saved successfully");
                s.close();

            } else {
                responseObject.addProperty("message", "Question not found");
            }

        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
