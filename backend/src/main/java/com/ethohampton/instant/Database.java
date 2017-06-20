package com.ethohampton.instant;

import com.ethohampton.instant.Objects.Question;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.Result;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by ethohampton on 12/15/16.
 * Connects to cloud data store
 */

public class Database {

    public static Long put(String author, Set<String> questions) {
        //puts all answer possibilities into hashmap with number mapped values
        HashMap<String, String> options = new HashMap<>();
        int i = 0;
        for (String op : questions) {
            options.put(String.valueOf(i), op);
            i++;
        }
        //creates new question and saves it
        Question question = new Question(System.currentTimeMillis(), author, options);
        Result<Key<Question>> entity = OfyService.ofy().save().entity(question);
        return entity.now().getId();
    }

    //saves question
    private static boolean put(Question question) {
        OfyService.ofy().save().entity(question).now();
        return true;
    }

    /**
     * @param id     of the question
     * @param answer number of the question
     * @return a question if successful, null if not
     */
    public static Question addAnswer(Long id, int answer) {
        Question question = get(id);
        String ans = String.valueOf(answer);
        if (question != null) {
            if (question.getOptions().containsKey(ans)) {
                //get current number of votes and add one to it for that answer as long as it exists
                int votes;
                if (question.getOptionVotes() != null && question.getOptionVotes().containsKey(ans)) {
                    votes = question.getOptionVotes().get(ans);
                    votes += 1;
                    question.getOptionVotes().put(ans, votes);
                } else {//if map doesn't exist then create it
                    votes = 1;
                    HashMap<String, Integer> opVotes = new HashMap<>();
                    opVotes.put(ans, votes);
                    question.setOptionVotes(opVotes);
                }
                //if the put into the database works then return true
                if (put(question)) {
                    return question;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    public static Question get(Long id) {
        LoadResult<Question> loadResult = OfyService.ofy().load().type(Question.class).id(id);
        return loadResult.now();
    }

}
