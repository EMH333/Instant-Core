// Copyright (c) 2010 Tim Niblett All Rights Reserved.
//
// File:        ServeModule.java  (05-Oct-2010)
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


package com.ethohampton.instant.Guice;


import com.ethohampton.instant.web.MailQueueServlet;
import com.ethohampton.instant.web.MailReceiveServlet;
import com.ethohampton.instant.web.RegisterServlet;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.cache.AsyncCacheFilter;

import org.apache.shiro.web.servlet.ShiroFilter;

import java.util.Map;
import java.util.logging.Logger;


public class ServeModule extends ServletModule {
    static final Logger LOG = Logger.getLogger(ServeModule.class.getName());

    private final String userBaseUrl;

    public ServeModule(String userBaseUrl) {
        Preconditions.checkArgument(userBaseUrl != null && !userBaseUrl.endsWith("/"));
        this.userBaseUrl = userBaseUrl;
    }

    private static Map<String, String> map(String... params) {
        Preconditions.checkArgument(params.length % 2 == 0, "You have to have an even number of map params");
        Map<String, String> map = Maps.newHashMap();
        for (int i = 0; i < params.length; i += 2) {
            map.put(params[i], params[i + 1]);
        }
        return map;
    }

    @Override
    protected void configureServlets() {
        filter("/*").through(ShiroFilter.class);
        filter("/*").through(AsyncCacheFilter.class);
        //filter("/*").through(AppstatsFilter.class, map("calculateRpcCosts", "true"));

        serve(userBaseUrl + "/register").with(RegisterServlet.class);
        serve(userBaseUrl + "/registermail").with(MailQueueServlet.class);

        // Lets check mail to see when stuff bounces
        serve("/_ah/mail/*").with(MailReceiveServlet.class);
        // serve("/appstats/*").with(AppstatsServlet.class);
    }
}
