package controller;

import java.io.IOException;
import java.sql.*;
import java.util.Random;

import javax.servlet.*;
import javax.servlet.http.*;

import util.DBConnection;

public class CreateGroupServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String groupName = req.getParameter("groupName");

        // generate random group code
        String groupCode = generateCode();

        try {
            Connection conn = DBConnection.getConnection();

            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                res.sendRedirect("jsp/login.jsp");
                return;
            }

            int userId = (int) session.getAttribute("userId");

            // Insert group
            String sql = "INSERT INTO user_groups(group_name, group_code, created_by) VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, groupName);
            ps.setString(2, groupCode);
            ps.setInt(3, userId);

            ps.executeUpdate();

            // Get group ID
            String getIdSql = "SELECT id FROM user_groups WHERE group_code=?";
            PreparedStatement psGet = conn.prepareStatement(getIdSql);
            psGet.setString(1, groupCode);

            ResultSet rs = psGet.executeQuery();

            if (rs.next()) {
                int groupId = rs.getInt("id");

                // Add creator to group_members
                String insertMember = "INSERT INTO group_members(group_id, user_id) VALUES(?,?)";
                PreparedStatement psMem = conn.prepareStatement(insertMember);
                psMem.setInt(1, groupId);
                psMem.setInt(2, userId);

                psMem.executeUpdate();
            }

            
            res.sendRedirect("dashboard");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateCode() {
        Random r = new Random();
        return "GRP" + (10000 + r.nextInt(90000));
    }
}