package main.java.controller;

import java.io.IOException;
import java.sql.*;
import java.util.Random;

import javax.servlet.*;
import javax.servlet.http.*;

import main.java.util.DBConnection;

public class CreateGroupServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String groupName = req.getParameter("groupName");

        if (groupName == null || groupName.trim().isEmpty()) {
            res.sendError(400, "Group name is required");
            return;
        }

        // generate random group code
        String groupCode = generateCode();

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect("jsp/login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                res.sendError(500, "Database connection failed");
                return;
            }

            int groupId = 0;

            // =========================
            // 1. INSERT GROUP
            // =========================
            String sql = "INSERT INTO user_groups(group_name, group_code, created_by) VALUES(?,?,?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, groupName);
                ps.setString(2, groupCode);
                ps.setInt(3, userId);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        groupId = rs.getInt(1);
                    }
                }
            }

            if (groupId == 0) {
                res.sendError(500, "Failed to create group");
                return;
            }

            // =========================
            // 2. ADD CREATOR TO GROUP
            // =========================
            String insertMember = "INSERT INTO group_members(group_id, user_id) VALUES(?,?)";

            try (PreparedStatement psMem = conn.prepareStatement(insertMember)) {
                psMem.setInt(1, groupId);
                psMem.setInt(2, userId);

                psMem.executeUpdate();
            }

            res.sendRedirect("dashboard");

        } catch (SQLException e) {
            e.printStackTrace();
            res.sendError(500, "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Unexpected error: " + e.getMessage());
        }
    }

    private String generateCode() {
        Random r = new Random();
        return "GRP" + (10000 + r.nextInt(90000));
    }
}