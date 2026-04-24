<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Join Group - Group Expense Splitter</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<div class="container d-flex justify-content-center align-items-center" style="height: 100vh;">

    <div class="card shadow p-4" style="width: 400px;">
        
        <h3 class="text-center mb-4">Join Group</h3>

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

        <form action="../joinGroup" method="post">

            <div class="mb-3">
                <label class="form-label">Group Code</label>
                <input type="text" name="groupCode" class="form-control" required>
            </div>

            <button type="submit" class="btn btn-primary w-100">Join Group</button>

        </form>

        <div class="text-center mt-3">
            <a href="../dashboard">Back to Dashboard</a>
        </div>

    </div>

</div>

</body>
</html>