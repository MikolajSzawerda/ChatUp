package com.chatup.chatup_server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;


@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {
    private static final long secondsInHour = 1000;
    private static final long hoursInDay = 24;
    private static final long millisecondsInSecond = 1000;

    private String secretKey;
    private String tokenPrefix;
    private long daysExpire;

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public String getTokenPrefix() { return tokenPrefix; }
    public void setTokenPrefix(String tokenPrefix) { this.tokenPrefix = tokenPrefix; }

    public long getDaysExpire() { return daysExpire; }
    public void setDaysExpire(long daysExpire) {
        this.daysExpire = daysExpire;
    }

    public long getExpireMilliseconds() {
        return daysExpire * hoursInDay * secondsInHour * millisecondsInSecond;
    }

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }
}
