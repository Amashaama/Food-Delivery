package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.UserType;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {
            JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

            String firstName = user.get("firstName").getAsString();
            String lastName = user.get("lastName").getAsString();
            final String email = user.get("email").getAsString();
            String password = user.get("password").getAsString();
            String mobile = user.get("mobile").getAsString();

            if (firstName.isEmpty()) {
                responseObject.addProperty("message", "First Name can not be empty.");
            } else if (lastName.isEmpty()) {
                responseObject.addProperty("message", "Last Name can not be empty");
            } else if (email.isEmpty()) {
                responseObject.addProperty("message", "Email can not be empty");
            } else if (!Util.isEmailValid(email)) {
                responseObject.addProperty("message", "Please enter a valid email.");
            } else if (mobile.isEmpty()) {
                responseObject.addProperty("message", "Mobile can not be empty.");
            } else if (!Util.isMobileValid(mobile)) {
                responseObject.addProperty("message", "Invalid mobile number. Please enter a valid number");
            } else if (password.isEmpty()) {
                responseObject.addProperty("message", "Password can not be empty.");
            } else if (!Util.isPasswordValid(password)) {
                responseObject.addProperty("message", "Password must be 8+ characters with a lowercase, uppercase, number, and special character");
            } else {

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria criteria = s.createCriteria(User.class);
                criteria.add(Restrictions.eq("email", email));

                UserType userType = (UserType) s.get(UserType.class, 1);

                if (!criteria.list().isEmpty()) {
                    responseObject.addProperty("message", "User with this email already exists.");
                } else {
                    User u = new User();

                    u.setFirst_name(firstName);
                    u.setLast_name(lastName);
                    u.setEmail(email);
                    u.setPassword(password);
                    u.setMobile(mobile);
                    u.setUserType(userType);

                    final String verificationCode = Util.genarateCode();
                    u.setVerification(verificationCode);
                    u.setCreated_at(new Date());

                    s.save(u);
                    s.beginTransaction().commit();

                    final String emailhtmlContent
                            = "<html>"
                            + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                            + "<div style='background-color: #ffffff; padding: 20px; border-radius: 6px; max-width: 500px; margin: auto;'>"
                            + "<h2 style='color: #ff6f61;'>Welcome to FoodMart Food Delivery!</h2>"
                            + "<p>Thank you for signing up.</p>"
                            + "<p>Your verification code is:</p>"
                            + "<div style='text-align: center; margin: 20px 0;'>"
                            + "<span style='display: inline-block; padding: 10px 20px; font-size: 24px; background-color: #ff6f61; color: #ffffff; border-radius: 4px;'>" + verificationCode + "</span>"
                            + "</div>"
                            + "<p>If you did not request this, you can ignore this email.</p>"
                            + "<p style='color: #888888;'>FoodMart Food Delivery</p>"
                            + "</div>"
                            + "</body>"
                            + "</html>";

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Mail.sendMail(email, "FoodMart Food Delivery Verification", emailhtmlContent);

                        }
                    }).start();

                    //create session
                    HttpSession ses = request.getSession();
                    ses.setAttribute("email", email);

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Registration Success. Please check your email for verification code.");

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
