package com.ethohampton.instant.web;

import com.ethohampton.instant.Authentication.userManagment.UserDAO;
import com.ethohampton.instant.Database;
import com.ethohampton.instant.Util.Constants;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 12/16/16.
 * <p>
 * put question into the database
 */
@Singleton
public class PutQuestion extends BaseServlet {
    @Inject
    PutQuestion(Provider<UserDAO> daoProvider) {
        super(daoProvider);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        String temp = "";
        try {
            temp = req.getParameter("answers").trim();
        } catch (Exception e) {
            resp.sendError(400, "Invalid parsing of data");
        }
        String[] list = temp.split(Constants.ESCAPED_SEPARATOR);
        Set<String> answers = new HashSet<>();
        Collections.addAll(answers, list);
        resp.getWriter().println(Database.put("EMH", answers));//// FIXME: 12/16/16 Change so that author is changable

    }
}
