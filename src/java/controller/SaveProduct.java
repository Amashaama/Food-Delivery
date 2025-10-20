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
import hibernate.User;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    private static final int PENDING_STATUS_ID = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

         JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        try{
        
        
        String categoryId = request.getParameter("categoryId");
        String subCategoryId = request.getParameter("subCategoryId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String portionSizeId = request.getParameter("portionSizeId");
        String spiceLevelId = request.getParameter("spiceLevelId");
        String qualityId = request.getParameter("qualityId");
        String price = request.getParameter("price");
        String qty = request.getParameter("qty");
        Part part1 = request.getPart("image1");
        Part part2 = request.getPart("image2");
        Part part3 = request.getPart("image3");

       

        Session s = HibernateUtil.getSessionFactory().openSession();

        if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "Please sign In");
        } else if (title.isEmpty()) {
            responseObject.addProperty("message", "Product Title can not be empty");
        } else if (!Util.isInteger(categoryId)) {
            responseObject.addProperty("message", "Invalid Category");
        } else if (Integer.parseInt(categoryId) == 0) {
            responseObject.addProperty("message", "Please select a Category");
        } else if (description.isEmpty()) {
            responseObject.addProperty("message", "Product description can not be empty");
        } else if (!Util.isInteger(subCategoryId)) {

            responseObject.addProperty("message", "Invalid Sub Category");

        } else if (Integer.parseInt(subCategoryId) == 0) {
            responseObject.addProperty("message", "Please select a Sub Category");
        } else if (!Util.isInteger(portionSizeId)) {

            responseObject.addProperty("message", "Invalid Portion Size");

        } else if (Integer.parseInt(portionSizeId) == 0) {
            responseObject.addProperty("message", "Please select a Portion Size");
        } else if (!Util.isInteger(spiceLevelId)) {

            responseObject.addProperty("message", "Invalid Spice Level");

        } else if (Integer.parseInt(spiceLevelId) == 0) {
            responseObject.addProperty("message", "Please select a Spice Level");
        } else if (!Util.isInteger(qualityId)) {

            responseObject.addProperty("message", "Invalid Quality");

        } else if (Integer.parseInt(qualityId) == 0) {
            responseObject.addProperty("message", "Please select a Quality");
        } else if (price.isEmpty()) {
            responseObject.addProperty("message", "Price can not be empty");
        } else if (!Util.isDouble(price)) {
            responseObject.addProperty("message", "Invalid Price");
        } else if (Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Price must be greater than 0");
        } else if (qty.isEmpty()) {
            responseObject.addProperty("message", "Quantity can not be empty");
        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Invalid Quantity");
        } else if (Integer.parseInt(qty) <= 0) {
            responseObject.addProperty("message", "Quantity must be greater than 0");
        } else if (part1.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image one is required");
        } else if (part2.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image two is required");
        } else if (part3.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image three is required");
        } else {

            Category category = (Category) s.get(Category.class, Integer.parseInt(categoryId));

            if (category == null) {
                responseObject.addProperty("message", "Please select a valid Category");
            } else {

                SubCategory subCategory = (SubCategory) s.get(SubCategory.class, Integer.parseInt(subCategoryId));

                if (subCategory == null) {
                    responseObject.addProperty("message", "Please select a valid Sub Category");
                } else {

                    PortionSize portionSize = (PortionSize) s.get(PortionSize.class, Integer.parseInt(portionSizeId));

                    if (portionSize == null) {
                        responseObject.addProperty("message", "Please select a valid Portion Size");
                    } else {

                        SpiceLevel spiceLevel = (SpiceLevel) s.get(SpiceLevel.class, Integer.parseInt(spiceLevelId));

                        if (spiceLevel == null) {
                            responseObject.addProperty("message", "Please select a valid Spice Level");
                        } else {

                            Quality quality = (Quality) s.get(Quality.class, Integer.parseInt(qualityId));
                            if (quality == null) {
                                responseObject.addProperty("message", "Please select a valid Quality");
                            } else {

                                FoodItem foodItem = new FoodItem();
                                foodItem.setCreated_at(new Date());
                                foodItem.setDescription(description);
                                foodItem.setPortionSize(portionSize);
                                foodItem.setPrice(Double.parseDouble(price));
                                foodItem.setQty(Integer.parseInt(qty));
                                foodItem.setQuality(quality);
                                foodItem.setSpiceLevel(spiceLevel);
                                foodItem.setSpiceLevel(spiceLevel);
                                foodItem.setSubCategory(subCategory);

                                Status status = (Status) s.get(Status.class, SaveProduct.PENDING_STATUS_ID);

                                foodItem.setStatus(status);

                                User user = (User) request.getSession().getAttribute("user");
                                Criteria c1 = s.createCriteria(User.class);
                                c1.add(Restrictions.eq("email", user.getEmail()));
                                User u1 = (User) c1.uniqueResult();

                                foodItem.setUser(u1);
                                foodItem.setTitle(title);

                                int id = (int) s.save(foodItem);
                                s.beginTransaction().commit();
                                s.close();

                                //image uploading
                                String appPath = getServletContext().getRealPath("");

                                String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images");
                                File productFolder = new File(newPath, String.valueOf(id));
                                productFolder.mkdir();

                                File file1 = new File(productFolder, "image1.png");
                                Files.copy(part1.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                
                                File file2 = new File(productFolder, "image2.png");
                                Files.copy(part2.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                File file3 = new File(productFolder, "image3.png");
                                Files.copy(part3.getInputStream(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                responseObject.addProperty("status", true);

                            }
                        }

                    }

                }

            }

        }
        
         }catch(Exception e){
             responseObject.addProperty("message", "An unexpected error occurred");
             
            
        }

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
