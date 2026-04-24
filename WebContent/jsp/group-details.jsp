<%@ page session="true" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Group Details</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-4">

    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3>${groupName}</h3>
            <small class="text-muted">Code: ${groupCode}</small>
        </div>

        <div>
            <a href="<%= request.getContextPath() %>/jsp/add-expense.jsp"
               class="btn btn-primary btn-sm">
                Add Expense
            </a>

            <form action="<%= request.getContextPath() %>/settleUp"
                  method="post"
                  style="display:inline;">
                <button type="submit" class="btn btn-warning btn-sm">
                    Settle Up
                </button>
            </form>
        </div>
    </div>

    <div class="row">

        <!-- LEFT SIDE -->
        <div class="col-md-8">

            <!-- Active Expenses -->
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    Active Expenses
                </div>
                <div class="card-body">

                    <%
                        List<String[]> activeExpenses =
                                (List<String[]>) request.getAttribute("activeExpenses");

                        if (activeExpenses != null && !activeExpenses.isEmpty()) {
                            for (String[] e : activeExpenses) {
                                double amount = Double.parseDouble(e[1]);
                    %>
                    <p>
                        <b><%= e[0] %></b> paid ₹<%= String.format("%.2f", amount) %>
                        (<%= e[2] %>)
                    </p>
                    <%
                            }
                        } else {
                    %>
                    <p class="text-muted">No active expenses</p>
                    <% } %>

                </div>
            </div>

            <!-- Settled History -->
            <div class="card">
                <div class="card-header bg-secondary text-white">
                    Settled History
                </div>
                <div class="card-body">

                    <%
                        List<String[]> settledExpenses =
                                (List<String[]>) request.getAttribute("settledExpenses");

                        if (settledExpenses != null && !settledExpenses.isEmpty()) {
                            for (String[] e : settledExpenses) {
                                double amount = Double.parseDouble(e[1]);
                    %>
                    <p class="text-muted">
                        <b><%= e[0] %></b> paid ₹<%= String.format("%.2f", amount) %>
                        (<%= e[2] %>) [Settled]
                    </p>
                    <%
                            }
                        } else {
                    %>
                    <p class="text-muted">No settled expenses</p>
                    <% } %>

                </div>
            </div>

        </div>

        <!-- RIGHT SIDE -->
        <div class="col-md-4">

            <!-- Members -->
            <div class="card mb-4">
                <div class="card-header">
                    Members
                </div>
                <div class="card-body">

                    <%
                        List<String> members = (List<String>) request.getAttribute("members");

                        if (members != null && !members.isEmpty()) {
                            for (String m : members) {
                    %>
                    <p><%= m %></p>
                    <%
                            }
                        }
                    %>

                </div>
            </div>

            <!-- Balances -->
            <div class="card mb-4">
                <div class="card-header">
                    Balances
                </div>
                <div class="card-body">

                    <%
                        List<Object[]> balances =
                                (List<Object[]>) request.getAttribute("balances");

                        boolean allZero = true;

                        if (balances != null && !balances.isEmpty()) {
                            for (Object[] b : balances) {

                                String name = (String) b[1];
                                double bal = (double) b[2];

                                if (Math.abs(bal) > 0.01) {
                                    allZero = false;
                                }
                    %>

                    <div class="d-flex justify-content-between border-bottom py-1">
                        <span><%= name %></span>

                        <span>
                            <% if (bal > 0) { %>
                                <span class="text-success">
                                    +₹<%= String.format("%.2f", bal) %>
                                </span>
                            <% } else if (bal < 0) { %>
                                <span class="text-danger">
                                    -₹<%= String.format("%.2f", -bal) %>
                                </span>
                            <% } else { %>
                                <span class="text-muted">0</span>
                            <% } %>
                        </span>
                    </div>

                    <%
                            }
                        }
                    %>

                </div>
            </div>

            <!-- Settlement Plan -->
            <div class="card">
                <div class="card-header">
                    Settlement Plan
                </div>
                <div class="card-body">

                    <%
                        List<Object[]> settlements =
                                (List<Object[]>) request.getAttribute("settlements");

                        if (allZero) {
                    %>
                    <p class="text-success">All settled up</p>
                    <%
                        } else if (settlements != null && !settlements.isEmpty()) {
                            for (Object[] s : settlements) {

                                String fromName = (String) s[0];
                                String toName = (String) s[1];
                                double amount = (double) s[2];
                                int fromId = (int) s[3];
                                int toId = (int) s[4];
                    %>

                    <div class="d-flex justify-content-between align-items-center border-bottom py-2">

                        <span>
                            <b><%= fromName %></b> pays 
                            <b><%= toName %></b> 
                            ₹<%= String.format("%.2f", amount) %>
                        </span>

                        <form action="<%= request.getContextPath() %>/settleOne"
                              method="post"
                              style="margin:0;"
                              onsubmit="return handleSettle(this);">

                            <input type="hidden" name="fromId" value="<%= fromId %>">
                            <input type="hidden" name="toId" value="<%= toId %>">
                            <input type="hidden" name="amount" value="<%= amount %>">

                            <button type="submit" class="btn btn-sm btn-success">
                                Settle
                            </button>
                        </form>

                    </div>

                    <%
                            }
                        }
                    %>

                </div>
            </div>

        </div>

    </div>

    <!-- Back -->
    <div class="mt-4">
        <a href="<%= request.getContextPath() %>/dashboard"
           class="btn btn-outline-secondary btn-sm">
            Back to Dashboard
        </a>
    </div>

</div>

<!-- ✅ Prevent double click -->
<script>
function handleSettle(form) {
    const btn = form.querySelector("button");
    btn.disabled = true;
    btn.innerText = "Settling...";
    return true;
}
</script>

</body>
</html>