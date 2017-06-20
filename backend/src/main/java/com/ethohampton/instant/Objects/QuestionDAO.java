package com.ethohampton.instant.Objects;


import com.ethohampton.instant.Authentication.user.BaseDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ethohampton on 6/19/17.
 * DAO for questions
 */

public class QuestionDAO extends BaseDAO<Question> {
    static final Logger LOG = LoggerFactory.getLogger(QuestionDAO.class);

    public QuestionDAO() {
        super(Question.class);
    }
}
