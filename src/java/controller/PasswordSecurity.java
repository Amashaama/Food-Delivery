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
@WebServlet(name = "PasswordSecurity", urlPatterns = {"/PasswordSecurity"})
public class PasswordSecurity extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        Transaction tr = null;
        
        try{
        
        JsonObject passwordData = gson.fromJson(request.getReader(), JsonObject.class);

        String currentPassword = passwordData.get("currentPassword").getAsString();
        String newPassword = passwordData.get("newPassword").getAsString();
        String confirmPassword = passwordData.get("confirmPassword").getAsString();

        

        if (currentPassword.isEmpty()) {
            responseObject.addProperty("message", "Enter your current password");
        } else if (newPassword.isEmpty()) {
            responseObject.addProperty("message", "Enter your new password");
        } else if (!Util.isPasswordValid(newPassword)) {
            responseObject.addProperty("message", "Password must be 8+ characters with a lowercase, uppercase, number, and special character");
        } else if (newPassword.equals(currentPassword)) {
            responseObject.addProperty("message", "New password cannot match current password");
        } else if (confirmPassword.isEmpty()) {
            responseObject.addProperty("message", "Confirm your new password");
        } else if (!Util.isPasswordValid(confirmPassword)) {
            responseObject.addProperty("message", "Password must be 8+ characters with a lowercase, uppercase, number, and special character");
        } else if (!confirmPassword.equals(newPassword)) {
            responseObject.addProperty("message", "Confirm password does not match new password");
        } else {

            HttpSession ses = request.getSession();

            if (ses.getAttribute("user") != null) {

                User u = (User) ses.getAttribute("user");

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", u.getEmail()));

                if (!c.list().isEmpty()) {

                    User u1 = (User) c.list().get(0);

                    if (!confirmPassword.isEmpty()) {

                        u1.setPassword(confirmPassword);

                    } else {
                        u1.setPassword(currentPassword);
                    }

                    ses.setAttribute("user", u1);

                    s.merge(u1);

                    s.beginTransaction().commit();
                    responseObject.addProperty("status", true);
                     responseObject.addProperty("message", "111"); 
                    s.close();

                }

            }

        }
        
        }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             tr.rollback();
            
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

}
