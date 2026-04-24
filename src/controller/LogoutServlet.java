package controller;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public class LogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session != null) {
            session.invalidate(); // clears userId, userName, everything
        }

        res.sendRedirect("jsp/login.jsp");
    }
}