/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.PortionSize;
import hibernate.Quality;
import hibernate.SpiceLevel;
import hibernate.Status;
import hibernate.SubCategory;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "LoadData", urlPatterns = {"/LoadData"})
public class LoadData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {
            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c1 = s.createCriteria(Category.class);
            List<Category> categoryList = c1.list();

            Criteria c2 = s.createCriteria(SubCategory.class);
            List<SubCategory> subCategoryList = c2.list();

            Criteria c3 = s.createCriteria(SpiceLevel.class);
            List<SpiceLevel> spiceLevelList = c3.list();

            Criteria c4 = s.createCriteria(PortionSize.class);
            List<PortionSize> portionSizeList = c4.list();

            Criteria c5 = s.createCriteria(Quality.class);
            List<Quality> qualityList = c5.list();

            Criteria c6 = s.createCriteria(FoodItem.class);
            c6.addOrder(Order.desc("id"));
            c6.add(Restrictions.eq("status.id", 1));
            c6.add(Restrictions.gt("qty", 0));
            responseObject.addProperty("allProductCount", c6.list().size());
            c6.setFirstResult(0);
            c6.setMaxResults(6);

            List<FoodItem> foodItemList = c6.list();
            for (FoodItem foodItem : foodItemList) {
                foodItem.setUser(null);
            }

            Gson gson = new Gson();
            responseObject.add("categoryList", gson.toJsonTree(categoryList));
            responseObject.add("subCategoryList", gson.toJsonTree(subCategoryList));
            responseObject.add("spiceLevelList", gson.toJsonTree(spiceLevelList));
            responseObject.add("portionSizeList", gson.toJsonTree(portionSizeList));
            responseObject.add("qualityList", gson.toJsonTree(qualityList));
            responseObject.add("foodItemList", gson.toJsonTree(foodItemList));

            responseObject.addProperty("status", true);

            String toJson = gson.toJson(responseObject);

            response.setContentType("application/json");
            response.getWriter().write(toJson);
            s.close();

        } catch (Exception e) {
            responseObject.addProperty("message", "An unexpected error occurred");

        }

    }

}
