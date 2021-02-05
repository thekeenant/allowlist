package com.keenant.allowlist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginListener implements Listener {
    private final Logger logger;
    private final RegistrationServer registrationServer;
    private final AllowlistConfig config;

    // Expiring cache mapped from user id to verification code.
    private final LoadingCache<UUID, String> verificationCodeCache;

    @Inject
    public LoginListener(Logger logger, RegistrationServer registrationServer, VerificationCodeGenerator verificationCodeGenerator, AllowlistConfig config) {
        this.logger = logger;
        this.registrationServer = registrationServer;
        this.config = config;
        verificationCodeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build(new CacheLoader<UUID, String>() {
                    public String load(UUID key) {
                        return verificationCodeGenerator.get();
                    }
                });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID userId = event.getUniqueId();
        if (config.isAllowlisted(userId)) {
            return;
        }

        try {
            String verificationCode = verificationCodeCache.get(userId);
            boolean registered = registrationServer.isRecentlyRegistered(verificationCode);

            if (registered) {
                config.saveToAllowlist(userId, verificationCode);
                verificationCodeCache.invalidate(event.getUniqueId());
            } else {
                event.setKickMessage(String.format(config.getKickMessage(), verificationCode));
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during login", e);
            event.setKickMessage("Some error occurred, please try again");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }
}
