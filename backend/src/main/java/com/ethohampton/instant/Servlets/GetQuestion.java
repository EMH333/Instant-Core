package com.ethohampton.instant.Servlets;

import com.ethohampton.instant.Database;
import com.ethohampton.instant.Objects.BasicServlet;
import com.ethohampton.instant.Objects.Question;
import com.ethohampton.instant.Util.QuestionUtils;

import java.io.IOException;

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

        //if authentication is valid then continue // TODO: 1/3/17 add authentication
        if (true) {
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
            //formats question and sends response
            resp.getWriter().println(QuestionUtils.format(temp));

        } else {
            resp.sendError(401);
        }
    }
}
