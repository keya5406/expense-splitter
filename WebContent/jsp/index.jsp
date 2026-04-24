<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page session="true" %>

<%
    Integer userId = (Integer) session.getAttribute("userId");

    if (userId != null) {
        response.sendRedirect("dashboard");
    } else {
        response.sendRedirect("jsp/login.jsp");
    }
%>