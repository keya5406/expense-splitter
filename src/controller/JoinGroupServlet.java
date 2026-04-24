package controller;

import java.io.IOException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import util.DBConnection;

public class JoinGroupServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String groupCode = req.getParameter("groupCode");

        try {
            Connection conn = DBConnection.getConnection();

            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                res.sendRedirect("jsp/login.jsp");
                return;
            }

            int userId = (int) session.getAttribute("userId");

            // Find group
            String findSql = "SELECT id FROM user_groups WHERE group_code=?";
            PreparedStatement ps1 = conn.prepareStatement(findSql);
            ps1.setString(1, groupCode);

            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {

                int groupId = rs.getInt("id");

                // Check duplicate
                String checkSql = "SELECT * FROM group_members WHERE group_id=? AND user_id=?";
                PreparedStatement checkPs = conn.prepareStatement(checkSql);
                checkPs.setInt(1, groupId);
                checkPs.setInt(2, userId);

                ResultSet checkRs = checkPs.executeQuery();

                if (checkRs.next()) {
                    res.sendRedirect("dashboard");
                    return;
                }

                // Insert
                String insertSql = "INSERT INTO group_members(group_id, user_id) VALUES(?,?)";
                PreparedStatement ps2 = conn.prepareStatement(insertSql);
                ps2.setInt(1, groupId);
                ps2.setInt(2, userId);

                ps2.executeUpdate();

                res.sendRedirect("dashboard");

            } else {
                res.getWriter().println("Invalid Group Code");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}