package com.ethohampton.instant;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by ethohampton on 12/14/16.
 * utility methods for all classes
 */

public class Util {
    public static long generateUUID(){
        Random r = new SecureRandom();
        return r.nextLong();
    }
}
