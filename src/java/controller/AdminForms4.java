
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.PortionSize;
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
@WebServlet(name = "AdminForms4", urlPatterns = {"/AdminForms4"})
public class AdminForms4 extends HttpServlet {
     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

      
        
        String portionName = requestObject.get("portionName").getAsString();
      

        if (request.getSession().getAttribute("admin") != null) {
            Session s = HibernateUtil.getSessionFactory().openSession();
           
           if(portionName.isEmpty()){
                  responseObject.addProperty("message", "Please add a Portion Size");
           }else{
               
                Criteria c1 = s.createCriteria(PortionSize.class);
                c1.add(Restrictions.eq("name", portionName));
                List<PortionSize> portionList = c1.list();

                if (!portionList.isEmpty()) {
                    responseObject.addProperty("message", "Portion Size already exist");

                } else {
                    PortionSize portionObject = new PortionSize();
                    portionObject.setName(portionName);
                    s.save(portionObject);
                    s.beginTransaction().commit();
                     responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "New Portion Size added");
                    

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
