package main.java.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

import main.java.util.DBConnection;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            res.sendError(400, "Email and password are required");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                res.sendError(500, "Database connection failed");
                return;
            }

            String sql = "SELECT * FROM users WHERE email=? AND password=?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, email);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        // LOGIN SUCCESS
                        HttpSession session = req.getSession();
                        session.setAttribute("userId", rs.getInt("id"));
                        session.setAttribute("userName", rs.getString("name"));

                        res.sendRedirect("dashboard");
                    } else {
                        // LOGIN FAIL
                        res.getWriter().println("Invalid Email or Password");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            res.sendError(500, "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Unexpected error: " + e.getMessage());
        }
    }
}