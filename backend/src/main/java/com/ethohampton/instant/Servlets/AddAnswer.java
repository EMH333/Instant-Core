package com.ethohampton.instant.Servlets;

import com.ethohampton.instant.Database;
import com.ethohampton.instant.Objects.BasicServlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 12/16/16.
 * <p>
 * adds vote for one answer
 */

public class AddAnswer extends BasicServlet {
    public AddAnswer() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Long startTime = System.currentTimeMillis();
        resp.setContentType("text/plain");
        //sets what question and answer to vote on
        Long id = 0L;
        int vote = 0;
        try {
            String query = req.getQueryString();
            id = Long.parseLong(query.split("&")[0].trim());
            vote = Integer.parseInt(query.split("&")[1].trim());
        } catch (Exception e) {
            resp.sendError(400, "URL Invalid");
        }

        try {
            Database.addAnswer(id, vote);
            resp.getWriter().println(Database.get(id).getOptionVotes().get((String.valueOf(vote))));
            resp.getWriter().println(System.currentTimeMillis() - startTime);
            resp.setStatus(200);
        } catch (NullPointerException e) {
            resp.sendError(404, "Invalid ID");
        }

    }
}
