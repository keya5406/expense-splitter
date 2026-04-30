package main.java.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

import main.java.util.DBConnection;

public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (name == null || email == null || password == null ||
                name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            res.sendError(400, "All fields are required");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                res.sendError(500, "Database connection failed");
                return;
            }

            String sql = "INSERT INTO users(name, email, password) VALUES(?,?,?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);

                ps.executeUpdate();
            }

            res.sendRedirect("jsp/login.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            res.sendError(500, "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Unexpected error: " + e.getMessage());
        }
    }
}