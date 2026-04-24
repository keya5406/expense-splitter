package controller;

import java.io.IOException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import util.DBConnection;

public class SettleOneServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("activeGroupId") == null) {
                res.sendRedirect("dashboard");
                return;
            }

            int groupId = (int) session.getAttribute("activeGroupId");

            int fromId = Integer.parseInt(req.getParameter("fromId"));
            int toId = Integer.parseInt(req.getParameter("toId"));
            double amount = Double.parseDouble(req.getParameter("amount"));

            amount = Math.round(amount * 100.0) / 100.0;

            if (fromId == toId || amount <= 0) {
                res.sendRedirect("group?groupId=" + groupId);
                return;
            }

            Connection conn = DBConnection.getConnection();

           
            String balanceSql = "SELECT u.id, " +
                    "IFNULL((SELECT SUM(e.amount) FROM expenses e " +
                    "WHERE e.paid_by = u.id AND e.group_id = ?), 0) " +
                    "- IFNULL((SELECT SUM(es.amount) FROM expense_splits es " +
                    "JOIN expenses e ON es.expense_id = e.id " +
                    "WHERE es.user_id = u.id AND e.group_id = ?), 0) " +
                    "+ IFNULL((SELECT SUM(s.amount) FROM settlements s " +
                    "WHERE s.from_user = u.id AND s.group_id = ?), 0) " + // ✅ flipped
                    "- IFNULL((SELECT SUM(s.amount) FROM settlements s " +
                    "WHERE s.to_user = u.id AND s.group_id = ?), 0) " + // ✅ flipped
                    "AS balance " +
                    "FROM users u " +
                    "JOIN group_members gm ON u.id = gm.user_id " +
                    "WHERE gm.group_id = ?";
            // Note: no AND e.is_settled = 0 — keep consistent with GroupDetailsServlet

            PreparedStatement ps = conn.prepareStatement(balanceSql);

            for (int i = 1; i <= 5; i++)
                ps.setInt(i, groupId);

            ResultSet rs = ps.executeQuery();

            double fromBalance = 0;
            double toBalance = 0;

            while (rs.next()) {
                int uid = rs.getInt("id");
                double bal = rs.getDouble("balance");

                if (uid == fromId)
                    fromBalance = bal;
                if (uid == toId)
                    toBalance = bal;
            }

            // =========================
            // ❌ VALIDATIONS
            // =========================

            // payer must owe
            if (fromBalance >= 0) {
                res.sendRedirect("group?groupId=" + groupId);
                return;
            }

            // receiver must be creditor
            if (toBalance <= 0) {
                res.sendRedirect("group?groupId=" + groupId);
                return;
            }

            // =========================
            // ✅ LIMIT PAYMENT
            // =========================
            double maxFrom = Math.abs(fromBalance);
            double maxTo = toBalance;

            double maxAllowed = Math.min(maxFrom, maxTo);

            if (amount > maxAllowed) {
                amount = maxAllowed;
            }

            // =========================
            // ✅ CHECK ALREADY PAID BETWEEN PAIR
            // =========================
            String pairSql = "SELECT COALESCE(SUM(amount),0) FROM settlements " +
                    "WHERE group_id=? AND from_user=? AND to_user=?";
            PreparedStatement ps2 = conn.prepareStatement(pairSql);

            ps2.setInt(1, groupId);
            ps2.setInt(2, fromId);
            ps2.setInt(3, toId);

            ResultSet rs2 = ps2.executeQuery();

            double alreadyPaid = 0;
            if (rs2.next()) {
                alreadyPaid = rs2.getDouble(1);
            }

            double remaining = maxAllowed - alreadyPaid;

            if (remaining <= 0.01) {
                res.sendRedirect("group?groupId=" + groupId);
                return;
            }

            if (amount > remaining) {
                amount = remaining;
            }

            // =========================
            // ✅ INSERT
            // =========================
            String insert = "INSERT INTO settlements (group_id, from_user, to_user, amount) VALUES (?, ?, ?, ?)";
            PreparedStatement ps3 = conn.prepareStatement(insert);

            ps3.setInt(1, groupId);
            ps3.setInt(2, fromId);
            ps3.setInt(3, toId);
            ps3.setDouble(4, amount);

            ps3.executeUpdate();

            res.sendRedirect("group?groupId=" + groupId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}