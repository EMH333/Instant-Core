package com.ethohampton.instant;

import com.ethohampton.instant.Objects.Question;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Created by ethohampton on 12/14/16.
 * objectify service
 */

public class OfyService {
    static {
        factory().register(Question.class);
    }
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
