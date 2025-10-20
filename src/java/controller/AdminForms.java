/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
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
@WebServlet(name = "AdminForms", urlPatterns = {"/AdminForms"})
public class AdminForms extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (request.getSession().getAttribute("admin") != null) {

            Session s = HibernateUtil.getSessionFactory().openSession();
            Criteria c1 = s.createCriteria(Category.class);
            List<Category> categoryList = c1.list();

            responseObject.add("categoryList", gson.toJsonTree(categoryList));
            responseObject.addProperty("status", true);

        } else {
            responseObject.addProperty("message", "1");
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        Session s = null;
        Transaction tr= null;
        
        try{
        
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);

        String category = requestObject.get("category").getAsString();

        if (request.getSession().getAttribute("admin") != null) {
             s = HibernateUtil.getSessionFactory().openSession();
            if (category.isEmpty()) {
                responseObject.addProperty("message", "Please add a Category Name");
            } else {
                Criteria c1 = s.createCriteria(Category.class);
                c1.add(Restrictions.eq("name", category));
                List<Category> categoryList = c1.list();
                if (!categoryList.isEmpty()) {
                    responseObject.addProperty("message", "Category already exists");
                } else {
                    Category catObject = new Category();
                    catObject.setName(category);

                    s.save(catObject);
                    s.beginTransaction().commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "New Category added");
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
