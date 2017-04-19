// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        SettingsServlet.java  (14-Nov-2011)
// Author:      tim

//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.ethohampton.instant.web.user;


import com.ethohampton.instant.Authentication.gae.GaeUser;
import com.ethohampton.instant.Authentication.gae.GaeUserDAO;
import com.ethohampton.instant.web.BaseServlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class SettingsServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(SettingsServlet.class.getName());

    @Inject
    SettingsServlet(Provider<GaeUserDAO> daoProvider) {
        super(daoProvider);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            GaeUserDAO dao = new GaeUserDAO();
            String userName = request.getParameter(USERNAME);
            String password = request.getParameter(PASSWORD);

            Subject subject = SecurityUtils.getSubject();
            String subjectID = (String) subject.getPrincipal();
            GaeUser user = dao.findUser(subjectID);
            if (subject.isAuthenticated() && user != null) {
                if (userName.equals(subjectID)) {
                    if (password != null) {
                        user.setPassword(password);
                        dao.saveUser(user, false);
                        issueJson(response, HTTP_STATUS_OK, MESSAGE, "password changed successfully");
                    } else {
                        issueJson(response, HTTP_STATUS_FORBIDDEN, MESSAGE, "Your password is invalid");
                    }
                } else {
                    issueJson(response, HTTP_STATUS_NOT_FOUND, MESSAGE, "You're not " + userName);
                }
            } else {
                if (user == null) {
                    issueJson(response, HTTP_STATUS_FORBIDDEN, MESSAGE, "You're not a user I can set the password for");
                } else {
                    issueJson(response, HTTP_STATUS_FORBIDDEN, MESSAGE, "You're not authenticated");
                }
            }
        } catch (Exception e) {
            issueJson(response, HTTP_STATUS_INTERNAL_SERVER_ERROR, MESSAGE, "Oops, error in settings: " + e.getMessage());
        }
    }
}
