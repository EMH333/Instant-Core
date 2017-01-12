package com.ethohampton.instant.Objects;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.HashMap;

/**
 * Created by ethohampton on 12/14/16.
 * default question type
 */
@Entity
@Cache
public class Question {

    @Id
    private Long id;
    private String creator;
    private long creationTime;
    private HashMap<String, String> options;
    private HashMap<String, Integer> optionVotes;

    @Deprecated
    public Question() {
    }

    public Question(long id, String creator, long creationTime, HashMap<String, String> options, HashMap<String, Integer> optionVotes) {
        this.id = id;
        this.creator = creator;
        this.creationTime = creationTime;
        this.options = options;
        this.optionVotes = optionVotes;
    }

    public Question(long creationTime, String creator, HashMap<String, String> options) {
        this.creationTime = creationTime;
        this.creator = creator;
        this.options = options;
    }

    public HashMap<String, Integer> getOptionVotes() {
        return optionVotes;
    }

    public void setOptionVotes(HashMap<String, Integer> optionVotes) {
        this.optionVotes = optionVotes;
    }

    public HashMap<String, String> getOptions() {
        return options;
    }

    public void setOptions(HashMap<String, String> options) {
        this.options = options;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the creationTime
     */
    public Long getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime the creationTime to set
     */
    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }


}

