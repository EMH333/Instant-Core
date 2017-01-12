package com.ethohampton.instant.Caching;

import java.util.TimerTask;

/**
 * Created by ethohampton on 1/11/17.
 * <p>
 * updates memcache and datastore
 */

public class UpdateCache extends TimerTask {
    @Override
    public void run() {
        System.out.println("This works");
    }
}
