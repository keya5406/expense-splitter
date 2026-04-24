package controller;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import util.DBConnection;

public class GroupDetailsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                res.sendRedirect("jsp/login.jsp");
                return;
            }

            int groupId = Integer.parseInt(req.getParameter("groupId"));
            session.setAttribute("activeGroupId", groupId);

            Connection conn = DBConnection.getConnection();

            // =========================
            // 1. GROUP INFO
            // =========================
            String groupName = "";
            String groupCode = "";

            String groupSql = "SELECT group_name, group_code FROM user_groups WHERE id=?";
            PreparedStatement ps1 = conn.prepareStatement(groupSql);
            ps1.setInt(1, groupId);
            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                groupName = rs1.getString("group_name");
                groupCode = rs1.getString("group_code");
            }

            // =========================
            // 2. MEMBERS
            // =========================
            List<String> members = new ArrayList<>();

            String memberSql = "SELECT u.name FROM users u " +
                    "JOIN group_members gm ON u.id = gm.user_id " +
                    "WHERE gm.group_id=?";

            PreparedStatement ps2 = conn.prepareStatement(memberSql);
            ps2.setInt(1, groupId);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
                members.add(rs2.getString("name"));
            }

            // =========================
            // 3. ACTIVE EXPENSES
            // =========================
            List<String[]> activeExpenses = new ArrayList<>();

            String activeExpenseSql = "SELECT e.amount, e.description, u.name AS paid_by " +
                    "FROM expenses e " +
                    "JOIN users u ON e.paid_by = u.id " +
                    "WHERE e.group_id=? AND e.is_settled = 0 " +
                    "ORDER BY e.id DESC";

            PreparedStatement ps3 = conn.prepareStatement(activeExpenseSql);
            ps3.setInt(1, groupId);
            ResultSet rs3 = ps3.executeQuery();

            while (rs3.next()) {
                activeExpenses.add(new String[] {
                        rs3.getString("paid_by"),
                        rs3.getString("amount"),
                        rs3.getString("description")
                });
            }

            // =========================
            // 4. SETTLED EXPENSES
            // =========================
            List<String[]> settledExpenses = new ArrayList<>();

            String settledExpenseSql = "SELECT e.amount, e.description, u.name AS paid_by " +
                    "FROM expenses e " +
                    "JOIN users u ON e.paid_by = u.id " +
                    "WHERE e.group_id=? AND e.is_settled = 1 " +
                    "ORDER BY e.id DESC";

            PreparedStatement ps4 = conn.prepareStatement(settledExpenseSql);
            ps4.setInt(1, groupId);
            ResultSet rs4 = ps4.executeQuery();

            while (rs4.next()) {
                settledExpenses.add(new String[] {
                        rs4.getString("paid_by"),
                        rs4.getString("amount"),
                        rs4.getString("description")
                });
            }

            // =========================
            // 5. BALANCES
            // =========================
            List<Object[]> balances = new ArrayList<>();

            String balanceSql = "SELECT u.id, u.name, " +
                    "IFNULL((SELECT SUM(e.amount) FROM expenses e " +
                    "WHERE e.paid_by = u.id AND e.group_id = ?), 0) " +
                    "- IFNULL((SELECT SUM(es.amount) FROM expense_splits es " +
                    "JOIN expenses e ON es.expense_id = e.id " +
                    "WHERE es.user_id = u.id AND e.group_id = ?), 0) " +
                    "+ IFNULL((SELECT SUM(s.amount) FROM settlements s " +
                    "WHERE s.from_user = u.id AND s.group_id = ?), 0) " + // ✅ plus
                    "- IFNULL((SELECT SUM(s.amount) FROM settlements s " +
                    "WHERE s.to_user = u.id AND s.group_id = ?), 0) " + // ✅ minus
                    "AS balance " +
                    "FROM users u " +
                    "JOIN group_members gm ON u.id = gm.user_id " +
                    "WHERE gm.group_id = ?";

            PreparedStatement ps5 = conn.prepareStatement(balanceSql);
            for (int i = 1; i <= 5; i++)
                ps5.setInt(i, groupId);

            ResultSet rs5 = ps5.executeQuery();

            while (rs5.next()) {
                double bal = rs5.getDouble("balance");
                bal = Math.round(bal * 100.0) / 100.0;

                balances.add(new Object[] {
                        rs5.getInt("id"),
                        rs5.getString("name"),
                        bal
                });
            }

            // =========================
            // 6. SETTLEMENT PLAN
            // =========================
            List<Object[]> debtors = new ArrayList<>();
            List<Object[]> creditors = new ArrayList<>();

            for (Object[] b : balances) {
                int id = (int) b[0];
                String name = (String) b[1];
                double bal = (double) b[2];

                if (bal < 0)
                    debtors.add(new Object[] { id, name, -bal });
                else if (bal > 0)
                    creditors.add(new Object[] { id, name, bal });
            }

            List<Object[]> settlements = new ArrayList<>();

            int i = 0, j = 0;

            while (i < debtors.size() && j < creditors.size()) {

                double debt = (double) debtors.get(i)[2];
                double credit = (double) creditors.get(j)[2];

                double min = Math.min(debt, credit);
                double amt = Math.round(min * 100.0) / 100.0;

                settlements.add(new Object[] {
                        debtors.get(i)[1],
                        creditors.get(j)[1],
                        amt,
                        debtors.get(i)[0],
                        creditors.get(j)[0]
                });

                debtors.get(i)[2] = debt - amt;
                creditors.get(j)[2] = credit - amt;

                if ((double) debtors.get(i)[2] <= 0.01)
                    i++;
                if ((double) creditors.get(j)[2] <= 0.01)
                    j++;
            }

            // SEND
            req.setAttribute("groupName", groupName);
            req.setAttribute("groupCode", groupCode);
            req.setAttribute("members", members);
            req.setAttribute("activeExpenses", activeExpenses);
            req.setAttribute("settledExpenses", settledExpenses);
            req.setAttribute("balances", balances);
            req.setAttribute("settlements", settlements);
            req.setAttribute("groupId", groupId);

            req.getRequestDispatcher("jsp/group-details.jsp").forward(req, res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}