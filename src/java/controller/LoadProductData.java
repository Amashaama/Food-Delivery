/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.SubCategory;
import hibernate.HibernateUtil;
import hibernate.PortionSize;
import hibernate.Quality;
import hibernate.SpiceLevel;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.enterprise.context.BeforeDestroyed;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 *
 * @author Anne
 */
@WebServlet(name = "LoadProductData", urlPatterns = {"/LoadProductData"})
public class LoadProductData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        try{
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        
        Criteria c1= s.createCriteria(Category.class);
        List<Category> categoryList = c1.list();
        
             
        Criteria c2= s.createCriteria(SubCategory.class);
        List<SubCategory> subCategoryList = c2.list();
        
        Criteria c3 = s.createCriteria(SpiceLevel.class);
        List<SpiceLevel> spiceLevelList = c3.list();
        
        Criteria c4 =s.createCriteria(PortionSize.class);
        List<PortionSize> portionSizeList = c4.list();
        
        Criteria c5 = s.createCriteria(Quality.class);
        List<Quality> qualityList = c5.list();
        
        
        
        Gson gson = new Gson();
        
        responseObject.addProperty("status",true);
        
        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("subCategoryList", gson.toJsonTree(subCategoryList));
        responseObject.add("spiceLevelList", gson.toJsonTree(spiceLevelList));
        responseObject.add("portionSizeList", gson.toJsonTree(portionSizeList));
        responseObject.add("qualityList", gson.toJsonTree(qualityList));
        
        
    
       response.setContentType("application/json");
       response.getWriter().write(gson.toJson(responseObject));
       
       }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             
            
        }
        
        
    }

  

}
