package com.ethohampton.instant.web;

import com.ethohampton.instant.Database;
import com.ethohampton.instant.Objects.BasicServlet;
import com.ethohampton.instant.Objects.Question;
import com.ethohampton.instant.Util.QuestionUtils;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 12/16/16.
 * <p>
 * adds vote for one answer
 */
@Singleton
public class AddAnswer extends BasicServlet {
    public AddAnswer() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
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

        Question q = Database.addAnswer(id, vote);
        if (q != null) {//true if successful vote
            resp.getWriter().println(QuestionUtils.format(q));
            resp.setStatus(200);
        } else {
            resp.sendError(404, "Invalid Answer or ID");
        }

    }
}
