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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "AdminForms2", urlPatterns = {"/AdminForms2"})
public class AdminForms2 extends HttpServlet {

  @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requestObject = gson.fromJson(request.getReader(), JsonObject.class);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

       
        int categorySelect = requestObject.get("categorySelect").getAsInt();
        String subCategory = requestObject.get("subCategory").getAsString();
      

        if (request.getSession().getAttribute("admin") != null) {
            Session s = HibernateUtil.getSessionFactory().openSession();
           
            if (!Util.isInteger(String.valueOf(categorySelect))) {
                responseObject.addProperty("message", "Invalid Category");
            } else if (categorySelect == 0) {
                responseObject.addProperty("message", "Please select a Category");
            } else if (subCategory.isEmpty()) {
                responseObject.addProperty("message", "Please add a Sub Category");
            } else {

                Category categorySelected = (Category) s.get(Category.class, categorySelect);
                Criteria c2 = s.createCriteria(SubCategory.class);
                c2.add(Restrictions.eq("category", categorySelected));
                c2.add(Restrictions.eq("name", subCategory));
                List<SubCategory> subCategoryList = c2.list();
                if (!subCategoryList.isEmpty()) {
                    responseObject.addProperty("message", "Sub Category already exists");
                } else {
                    SubCategory subCatObject = new SubCategory();
                    subCatObject.setCategory(categorySelected);
                    subCatObject.setName(subCategory);

                    s.save(subCatObject);
                    s.beginTransaction().commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "New Sub Category added");

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
