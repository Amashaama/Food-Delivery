
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.Quality;
import hibernate.SpiceLevel;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
@WebServlet(name = "AdminForms6", urlPatterns = {"/AdminForms6"})
public class AdminForms6 extends HttpServlet {

     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String qualityName = requestObject.get("qualityName").getAsString();

        if (request.getSession().getAttribute("admin") != null) {
            Session s = HibernateUtil.getSessionFactory().openSession();
              if (qualityName.isEmpty()) {
                responseObject.addProperty("message", "Please add a Food Item Quality Level");
            } else {

                Criteria c1 = s.createCriteria(Quality.class);
                c1.add(Restrictions.eq("value", qualityName));
                List<Quality> qualityList = c1.list();

                if (!qualityList.isEmpty()) {
                    responseObject.addProperty("message", "Quality type already exist");

                } else {
                    Quality qualityObject = new Quality();
                    qualityObject.setValue(qualityName);
                    s.save(qualityObject);
                    s.beginTransaction().commit();
                     responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "New Quality Level added");
                    

                }

            }
           
            

        } else {
            responseObject.addProperty("message", "1");
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }


}
