package com.keenant.allowlist;

import com.google.inject.Guice;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AllowlistJavaPlugin extends JavaPlugin {

  private AllowlistServer server;

  @Override
  public void onEnable() {
    try {
      server = Guice.createInjector(new GuiceModule(this)).getInstance(AllowlistServer.class);
      server.start();
    } catch (Throwable e) {
      System.err.println("Failed to start Allowlist plugin, shutting down!");
      Bukkit.shutdown();
    }
  }

  @Override
  public void onDisable() {
    server.stop();
  }
}
