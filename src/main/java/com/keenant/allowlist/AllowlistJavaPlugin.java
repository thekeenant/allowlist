package com.keenant.allowlist;

import com.google.inject.Guice;
import org.bukkit.plugin.java.JavaPlugin;

public class AllowlistJavaPlugin extends JavaPlugin {
    private AllowlistServer server;

    @Override
    public void onEnable() {
        server = Guice.createInjector(new GuiceModule(this)).getInstance(AllowlistServer.class);
        server.start();
    }

    @Override
    public void onDisable() {
        server.stop();
    }
}
