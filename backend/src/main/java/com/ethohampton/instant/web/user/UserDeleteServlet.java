// Copyright (c) 2014 Cilogi. All Rights Reserved.
//
// File:        UserDeleteServlet.java  (03/10/14)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Cilogi (the Author) and may not be used, sold, licenced, 
// transferred, copied or reproduced in whole or in part in 
// any manner or form or in or on any media to any person other than 
// in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.ethohampton.instant.web.user;


import com.ethohampton.instant.Authentication.gae.User;
import com.ethohampton.instant.Authentication.gae.UserDAO;
import com.ethohampton.instant.web.BaseServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class UserDeleteServlet extends BaseServlet {
    static final Logger LOG = LoggerFactory.getLogger(UserDeleteServlet.class);

    @Inject
    public UserDeleteServlet(Provider<UserDAO> daoProvider) {
        super(daoProvider);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String userName = request.getParameter(USERNAME);
            UserDAO dao = daoProvider.get();
            User user = dao.findUser(userName);
            if (user != null) {
                if (isCurrentUserAdmin()) {
                    dao.deleteUser(user);
                    issueJson(response, HTTP_STATUS_OK,
                            MESSAGE, "User " + userName + " is deleted");
                } else {
                    issueJson(response, HTTP_STATUS_OK,
                            MESSAGE, "Only admins can delete users", CODE, "404");
                }
            } else {
                LOG.warn("Can't find user " + userName);
                issueJson(response, HTTP_STATUS_NOT_FOUND, MESSAGE, "Can't find user " + userName);
            }
        } catch (Exception e) {
            LOG.error("Suspend failure: " + e.getMessage());
            issueJson(response, HTTP_STATUS_INTERNAL_SERVER_ERROR, MESSAGE, "Error generating JSON: " + e.getMessage());
        }
    }
}
