package com.ethohampton.instant.Util;

import com.ethohampton.instant.Objects.Question;

import java.util.Map;

/**
 * Created by ethohampton on 1/17/17.
 * Utility class for Question class
 */

public class QuestionUtils {
    /**
     * @param question question wanting to format
     * @return serialized question for public consumption
     */
    public static String format(Question question) {
        StringBuilder formatted = new StringBuilder();
        if (question == null) {
            return "";
        }
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            //adds creator and amount of answers
            formatted.append(question.getCreator()).append(Constants.SEPARATOR);
            formatted.append(question.getOptions().size()).append(Constants.SEPARATOR);

            //adds answers and allows for answers with no votes
            for (Map.Entry<String, String> t : question.getOptions().entrySet()) {
                //insure the vote map exists and has the value in it before returning
                if (question.getOptionVotes() != null && question.getOptionVotes().containsKey(t.getKey())) {
                    formatted.append(t.getValue()).append(Constants.SEPARATOR).append(question.getOptionVotes().get(t.getKey())).append(Constants.SEPARATOR);
                } else {
                    formatted.append(t.getValue()).append(Constants.SEPARATOR).append("0").append(Constants.SEPARATOR);
                }
            }
        }
        return formatted.toString();
    }
}
