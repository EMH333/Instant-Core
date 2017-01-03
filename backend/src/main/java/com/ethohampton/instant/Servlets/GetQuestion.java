package com.ethohampton.instant.Servlets;

import com.ethohampton.instant.Database;
import com.ethohampton.instant.Objects.BasicServlet;
import com.ethohampton.instant.Objects.Question;
import com.ethohampton.instant.Util.Authentication;
import com.ethohampton.instant.Util.Constants;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 12/15/16.
 * gets a question
 */

public class GetQuestion extends BasicServlet {
    public GetQuestion() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        String userID = "";
        //find authentication cookie
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals("uid")) {
                    userID = cookie.getValue();
                }
            }
        }
        //if authentication is valid then continue
        if (Authentication.isVaild(userID)) {
            //gets string
            String queryString = req.getQueryString();
            Long id = 0L;
            try {
                id = Long.parseLong(queryString);
            } catch (NumberFormatException e) {
                resp.sendError(404, "Invalid ID");
            }


            //get question
            Question temp = Database.get(id);
            if (temp == null) {
                resp.sendError(404, "Question not found");
                return;
            }
            if (temp.getOptions() != null && !temp.getOptions().isEmpty()) {
                for (Map.Entry<String, String> t : temp.getOptions().entrySet()) {
                    //insure the vote map exists and has the value in it before returning
                    if (temp.getOptionVotes() != null && temp.getOptionVotes().containsKey(t.getKey())) {
                        resp.getWriter().println(t.getValue() + Constants.SEPARATOR + temp.getOptionVotes().get(t.getKey()));
                    } else {
                        resp.getWriter().println(t.getValue() + Constants.SEPARATOR + "0");
                    }
                }
            }
        } else {
            resp.sendError(401);
        }
    }
}
