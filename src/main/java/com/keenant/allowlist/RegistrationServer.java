package com.keenant.allowlist;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class RegistrationServer implements Handler {
    private static final String VERIFICATION_CODE_JSON_KEY = "verificationCode";
    private final Javalin javalin = Javalin.create();
    private final Gson gson = new Gson();
    private final Logger logger;
    private final AllowlistConfig config;

    @Inject
    public RegistrationServer(Logger logger, AllowlistConfig config) {
        this.logger = logger;
        this.config = config;
    }

    // Verification codes recently received by the HTTP server. The boolean value is a dummy value.
    private final Cache<String, Boolean> recentlyRegisteredVerificationCodes = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build();

    public void start() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(RegistrationServer.class.getClassLoader());
        javalin.start(config.getHttpPort());
        Thread.currentThread().setContextClassLoader(classLoader);

        javalin.post("/", this);
    }

    public void stop() {
        javalin.stop();
    }

    @Override
    public void handle(Context context) {
        logger.log(Level.INFO, "Received HTTP request: " + context.body());
        JsonObject body = gson.fromJson(context.body(), JsonObject.class);
        recentlyRegisteredVerificationCodes.put(body.get(VERIFICATION_CODE_JSON_KEY).getAsString(), true);
        context.result("OK");
    }

    public boolean isRecentlyRegistered(String verificationCode) {
        return recentlyRegisteredVerificationCodes.getIfPresent(verificationCode) != null;
    }
}
