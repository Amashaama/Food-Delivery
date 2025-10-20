
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.City;
import hibernate.HibernateUtil;
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
@WebServlet(name = "AdminForms5", urlPatterns = {"/AdminForms5"})
public class AdminForms5 extends HttpServlet {

     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

      
        
        
        String spiceLevelName = requestObject.get("spiceLevelName").getAsString();
       

        if (request.getSession().getAttribute("admin") != null) {
            Session s = HibernateUtil.getSessionFactory().openSession();
           
            if (spiceLevelName.isEmpty()) {
                responseObject.addProperty("message", "Please add a Spice Level");
            } else {

                Criteria c1 = s.createCriteria(SpiceLevel.class);
                c1.add(Restrictions.eq("name", spiceLevelName));
                List<SpiceLevel> spiceList = c1.list();

                if (!spiceList.isEmpty()) {
                    responseObject.addProperty("message", "Spice Level already exist");

                } else {
                    SpiceLevel spiceObject = new SpiceLevel();
                    spiceObject.setName(spiceLevelName);
                    s.save(spiceObject);
                    s.beginTransaction().commit();
                     responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "New Spice added");
                    

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
