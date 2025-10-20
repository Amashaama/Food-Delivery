/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.City;
import hibernate.DeliveryType;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.OrderItem;
import hibernate.OrderStatus;
import hibernate.Orders;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    private static final int SELECTOR_DEFAULT_VALUE = 0;
    private static final int ORDER_PENDING = 5;
    private static final int WITHIN_COLOMBO = 1;
    private static final int OUT_OF_COLOMBO = 2;
    private static final int RATING_DEFAULT_VALUE = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject reqJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        boolean isCurrentAddress = reqJsonObject.get("isCurrentAddress").getAsBoolean();
        String firstName = reqJsonObject.get("firstName").getAsString();
        String lastName = reqJsonObject.get("lastName").getAsString();
        String citySelect = reqJsonObject.get("citySelect").getAsString();
        String lineOne = reqJsonObject.get("lineOne").getAsString();
        String lineTwo = reqJsonObject.get("lineTwo").getAsString();
        String postalCode = reqJsonObject.get("postalCode").getAsString();
        String mobile = reqJsonObject.get("mobile").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "First Name can not be empty.");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Last Name can not be empty.");
        } else if (lineOne.isEmpty()) {
            responseObject.addProperty("message", "Line one can not be empty");
        } else if (lineTwo.isEmpty()) {
            responseObject.addProperty("message", "Line two can not be empty");
        } else if (postalCode.isEmpty()) {
            responseObject.addProperty("message", "Postal code can not be empty");
        } else if (!Util.isCodeValid(postalCode)) {
            responseObject.addProperty("message", "Invalid Postal Code.");
        } else if (citySelect.equals("0")) {
            responseObject.addProperty("message", "Please select a city");
        } else if (!Util.isInteger(citySelect)) {
            responseObject.addProperty("message", "Invalid city");
        } else if (mobile.isEmpty()) {
            responseObject.addProperty("message", "Mobile number can not be empty");
        } else if (!Util.isMobileValid(mobile)) {
            responseObject.addProperty("message", "Invalid mobile number");
        } else {

            Session s = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = s.beginTransaction();

            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                responseObject.addProperty("message", "Session expired! Please log in again");
            } else {

                if (isCurrentAddress) {
                    Criteria c1 = s.createCriteria(Address.class);
                    c1.add(Restrictions.eq("user", user));

                    c1.addOrder(Order.desc("id"));
                    if (c1.list().isEmpty()) {
                         
                        responseObject.addProperty("message", "Your current address is not found. Please add a new address ");
                    } else {
                        Address address = (Address) c1.list().get(0);

                        processCheckout(s, tr, user, address, responseObject);
                    }

                } else {
                    if (firstName.isEmpty()) {
                        responseObject.addProperty("message", "First name is required");
                    } else if (lastName.isEmpty()) {
                        responseObject.addProperty("message", "Last name is required");
                    } else if (!Util.isInteger(citySelect)) {
                        responseObject.addProperty("message", "Invalid City");
                    } else if (Integer.parseInt(citySelect) == Checkout.SELECTOR_DEFAULT_VALUE) {
                        responseObject.addProperty("message", "Invalid City");
                    } else {
                        City city = (City) s.get(City.class, Integer.valueOf(citySelect));

                        if (city == null) {
                            responseObject.addProperty("message", "Invalid City name");
                        } else {

                            if (lineOne.isEmpty()) {
                                responseObject.addProperty("message", "Line one is required");
                            } else if (lineTwo.isEmpty()) {
                                responseObject.addProperty("message", "Line two is required");
                            } else if (postalCode.isEmpty()) {

                                responseObject.addProperty("message", "Postalcode is required");
                            } else if (!Util.isCodeValid(postalCode)) {
                                responseObject.addProperty("message", "Invalid Postalcode");
                            } else if (mobile.isEmpty()) {
                                responseObject.addProperty("message", "Mobile number is required");
                            } else if (!Util.isMobileValid(mobile)) {
                                responseObject.addProperty("message", "Invalid Mobile number");
                            } else {

                                Address address = new Address();
                                address.setFirstName(firstName);
                                address.setLastName(lastName);
                                address.setLineOne(lineOne);
                                address.setLineTwo(lineTwo);
                                address.setCity(city);
                                address.setPostalCode(postalCode);
                                address.setMobile(mobile);
                                address.setUser(user);

                                s.save(address);

                                processCheckout(s, tr, user, address, responseObject);
                                
                                
                            }
                        }

                    }

                }
            }

        }

        response.setContentType("application/json");

        response.getWriter().write(gson.toJson(responseObject));

    }

    private void processCheckout(Session s,
            Transaction tr,
            User user,
            Address address,
            JsonObject responseObject) {

        try {
            double amount = 0;
            DeliveryType withinColombo = (DeliveryType) s.get(DeliveryType.class, Checkout.WITHIN_COLOMBO);
            DeliveryType outOfColombo = (DeliveryType) s.get(DeliveryType.class, Checkout.OUT_OF_COLOMBO);

            Orders orders = new Orders();
            orders.setAddress(address);
            orders.setCreated_at(new Date());
            orders.setUser(user);

            if (address.getCity().getName().equalsIgnoreCase("Colombo")) {
                amount += withinColombo.getPrice();
                orders.setDeliveryType(withinColombo);
            } else {
                amount += outOfColombo.getPrice();
                orders.setDeliveryType(outOfColombo);
            }

            int orderId = (int) s.save(orders);
            
            Criteria availableFoodItems2 = s.createCriteria(FoodItem.class);
            availableFoodItems2.add(Restrictions.gt("qty",0));
            List<FoodItem> availableList2 = availableFoodItems2.list();

            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            c1.add(Restrictions.in("foodItem", availableList2));
            List<Cart> cartList = c1.list();

            OrderStatus orderStatus = (OrderStatus) s.get(OrderStatus.class, Checkout.ORDER_PENDING);

            String items = "";

            for (Cart cart : cartList) {

                amount += cart.getQty() * cart.getFoodItem().getPrice();

                OrderItem orderItem = new OrderItem();

                items += cart.getFoodItem().getTitle() + " x " + cart.getQty() + ", ";

                FoodItem foodItem = cart.getFoodItem();
                orderItem.setFoodItem(foodItem);
                orderItem.setOrderStatus(orderStatus);
                orderItem.setOrders(orders);
                orderItem.setQty(cart.getQty());
                orderItem.setRating(Checkout.RATING_DEFAULT_VALUE);

                s.save(orderItem);

                foodItem.setQty(foodItem.getQty() - cart.getQty());
                s.update(foodItem);

                s.delete(cart);
            }

            tr.commit();
            //payhere process
            String merchantID = "1224036";
            String merchantSecret = "MjMyNDQ3OTMxNDY5OTk0NzY3NDIxMjE2MjAyODUyMzQ2OTU1OQ==";

            String orderID = "#000" + orderId;

            String currency = "LKR";

            String formattedAmount = new DecimalFormat("0.00").format(amount);
            // String formattedAmount = String.format("%.2f", amount);

            String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);
            String hash = PayHere.generateMD5(merchantID + orderID + formattedAmount + currency + merchantSecretMD5);

            JsonObject payHereJson = new JsonObject();

            payHereJson.addProperty("sandbox", true);
            payHereJson.addProperty("merchant_id", merchantID);

            payHereJson.addProperty("return_url", "");
            payHereJson.addProperty("cancel_url", "");
            //  payHereJson.addProperty("notify_url", "https://21f6516f879a.ngrok-free.app/Smarttrade/VerifyPayments");

            payHereJson.addProperty("notify_url", "http://localhost:8080/FoodDelivery/VerifyPayments");

            payHereJson.addProperty("order_id", orderID);
            payHereJson.addProperty("items", items);
            payHereJson.addProperty("amount", formattedAmount);
            payHereJson.addProperty("currency", currency);
            payHereJson.addProperty("hash", hash);

            payHereJson.addProperty("first_name", user.getFirst_name());
            payHereJson.addProperty("last_name", user.getLast_name());
            payHereJson.addProperty("email", user.getEmail());

            payHereJson.addProperty("phone", address.getUser().getMobile());
            payHereJson.addProperty("address", address.getLineOne() + ", " + address.getLineTwo());
            payHereJson.addProperty("city", address.getCity().getName());
            payHereJson.addProperty("country", "Sri Lanka");

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Checkout Complete");
            responseObject.add("payhereJson", new Gson().toJsonTree(payHereJson));

        } catch (Exception e) {
            tr.rollback();
        }

    }

}
