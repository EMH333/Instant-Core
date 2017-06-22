// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        GaeUser.java  (26-Oct-2011)
// Author:      tim

//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.ethohampton.instant.Authentication.user;

//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

@Cache
@Entity
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {
    static final Logger LOG = Logger.getLogger(User.class.getName());


    @Id
    private String name;

    @Index
    private String email;

    @Index
    private Date dateRegistered;

    private boolean isSuspended;

    /**
     * For objectify to create instances on retrieval
     */
    private User() {
    }

    public User(String name, String email) {
        Preconditions.checkNotNull(name, "User name can't be null");
        Preconditions.checkNotNull(email, "User email can't be null");

        this.name = name;
        this.email = email;
        this.dateRegistered = new Date();
        this.isSuspended = false;
    }


    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }


    public Date getDateRegistered() {
        return dateRegistered == null ? null : new Date(dateRegistered.getTime());
    }

    //@JsonIgnore
    public boolean isRegistered() {
        return getDateRegistered() != null;
    }

    public void register() {
        dateRegistered = new Date();
    }

    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }



    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User u = (User) o;
            return Objects.equal(getName(), u.getName()) &&
                    Objects.equal(getEmail(), u.getEmail());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, email);
    }
/*
    public String toJSONString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String out = mapper.writeValueAsString(this);
            return out;
        } catch (JsonProcessingException e) {
            LOG.severe("Can't convert GaeUser " + this + " to JSON string");
            return "";
        }
    }*/
}
