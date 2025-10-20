/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.FoodItem;
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
@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        try{
        
        String parameter = request.getParameter("id");

        if (Util.isInteger(parameter)) {
            Session s = HibernateUtil.getSessionFactory().openSession();
            try {

                FoodItem foodItem = (FoodItem) s.get(FoodItem.class, Integer.valueOf(parameter));

                if (foodItem.getStatus().getValue().equals("Available") && foodItem.getQty() >0 ) {

                    foodItem.getUser().setEmail(null);
                    foodItem.getUser().setPassword(null);
                    foodItem.getUser().setVerification(null);
                    foodItem.getUser().setId(-1);
                    foodItem.getUser().setCreated_at(null);

                    Criteria c1 = s.createCriteria(SubCategory.class);
                    c1.add(Restrictions.eq("category", foodItem.getSubCategory().getCategory()));
                    List<SubCategory> subCategoryList = c1.list();

                    Criteria c2 = s.createCriteria(FoodItem.class);
                    c2.add(Restrictions.in("subCategory", subCategoryList));
                    c2.add(Restrictions.ne("id", foodItem.getId()));
                    c2.setMaxResults(6);
                    List<FoodItem> foodItemList = c2.list();

                    for (FoodItem fi : foodItemList) {
                        fi.getUser().setEmail(null);
                        fi.getUser().setPassword(null);
                        fi.getUser().setVerification(null);
                        fi.getUser().setId(-1);
                        fi.getUser().setCreated_at(null);
                    }

                    responseObject.add("foodItem", gson.toJsonTree(foodItem));
                    responseObject.add("foodItemList", gson.toJsonTree(foodItemList));
                    responseObject.addProperty("status", true);

                } else {
                    responseObject.addProperty("message", "Product not found");
                }

            } catch (Exception e) {
                responseObject.addProperty("message", "Product not found");
            }

        }
        
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
        
        }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             
            
        }

    }

}
