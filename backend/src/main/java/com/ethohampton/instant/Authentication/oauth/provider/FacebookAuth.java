// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        FacebookAuth.java  (05-Oct-2012)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used, sold, licenced, 
// transferred, copied or reproduced in whole or in part in 
// any manner or form or in or on any media to any person other than 
// in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.ethohampton.instant.Authentication.oauth.provider;


import com.ethohampton.instant.Authentication.gae.UserAuthType;
import com.ethohampton.instant.Authentication.oauth.OAuthInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FacebookAuth extends AuthBase implements IOAuthProviderInfo {
    static final Logger LOG = Logger.getLogger(FacebookAuth.class.getName());

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;

    private final String apiKey;
    private final String apiSecret;
    private final String host;

    @Inject
    public FacebookAuth(@Named("social.site") String site) {
        LOG.info("prefix is " + site);
        String prefix = "fb." + site;
        Properties props = new Properties();
        loadProperties(props, "/social.properties");
        apiKey = props.getProperty(prefix + ".apiKey");
        apiSecret = props.getProperty(prefix + ".apiSecret");
        host = props.getProperty(prefix + ".host");
    }

    public static String logoutUrl(String redirect, String accessToken) throws IOException {
        String redirectOK = OAuthEncoder.encode(redirect);
        return "https://www.facebook.com/logout.php?next=" + redirectOK + "&access_token=" + accessToken;
    }

    @Override
    public UserAuthType getUserAuthType() {
        return UserAuthType.FACEBOOK;
    }

    @Override
    public String loginURL(String callbackUri) {
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(makeAbsolute(callbackUri, host))
                .scope("email")
                .build();
        return service.getAuthorizationUrl(EMPTY_TOKEN);
    }

    @Override
    public String reAuthenticateURL(String callbackUri) {
        return loginURL(callbackUri) + "&auth_type=reauthenticate";
    }

    @Override
    public OAuthInfo getUserInfo(String code, String callBackUrl) {
        JSONObject obj = getUserInfoJSON(code, callBackUrl);
        return new OAuthInfo.Builder(UserAuthType.FACEBOOK)
                .errorString(errorString(obj))
                .email(obj.optString("email"))
                .token(obj.optString("access_token"))
                .build();
    }

    private JSONObject getUserInfoJSON(String code, String callBackUrl) {
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(makeAbsolute(callBackUrl, host))
                .scope("email")
                .build();
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        try {
            JSONObject obj = new JSONObject(response.getBody());
            obj.put("access_token", accessToken.getToken());
            return obj;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public void revokeToken(String token, HttpServletRequest request, HttpServletResponse response,
                            String redirectURL) throws IOException {
        String redirectHome = ProviderUtil.makeRoot(request.getRequestURL().toString(), redirectURL);

        String url = logoutUrl(redirectHome, token);
        response.sendRedirect(response.encodeRedirectURL(url));
    }

}
