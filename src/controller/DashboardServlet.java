package controller;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import util.DBConnection;

public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                res.sendRedirect("jsp/login.jsp");
                return;
            }

            int userId = (int) session.getAttribute("userId");

            System.out.println("User ID: " + userId); // debug

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT ug.id, ug.group_name, ug.group_code " +
                    "FROM user_groups ug " +
                    "JOIN group_members gm ON ug.id = gm.group_id " +
                    "WHERE gm.user_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            List<String[]> groups = new ArrayList<>();

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("group_name");
                String code = rs.getString("group_code");

                groups.add(new String[] { id, name, code });
            }
            req.setAttribute("groups", groups);

            RequestDispatcher rd = req.getRequestDispatcher("jsp/dashboard.jsp");
            rd.forward(req, res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}