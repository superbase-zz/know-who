package com.fun.crawl.base.utils;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;

public class EvernoteYingXiangApi  extends EvernoteApi {
    private static final String SANDBOX_URL = "https://app.yinxiang.com";

    public EvernoteYingXiangApi() {
    }

    public String getRequestTokenEndpoint() {
        return "https://app.yinxiang.com/oauth";
    }

    public String getAccessTokenEndpoint() {
        return "https://app.yinxiang.com/oauth";
    }

    public String getAuthorizationUrl(Token requestToken) {
        return String.format("https://app.yinxiang.com/OAuth.action?oauth_token=%s", requestToken.getToken());
    }
}
