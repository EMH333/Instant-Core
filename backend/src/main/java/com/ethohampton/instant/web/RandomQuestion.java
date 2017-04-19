package com.ethohampton.instant.web;

import com.ethohampton.instant.Database;
import com.ethohampton.instant.Objects.BasicServlet;
import com.ethohampton.instant.Objects.Question;
import com.ethohampton.instant.OfyService;
import com.ethohampton.instant.Util.QuestionUtils;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.List;
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
        //get list of all keys and pick a random one
        List<Key<Question>> keys = OfyService.ofy().load().type(Question.class).keys().list();
        Random r = new Random();
        int i = r.nextInt(keys.size());

        resp.getWriter().println(keys.size());


        //get question
        Question q = Database.get(keys.get(i).getId());
        //makes sure question is not null
        if (q == null) {
            resp.sendError(404, "Invalid ID");
            return;
        }
        //send response
        resp.getWriter().println(QuestionUtils.format(q));

    }
}
