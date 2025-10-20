/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.UserType;
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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        try{

        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String email = user.get("email").getAsString();
        String pw = user.get("password").getAsString();

        System.out.println(pw);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email can not be empty");

        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid Email");
        } else if (pw.isEmpty()) {
            responseObject.addProperty("message", "password can not be empty");
        } else {
            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c = s.createCriteria(User.class);

            Criterion crt1 = Restrictions.eq("email", email);
            Criterion crt2 = Restrictions.eq("password", pw);

            c.add(crt1);
            c.add(crt2);

            if (c.list().isEmpty()) {
                responseObject.addProperty("message", "Invalid Credentials");
            } else {
                UserType userType1 = (UserType) s.get(UserType.class, 2);
                User u = (User) c.list().get(0);
                responseObject.addProperty("status", true);
               
                HttpSession ses = request.getSession();

                if (u.getUserType().equals(userType1)) {
                    // Admin
                    ses.setAttribute("admin", u);
                    responseObject.addProperty("message", "admin");

                } else {
                    // Regular user
                    if (!u.getVerification().equals("Verified")) {
                        ses.setAttribute("email", email);
                        responseObject.addProperty("message", "1");
                    } else {
                        ses.setAttribute("user", u);
                        responseObject.addProperty("message", "2");
                    }
                }

            }
            s.close();
        }
        
        } catch (Exception e) {
            responseObject.addProperty("message", "An unexpected error occurred");

        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

    }

}
