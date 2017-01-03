package com.ethohampton.instant.Servlets;

import com.ethohampton.instant.Objects.BasicServlet;
import com.ethohampton.instant.Objects.Question;
import com.ethohampton.instant.OfyService;
import com.ethohampton.instant.Util.Constants;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 12/17/16.
 * <p>
 * gets a random question
 */

public class RandomQuestion extends BasicServlet {
    public RandomQuestion() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        List<Key<Question>> keys = OfyService.ofy().load().type(Question.class).keys().list();
        Random r = new Random();
        int i = r.nextInt(keys.size());
        Question q = OfyService.ofy().load().key(keys.get(i)).now();

        if (q == null) {
            resp.sendError(404, "Invalid ID");
            return;
        }

        //add id to response
        resp.getWriter().println(q.getId() + Constants.SEPARATOR);

        //add answers to response
        if (q.getOptions() != null && !q.getOptions().isEmpty()) {
            for (Map.Entry<String, String> t : q.getOptions().entrySet()) {
                //insure the vote map exists and has the value in it before returning
                if (q.getOptionVotes() != null && q.getOptionVotes().containsKey(t.getKey())) {
                    resp.getWriter().println(t.getValue() + Constants.SEPARATOR + q.getOptionVotes().get(t.getKey()));
                } else {
                    resp.getWriter().println(t.getValue() + Constants.SEPARATOR + "0");
                }
            }
        }
    }
}
