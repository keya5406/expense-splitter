<%@ page session="true" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard - Group Expense Splitter</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<div class="container mt-4">

    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3>Welcome ${sessionScope.userName}</h3>
        <a href="logout" class="btn btn-outline-danger btn-sm">Logout</a>
    </div>

    <!-- Action Buttons -->
    <div class="mb-4">
        <a href="<%= request.getContextPath() %>/jsp/create-group.jsp" class="btn btn-success me-2">
            Create Group
        </a>

        <a href="<%= request.getContextPath() %>/jsp/join-group.jsp" class="btn btn-primary">
            Join Group
        </a>
    </div>

    <!-- Groups Section -->
    <h5 class="mb-3">Your Groups</h5>

    <div class="row">

<%
    List<String[]> groups = (List<String[]>) request.getAttribute("groups");

    if (groups != null && !groups.isEmpty()) {
        for (String[] g : groups) {
%>

        <div class="col-md-4">
            <div class="card shadow-sm mb-4">
                <div class="card-body">

                    <h5 class="card-title"><%= g[1] %></h5>
                    <p class="text-muted">Code: <%= g[2] %></p>

                    <a href="<%= request.getContextPath() %>/group?groupId=<%= g[0] %>"
                       class="btn btn-primary btn-sm">
                        Open Group
                    </a>

                </div>
            </div>
        </div>

<%
        }
    } else {
%>

        <div class="col-12">
            <div class="alert alert-secondary">
                No groups yet. Create or join a group to get started.
            </div>
        </div>

<%
    }
%>

    </div>

</div>

</body>
</html>