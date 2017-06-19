// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        UserListServlet.java  (11-Nov-2011)
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
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.objectify.cmd.Query;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Singleton
public class UserListServlet extends BaseServlet {
    static final Logger LOG = Logger.getLogger(UserListServlet.class.getName());

    private static final int MAX_QUERY_OFFSET = 50;

    @Inject
    UserListServlet(Provider<UserDAO> daoProvider) {
        super(daoProvider);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int draw = intParameter("draw", request, -1);
            int start = intParameter("start", request, 0);
            int length = intParameter("length", request, 10);
            String search = request.getParameter("search[value]");
            HttpSession session = request.getSession();
            doOutput(session, response, search, start, length, draw);
        } catch (Exception e) {
            LOG.severe("Error posting to list: " + e.getMessage());
            issueJson(response, HTTP_STATUS_INTERNAL_SERVER_ERROR, MESSAGE, "Error generating JSON: " + e.getMessage());
        }
    }

    private void doOutput(HttpSession session, HttpServletResponse response, String sSearch, int start, int length, int draw)
            throws JSONException, IOException {
        UserDAO dao = new UserDAO();
        long nUsers = dao.getCount();
        Map<String, Object> map = Maps.newHashMap();
        map.put("recordsTotal", nUsers);
        map.put("recordsFiltered", nUsers);
        map.put("draw", draw);

        List<User> users = users(session, dao, sSearch, start, length);
        map.put("data", users);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("MMM dd yyyy", Locale.US));
        String output = mapper.writeValueAsString(map);
        issue(MIME_APPLICATION_JSON, HTTP_STATUS_OK, output, response); // This is JSON
    }

    private List<User> users(HttpSession session, UserDAO dao, String sSearch, int start, int length) {
        if (sSearch != null && !"".equals(sSearch)) {
            User user = dao.findUser(sSearch);
            List<User> list = Lists.newArrayList();
            if (user != null) {
                list.add(user);
            }
            return list;
        } else {
            List<User> list = Lists.newArrayList();

            Cursor cursor = (Cursor) session.getAttribute("cursor_" + start);
            if (cursor == null && start >= MAX_QUERY_OFFSET) {
                // Doing a query with an offset is very expensive as you have to read through
                // everything up to the offset.  So we just bail out if that it the case. The front
                // end should display an error of some sort.
                LOG.warning("Can't process query for offset " + start + " as its too expensive");
                return list;
            }

            Query<User> query = ofy().load().type(User.class)
                    .limit(length)
                    .order("-dateRegistered");

            query = (cursor != null) ? query.startAt(cursor) : query.offset(start);

            QueryResultIterator<User> it = query.iterator();
            while (it.hasNext()) {
                User user = it.next();
                list.add(user);
            }

            session.setAttribute("cursor_" + (start + length), it.getCursor());
            return list;
        }
    }
}
