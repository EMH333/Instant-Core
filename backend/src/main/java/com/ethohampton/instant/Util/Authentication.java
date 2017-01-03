package com.ethohampton.instant.Util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.OnSuccessListener;

/**
 * Created by ethohampton on 1/2/17.
 * <p>
 * Authenticates users
 */

public class Authentication {
    static boolean valid = false;
    static String email = "";
    static String username = "";
    static boolean emailVerified = false;


    public static boolean isVaild(String idToken) {
        valid = false;
        valid = FirebaseAuth.getInstance().verifyIdToken(idToken).isSuccessful();
        System.out.println(valid);
        return true;//return valid; // FIXME: 1/2/17 Make so it returns properly // STOPSHIP: 1/2/17 fix this now
    }

    //puts all info into static vars
    public static void getInfo(String idToken) {
        valid = false;
        email = "";
        username = "";
        emailVerified = false;
        FirebaseAuth.getInstance().verifyIdToken(idToken)
                .addOnSuccessListener(new OnSuccessListener<FirebaseToken>() {
                    @Override
                    public void onSuccess(FirebaseToken decodedToken) {
                        valid = true;
                        email = decodedToken.getEmail();
                        username = decodedToken.getName();
                        emailVerified = decodedToken.isEmailVerified();
                    }
                });
    }

}
