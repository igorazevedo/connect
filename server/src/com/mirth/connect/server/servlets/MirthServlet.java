/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * http://www.mirthcorp.com
 *
 * The software in this package is published under the terms of the MPL
 * license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.mirth.connect.server.servlets;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.mirth.connect.server.controllers.AuthorizationController;
import com.mirth.connect.server.controllers.ControllerException;
import com.mirth.connect.server.controllers.ControllerFactory;

public abstract class MirthServlet extends HttpServlet {
    private AuthorizationController authorizationController = ControllerFactory.getFactory().createAuthorizationController();

    public boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (session.getAttribute(UserServlet.SESSION_AUTHORIZED) != null) && (session.getAttribute(UserServlet.SESSION_AUTHORIZED).equals(true));
    }

    public boolean isUserAuthorized(HttpServletRequest request, Map<String, Object> parameterMap) throws ServletException {
        HttpSession session = request.getSession();
        
        try {
            return authorizationController.isUserAuthorized((Integer) session.getAttribute(UserServlet.SESSION_USER), request.getParameter("op"), parameterMap, getRequestIpAddress(request));
        } catch (ControllerException e) {
            throw new ServletException(e);
        }
    }

    public boolean isUserAuthorizedForExtension(HttpServletRequest request, String extensionName, String operation, Map<String, Object> parameterMap) throws ServletException {
        HttpSession session = request.getSession();

        try {
            return authorizationController.isUserAuthorizedForExtension((Integer) session.getAttribute(UserServlet.SESSION_USER), extensionName, operation, parameterMap, getRequestIpAddress(request));
        } catch (ControllerException e) {
            throw new ServletException(e);
        }

    }

    protected int getCurrentUserId(HttpServletRequest request) {
        return Integer.parseInt(request.getSession().getAttribute(UserServlet.SESSION_USER).toString());
    }

    private String getRequestIpAddress(HttpServletRequest request) {
        String address = request.getHeader("x-forwarded-for");

        if (address == null) {
            address = request.getRemoteAddr();
        }

        return address;
    }
}