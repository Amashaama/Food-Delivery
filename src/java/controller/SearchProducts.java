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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    private static final int MAX_RESULT = 6;
    private static final int ACTIVE_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {

            JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

            Session s = HibernateUtil.getSessionFactory().openSession();

            Criteria c1 = s.createCriteria(FoodItem.class);

            if (requestJsonObject.has("searchQuery")) {
                String keyWord = requestJsonObject.get("searchQuery").getAsString();
                if (!keyWord.isEmpty()) {
                    c1.add(Restrictions.ilike("title", "%" + keyWord + "%"));
                }
            } else {
                responseObject.addProperty("message", "No results");
            }

            if (requestJsonObject.has("category")) {

                int categoryId = requestJsonObject.get("category").getAsInt();
                if (categoryId != 0) {
                    //get category details
                    Criteria c2 = s.createCriteria(Category.class);
                    c2.add(Restrictions.eq("id", categoryId));
                    Category category = (Category) c2.uniqueResult();

                    Criteria c3 = s.createCriteria(SubCategory.class);
                    c3.add(Restrictions.eq("category", category));
                    List<SubCategory> subCatList = c3.list();

                    if (subCatList != null && !subCatList.isEmpty()) {

                        c1.add(Restrictions.in("subCategory", subCatList));
                    } else {
                        responseObject.addProperty("message", "No results");
                    }
                }
            }

            if (requestJsonObject.has("portionName")) {

                String portionName = requestJsonObject.get("portionName").getAsString();

                Criteria c4 = s.createCriteria(PortionSize.class);
                c4.add(Restrictions.eq("name", portionName));
                PortionSize portionSize = (PortionSize) c4.uniqueResult();

                c1.add(Restrictions.eq("portionSize", portionSize));

            }

            if (requestJsonObject.has("qualityName")) {

                String qualityName = requestJsonObject.get("qualityName").getAsString();

                Criteria c5 = s.createCriteria(Quality.class);
                c5.add(Restrictions.eq("value", qualityName));
                Quality quality = (Quality) c5.uniqueResult();

                c1.add(Restrictions.eq("quality", quality));

            }

            if (requestJsonObject.has("spiceName")) {
                String spiceName = requestJsonObject.get("spiceName").getAsString();

                Criteria c6 = s.createCriteria(SpiceLevel.class);
                c6.add(Restrictions.eq("name", spiceName));
                SpiceLevel spiceLevel = (SpiceLevel) c6.uniqueResult();

                c1.add(Restrictions.eq("spiceLevel", spiceLevel));

            }

            if (requestJsonObject.has("minPrice") && !requestJsonObject.get("minPrice").getAsString().isEmpty()) {

                String minPrice = requestJsonObject.get("minPrice").getAsString();

                if (Util.isDouble(minPrice)) {

                    double priceStart = requestJsonObject.get("minPrice").getAsDouble();
                    c1.add(Restrictions.ge("price", priceStart));
                } else {
                    responseObject.addProperty("message", "Invalid Min Price");
                }

            }

            if (requestJsonObject.has("maxPrice") && !requestJsonObject.get("maxPrice").getAsString().isEmpty()) {

                String maxPrice = requestJsonObject.get("maxPrice").getAsString();

                if (Util.isDouble(maxPrice)) {

                    double priceEnd = requestJsonObject.get("maxPrice").getAsDouble();
                    c1.add(Restrictions.le("price", priceEnd));
                } else {
                    responseObject.addProperty("message", "Invalid Max Price");
                }

            }

            if (requestJsonObject.has("sortValue")) {
                String sortValue = requestJsonObject.get("sortValue").getAsString();

                if (sortValue.equals("price-low")) {
                    c1.addOrder(Order.asc("price"));
                } else if (sortValue.equals("price-high")) {
                    c1.addOrder(Order.desc("price"));
                } else if (sortValue.equals("newest")) {
                    c1.addOrder(Order.desc("id"));
                } else if (sortValue.equals("rating")) {
                    c1.addOrder(Order.asc("id"));
                } else if (sortValue.equals("popular")) {
                    c1.addOrder(Order.asc("title"));
                }

            }

            Status status = (Status) s.get(Status.class, SearchProducts.ACTIVE_ID);
            c1.add(Restrictions.eq("status", status));

            c1.add(Restrictions.gt("qty", 0));

            responseObject.addProperty("allProductCount", c1.list().size());

            if (requestJsonObject.has("firstResult")) {

                int firstResult = requestJsonObject.get("firstResult").getAsInt();
                c1.setFirstResult(firstResult);
                c1.setMaxResults(SearchProducts.MAX_RESULT);

            }

            List<FoodItem> foodItemList = c1.list();
            for (FoodItem item : foodItemList) {
                item.setUser(null);
            }

            s.close();

            responseObject.add("foodItemList", gson.toJsonTree(foodItemList));
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("message", "An unexpected error occurred");

        }
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }

}
