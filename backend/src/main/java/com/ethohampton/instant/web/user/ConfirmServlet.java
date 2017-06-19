// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        ConfirmServlet.java  (02-Nov-2011)
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

import com.ethohampton.instant.Authentication.gae.User;
import com.ethohampton.instant.Authentication.gae.UserDAO;
import com.ethohampton.instant.web.BaseServlet;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.googlecode.objectify.VoidWork;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * This servlet registers a user for the first time and also changes the password
 * if the user has lost it, or just wants a new one.
 * A change of password is accompanied by a <code>change</code> parameter.
 */
@Singleton
public class ConfirmServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(ConfirmServlet.class.getName());

    @Inject
    ConfirmServlet(Provider<UserDAO> daoProvider) {
        super(daoProvider);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            final String code = Preconditions.checkNotNull(request.getParameter(CODE), "The registration code can't be null");
            final String userName = Preconditions.checkNotNull(request.getParameter(USERNAME), "The user name can't be null");
            final String password = Preconditions.checkNotNull(request.getParameter(PASSWORD), "The password can't be null");

            boolean isChange = "true".equals(request.getParameter(FORGOT));

            final UserDAO dao = new UserDAO();
            String userNameFromCode = dao.findUserNameFromValidCode(code);
            if (userNameFromCode != null) {

                // there are two objects, from two different groups, which can nowadays be run in a transaction
                ofy().transact(new VoidWork() {
                    public void vrun() {
                        User user = dao.findUser(userName);
                        if (user == null) {
                            user = new User(userName, password, defaultRoles(), defaultPermissions());
                            dao.saveUser(user, true);
                        } else {
                            user.setPassword(password);
                            dao.saveUser(user, false);
                        }
                        dao.register(code, userName);
                    }
                });

                Subject sub = SecurityUtils.getSubject();
                UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
                sub.login(token);

                issueJson(response, HTTP_STATUS_OK,
                        MESSAGE, isChange
                                ? "OK, password changed for user name " + userName
                                : "OK, you're registered with user name " + userName);
            } else {
                issueJson(response, HTTP_STATUS_NOT_FOUND, MESSAGE, "Wrong code, or code is expired: \"" + code + "\", you'll need to retry");
            }
        } catch (Exception e) {
            issueJson(response, HTTP_STATUS_INTERNAL_SERVER_ERROR, MESSAGE, "Error in confirm: " + e.getMessage());
        }
    }

    private Set<String> defaultRoles() {
        return Sets.newHashSet("user");
    }

    private Set<String> defaultPermissions() {
        return Sets.newHashSet();
    }

}
