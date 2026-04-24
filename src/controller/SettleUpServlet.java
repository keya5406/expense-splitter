package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.*;
import javax.servlet.http.*;

import util.DBConnection;

public class SettleUpServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            int groupId = (int) session.getAttribute("activeGroupId");

            Connection conn = DBConnection.getConnection();

            String sql = "UPDATE expenses SET is_settled = 1 WHERE group_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, groupId);

            ps.executeUpdate();

            res.sendRedirect("group?groupId=" + groupId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}