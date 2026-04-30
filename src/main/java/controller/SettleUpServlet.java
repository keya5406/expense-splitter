package main.java.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.http.*;

import main.java.util.DBConnection;

public class SettleUpServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("activeGroupId") == null) {
                res.sendRedirect("dashboard");
                return;
            }

            int groupId = (int) session.getAttribute("activeGroupId");

            try (Connection conn = DBConnection.getConnection()) {

                if (conn == null) {
                    res.sendError(500, "Database connection failed");
                    return;
                }

                String sql = "UPDATE expenses SET is_settled = 1 WHERE group_id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, groupId);
                    ps.executeUpdate();
                }
            }

            res.sendRedirect("group?groupId=" + groupId);

        } catch (SQLException e) {
            e.printStackTrace();
            res.sendError(500, "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Unexpected error: " + e.getMessage());
        }
    }
}