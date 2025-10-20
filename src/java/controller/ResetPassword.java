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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "ResetPassword", urlPatterns = {"/ResetPassword"})
public class ResetPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject jsonReq = gson.fromJson(request.getReader(), JsonObject.class);

        String code = jsonReq.get("code").getAsString();
        String newPassword = jsonReq.get("newPassword").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (code.isEmpty()) {
            responseObject.addProperty("message", "Verification code cannot be empty.");
        } else if (newPassword.isEmpty()) {
            responseObject.addProperty("message", "Password cannot be empty.");
        } else if (!Util.isPasswordValid(newPassword)) {
            responseObject.addProperty("message", "Password must be 8+ chars with lowercase, uppercase, number, and special character.");
        } else {

            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c1 = s.createCriteria(User.class);
            c1.add(Restrictions.eq("forgetVerification", code));

            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid verification code.");
            } else {
                User u = (User) c1.uniqueResult();

                if (u.getPassword().equals(newPassword)) {
                     responseObject.addProperty("message", "New password cannot be the same as the old password.");
                } else {

                    u.setPassword(newPassword);
                    u.setForgetVerification(null);

                    s.merge(u);
                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Password reset successful.");
                }
            }

            s.close();

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
