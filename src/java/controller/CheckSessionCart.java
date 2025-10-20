/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import hibernate.Cart;
import hibernate.FoodItem;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anne
 */
@WebServlet(name = "CheckSessionCart", urlPatterns = {"/CheckSessionCart"})
public class CheckSessionCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Session s = null;
        Transaction tr = null;

        try {

            User user = (User) request.getSession().getAttribute("user");
            if (user != null) {
                ArrayList<Cart> sessionCarts = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");
                if (sessionCarts != null) {
                    s = HibernateUtil.getSessionFactory().openSession();
                    tr = s.beginTransaction();

                    Criteria c1 = s.createCriteria(Cart.class);
                    c1.add(Restrictions.eq("user", user));

                    for (Cart sessionCart : sessionCarts) {
                        FoodItem foodItem = (FoodItem) s.get(FoodItem.class, sessionCart.getFoodItem().getId());
                        c1.add(Restrictions.eq("foodItem", sessionCart.getFoodItem()));

                        if (c1.list().isEmpty()) {
                            sessionCart.setUser(user);
                            s.save(sessionCart);

                        } else {
                            Cart dbCart = (Cart) c1.uniqueResult();
                            int newQty = sessionCart.getQty() + dbCart.getQty();

                            if (newQty <= foodItem.getQty()) {
                                dbCart.setQty(newQty);
                                dbCart.setUser(user);
                                s.update(dbCart);

                            }

                        }

                    }

                    tr.commit();

                    request.getSession().setAttribute("sessionCart", null);

                }

            }

        } catch (Exception e) {

            tr.rollback();

        }

    }

}
