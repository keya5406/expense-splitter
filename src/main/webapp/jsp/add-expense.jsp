<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Expense - Group Expense Splitter</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<div class="container d-flex justify-content-center align-items-center" style="height: 100vh;">

    <div class="card shadow p-4" style="width: 450px;">
        
        <h3 class="text-center mb-4">Add Expense</h3>

        <!-- Error Message -->
        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
            <div class="alert alert-danger"><%= error %></div>
        <%
            }
        %>

        <!-- Success Message -->
        <%
            String success = (String) request.getAttribute("success");
            if (success != null) {
        %>
            <div class="alert alert-success"><%= success %></div>
        <%
            }
        %>

        <form action="../addExpense" method="post">

            <!-- Amount -->
            <div class="mb-3">
                <label class="form-label">Amount</label>
                <input type="number" step="0.01" name="amount" class="form-control" required>
            </div>

            <!-- Description -->
            <div class="mb-3">
                <label class="form-label">Description</label>
                <input type="text" name="description" class="form-control" placeholder="e.g. Dinner, Taxi">
            </div>

            <button type="submit" class="btn btn-primary w-100">Add Expense</button>

        </form>

        <div class="text-center mt-3">
            <a href="../groupDetails?groupId=<%= session.getAttribute("activeGroupId") %>">
                Back to Group
            </a>
        </div>

    </div>

</div>

</body>
</html>