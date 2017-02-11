// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        BaseServlet.java  (31-Oct-2011)
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


package com.ethohampton.instant.Authentication.web;

import com.ethohampton.instant.Authentication.gae.GaeUserDAO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NonNull;


public class BaseServlet extends HttpServlet implements ParameterNames, MimeTypes {
    static final Logger LOG = Logger.getLogger(BaseServlet.class.getName());

    protected final String MESSAGE = "message";
    protected final String CODE = "code";

    protected final int HTTP_STATUS_OK = 200;
    protected final int HTTP_STATUS_NOT_FOUND = 404;
    protected final int HTTP_STATUS_FORBIDDEN = 403;
    protected final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
    protected Provider<GaeUserDAO> daoProvider;
    private CreateDoc create;

    protected BaseServlet(Provider<GaeUserDAO> daoProvider) {
        this.daoProvider = daoProvider;
    }

    /**
     * Login and make sure you then have a new session.  This helps prevent session fixation attacks.
     *
     * @param token
     * @param subject
     */
    protected static void loginWithNewSession(AuthenticationToken token, Subject subject) {
        Session originalSession = subject.getSession();

        Map<Object, Object> attributes = Maps.newLinkedHashMap();
        Collection<Object> keys = originalSession.getAttributeKeys();
        for (Object key : keys) {
            Object value = originalSession.getAttribute(key);
            if (value != null) {
                attributes.put(key, value);
            }
        }
        originalSession.stop();
        subject.login(token);

        Session newSession = subject.getSession();
        for (Object key : attributes.keySet()) {
            newSession.setAttribute(key, attributes.get(key));
        }
    }

    @Inject
    protected void setCreate(CreateDoc create) {
        this.create = create;
    }

    protected void issue(String mimeType, int returnCode, String output, HttpServletResponse response) throws IOException {
        response.setContentType(mimeType);
        response.setStatus(returnCode);
        response.getWriter().println(output);
    }

    protected void issueJson(HttpServletResponse response, int status, String... args) throws IOException {
        Preconditions.checkArgument(args.length % 2 == 0, "There must be an even number of strings");
        try {
            JSONObject obj = new JSONObject();
            for (int i = 0; i < args.length; i += 2) {
                obj.put(args[i], args[i + 1]);
            }
            issueJson(response, status, obj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected void issueJson(HttpServletResponse response, int status, JSONObject obj) throws IOException {
        issue(MIME_APPLICATION_JSON, status, obj.toString(), response);
    }

    protected void showView(HttpServletResponse response, String templateName, Object... args) throws IOException {
        showView(response, templateName, CreateDoc.map(args));
    }

    protected void showView(HttpServletResponse response, String templateName, Map<String, Object> args) throws IOException {
        try {
            String html = create.createDocumentString(templateName, args);
            issue(MIME_TEXT_HTML, HTTP_STATUS_OK, html, response);
        } catch (Exception e) {
            issue(MIME_TEXT_PLAIN, HTTP_STATUS_NOT_FOUND, "Can't find " + templateName + ": " + e.getMessage(), response);
        }
    }

    protected String view(String templateName, Object... args) {
        return create.createDocumentString(templateName, CreateDoc.map(args));
    }

    protected String stringParameter(@NonNull String name, HttpServletRequest request, String deflt) {
        String s = request.getParameter(name);
        return (s == null) ? deflt : s;
    }

    protected int intParameter(String name, HttpServletRequest request, int deflt) {
        String s = request.getParameter(name);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return deflt;
        }
    }

    protected boolean booleanParameter(String name, HttpServletRequest request, boolean deflt) {
        String s = request.getParameter(name);
        return (s == null) ? deflt : Boolean.parseBoolean(s);
    }

    protected boolean isCurrentUserAdmin() {
        Subject subject = SecurityUtils.getSubject();
        return subject.hasRole("admin");
    }

    @SuppressWarnings({"unchecked"})
    protected GaeUser getCurrentGaeUser() {
        Subject subject = SecurityUtils.getSubject();
        String email = (String) subject.getPrincipal();
        if (email == null) {
            return null;
        } else {
            GaeUserDAO dao = daoProvider.get();
            return dao.findUser(email);
        }
    }


}
