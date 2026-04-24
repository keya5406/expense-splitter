package controller;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import util.DBConnection;

public class AddExpenseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                res.sendRedirect("jsp/login.jsp");
                return;
            }

            int userId = (int) session.getAttribute("userId");

            Integer groupIdObj = (Integer) session.getAttribute("activeGroupId");
            if (groupIdObj == null) {
                res.getWriter().println("No active group selected");
                return;
            }

            int groupId = groupIdObj;

            double amount = Double.parseDouble(req.getParameter("amount"));
            String description = req.getParameter("description");

            Connection conn = DBConnection.getConnection();

            // =========================
            // 1. INSERT EXPENSE
            // =========================
            String sql = "INSERT INTO expenses(group_id, paid_by, amount, description) VALUES(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            ps.setDouble(3, amount);
            ps.setString(4, description);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            int expenseId = 0;
            if (rs.next()) {
                expenseId = rs.getInt(1);
            }

            // =========================
            // 2. GET MEMBERS (STORE IN LIST)
            // =========================
            String membersSql = "SELECT user_id FROM group_members WHERE group_id=?";
            PreparedStatement ps2 = conn.prepareStatement(membersSql);
            ps2.setInt(1, groupId);

            ResultSet rs2 = ps2.executeQuery();

            List<Integer> members = new ArrayList<>();

            while (rs2.next()) {
                members.add(rs2.getInt("user_id"));
            }

            int count = members.size();

            if (count == 0) {
                res.sendRedirect("group?groupId=" + groupId);
                return;
            }

            // =========================
            // 3. SPLIT LOGIC (ROUND SAFE)
            // =========================
            double splitAmount = Math.round((amount / count) * 100.0) / 100.0;

            // Fix remainder issue (important!)
            double totalAssigned = splitAmount * count;
            double remainder = Math.round((amount - totalAssigned) * 100.0) / 100.0;

            // =========================
            // 4. INSERT SPLITS
            // =========================
            for (int i = 0; i < members.size(); i++) {

                int memberId = members.get(i);
                double finalAmount = splitAmount;

                // Add remainder to first member to balance exact total
                if (i == 0) {
                    finalAmount += remainder;
                }

                String splitSql = "INSERT INTO expense_splits(expense_id, user_id, amount) VALUES(?,?,?)";
                PreparedStatement ps3 = conn.prepareStatement(splitSql);

                ps3.setInt(1, expenseId);
                ps3.setInt(2, memberId);
                ps3.setDouble(3, finalAmount);

                ps3.executeUpdate();
            }

            res.sendRedirect("group?groupId=" + groupId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}