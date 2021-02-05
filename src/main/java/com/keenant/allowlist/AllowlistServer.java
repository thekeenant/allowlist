package com.keenant.allowlist;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Singleton
public class AllowlistServer {
    private final JavaPlugin plugin;
    private final List<Listener> eventListeners;
    private final RegistrationServer registrationServer;

    @Inject
    public AllowlistServer(JavaPlugin plugin, List<Listener> eventListeners, RegistrationServer registrationServer) {
        this.plugin = plugin;
        this.eventListeners = eventListeners;
        this.registrationServer = registrationServer;
    }

    public void start() {
        plugin.saveDefaultConfig();
        eventListeners.forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
        registrationServer.start();
    }

    public void stop() {
        eventListeners.forEach(HandlerList::unregisterAll);
        registrationServer.stop();
    }

}
