package main.java.controller;

import java.io.IOException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import main.java.util.DBConnection;

public class JoinGroupServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String groupCode = req.getParameter("groupCode");

        if (groupCode == null || groupCode.trim().isEmpty()) {
            res.sendError(400, "Group code is required");
            return;
        }

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

            // =========================
            // 1. FIND GROUP
            // =========================
            String findSql = "SELECT id FROM user_groups WHERE group_code=?";

            try (PreparedStatement ps1 = conn.prepareStatement(findSql)) {
                ps1.setString(1, groupCode);

                try (ResultSet rs = ps1.executeQuery()) {

                    if (rs.next()) {

                        int groupId = rs.getInt("id");

                        // =========================
                        // 2. CHECK DUPLICATE
                        // =========================
                        String checkSql = "SELECT * FROM group_members WHERE group_id=? AND user_id=?";

                        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                            checkPs.setInt(1, groupId);
                            checkPs.setInt(2, userId);

                            try (ResultSet checkRs = checkPs.executeQuery()) {

                                if (checkRs.next()) {
                                    res.sendRedirect("dashboard");
                                    return;
                                }
                            }
                        }

                        // =========================
                        // 3. INSERT MEMBER
                        // =========================
                        String insertSql = "INSERT INTO group_members(group_id, user_id) VALUES(?,?)";

                        try (PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                            ps2.setInt(1, groupId);
                            ps2.setInt(2, userId);

                            ps2.executeUpdate();
                        }

                        res.sendRedirect("dashboard");

                    } else {
                        res.getWriter().println("Invalid Group Code");
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