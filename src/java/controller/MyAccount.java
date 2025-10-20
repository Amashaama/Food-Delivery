/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.Questions;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
@WebServlet(name = "MyAccount", urlPatterns = {"/MyAccount"})
public class MyAccount extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession();
          
        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");
          JsonObject responseObject = new JsonObject();
            responseObject.addProperty("firstName", user.getFirst_name());
            responseObject.addProperty("lastName", user.getLast_name());
            responseObject.addProperty("password", user.getPassword());
            responseObject.addProperty("mobile", user.getMobile());

            String since = new SimpleDateFormat("MMM yyyy").format(user.getCreated_at());
            responseObject.addProperty("since", since);

            Gson gson = new Gson();

            Session s = HibernateUtil.getSessionFactory().openSession();
            Criteria c = s.createCriteria(Address.class);
            c.add(Restrictions.eq("user", user));

            if (!c.list().isEmpty()) {

                List<Address> addressList = c.list();
                responseObject.add("addressList", gson.toJsonTree(addressList));

            }

            //question 
            Criteria c2 = s.createCriteria(FoodItem.class);
            c2.add(Restrictions.eq("user", user));

            List<FoodItem> foodItemList = c2.list();
            
             List<Questions> questionList = new ArrayList<>();

            if (!foodItemList.isEmpty()) {
                Criteria c3 = s.createCriteria(Questions.class);
                c3.add(Restrictions.in("foodItem", foodItemList));
                
                 questionList = c3.list();
               
                
            }
            
             responseObject.add("questionList", gson.toJsonTree(questionList));

            String toJson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(toJson);

        }else{
          
        }

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
          JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
       
        Transaction tr= null;
        
        try{
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = userData.get("firstName").getAsString();
        String lastName = userData.get("lastName").getAsString();
        String mobile = userData.get("mobile").getAsString();
        String lineOne = userData.get("lineOne").getAsString();
        String lineTwo = userData.get("lineTwo").getAsString();
        String postalCode = userData.get("postalCode").getAsString();
        int cityId = userData.get("cityId").getAsInt();

      

        if (firstName.isEmpty()) {

            responseObject.addProperty("message", "First Name can not be empty.");

        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last Name can not be empty.");
        } else if (mobile.isEmpty()) {
            responseObject.addProperty("message", "Mobile can not be empty.");
        } else if (!Util.isMobileValid(mobile)) {
            responseObject.addProperty("message", "Invalid mobile number. Please enter a valid number");
        } else if (lineOne.isEmpty()) {
            responseObject.addProperty("message", "Address Line One can not be empty.");
        } else if (lineTwo.isEmpty()) {
            responseObject.addProperty("message", "Address Line two can not be empty.");
        } else if (postalCode.isEmpty()) {
            responseObject.addProperty("message", "Postal Code can not be empty.");
        } else if (!Util.isCodeValid(postalCode)) { //check is number
            responseObject.addProperty("message", "Postal Code must be a (4-5) long digits");
        } else if (cityId == 0) {
            responseObject.addProperty("message", "Select a city");
        }else if (!Util.isInteger(String.valueOf(cityId))) {
            responseObject.addProperty("message", "Invalid City");
        } else {

            HttpSession ses = request.getSession();

            if (ses.getAttribute("user") != null) {
                User u = (User) ses.getAttribute("user");

                Session s = HibernateUtil.getSessionFactory().openSession();

                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", u.getEmail()));

                if (!c.list().isEmpty()) {

                    User u1 = (User) c.list().get(0);
                    u1.setFirst_name(firstName);
                    u1.setLast_name(lastName);
                    u1.setMobile(mobile);

                    City city = (City) s.load(City.class, cityId);

                    Criteria addressCriteria = s.createCriteria(Address.class);
                    addressCriteria.add(Restrictions.eq("lineOne", lineOne));
                    addressCriteria.add(Restrictions.eq("lineTwo", lineTwo));
                    addressCriteria.add(Restrictions.eq("postalCode", postalCode));
                    addressCriteria.add(Restrictions.eq("city", city));
                    addressCriteria.add(Restrictions.eq("user", u1));

                    if (addressCriteria.list().isEmpty()) {

                        Address address = new Address();
                        address.setLineOne(lineOne);
                        address.setLineTwo(lineTwo);
                        address.setPostalCode(postalCode);
                        address.setCity(city);
                        address.setUser(u1);

                        s.save(address);
                    }

                    ses.setAttribute("user", u1);

                    s.merge(u1);
                    //  s.save(address);

                    s.beginTransaction().commit();

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "111"); //successfilly updated
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
