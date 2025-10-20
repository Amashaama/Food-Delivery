package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.SubCategory;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "AdminForms3", urlPatterns = {"/AdminForms3"})
public class AdminForms3 extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        Session  s = null;
        Transaction tr = null;
        
        try{
        
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String city = requestObject.get("city").getAsString();

        if (request.getSession().getAttribute("admin") != null) {
           s = HibernateUtil.getSessionFactory().openSession();

            if (city.isEmpty()) {
                responseObject.addProperty("message", "Please add a City");
            } else {

                Criteria c1 = s.createCriteria(City.class);
                c1.add(Restrictions.eq("name", city));
                List<City> cityList = c1.list();

                if (!cityList.isEmpty()) {
                    responseObject.addProperty("message", "City already exist");

                } else {
                    City cityObject = new City();
                    cityObject.setName(city);
                    s.save(cityObject);
                    s.beginTransaction().commit();
                     responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "New City added");
                    

                }

            }

        } else {
            responseObject.addProperty("message", "1");
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
