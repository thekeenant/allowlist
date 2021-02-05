package com.keenant.allowlist;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public class GuiceModule extends AbstractModule {
    private final JavaPlugin plugin;

    public GuiceModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Provides
    public JavaPlugin providePlugin() {
        return plugin;
    }

    @Provides
    public Random provideRandom() {
        return new Random();
    }

    @Provides
    public List<Listener> provideListeners(LoginListener loginListener) {
        return Lists.newArrayList(loginListener);
    }
}
