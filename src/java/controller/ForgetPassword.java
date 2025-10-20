/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "ForgetPassword", urlPatterns = {"/ForgetPassword"})
public class ForgetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
           JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        Session s = null;
        Transaction tr = null;
        try{
        
        JsonObject jsonRequest = gson.fromJson(request.getReader(), JsonObject.class);

        String email = jsonRequest.get("email").getAsString();

     

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email cannot be empty.");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid email format.");
        } else {
             s = HibernateUtil.getSessionFactory().openSession();
            Criteria c1 = s.createCriteria(User.class);
            c1.add(Restrictions.eq("email", email));

            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "No account found with this email.");
            } else {
                User u = (User) c1.uniqueResult();
                String resetCode = Util.passwordVerificationgenarateCode();

                u.setForgetVerification(resetCode);
                s.merge(u);
                s.beginTransaction().commit();

                final String emailHtmlContent
                        = "<html><body>"
                        + "<h2>Password Reset Request</h2>"
                        + "<p>Your reset code is:</p>"
                        + "<h3 style='color: red;'>" + resetCode + "</h3>"
                        + "<p>Use this code to reset your password.</p>"
                        + "</body></html>";

                new Thread(() -> {
                    Mail.sendMail(email, "Password Reset Code", emailHtmlContent);
                }).start();

                request.getSession().setAttribute("email", email);

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Reset code sent to your email.");

            }
            s.close();

        }
        }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             tr.rollback();
            
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
