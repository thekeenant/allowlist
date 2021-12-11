package com.keenant.allowlist;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class AllowlistConfig {

  private static final String HTTP_PORT_KEY = "http-port";
  private static final String KICK_MESSAGE_KEY = "kick-message";
  private static final String ALLOWLIST_KEY = "allowlist";
  private static final String ALLOWLIST_UUID_KEY = "uuid";
  private static final String ALLOWLIST_VERIFICATION_CODE_KEY = "verification-code";

  private final Logger logger;
  private final JavaPlugin plugin;

  @Inject
  public AllowlistConfig(Logger logger, JavaPlugin plugin) {
    this.logger = logger;
    this.plugin = plugin;
  }

  public void saveToAllowlist(UUID uuid, String verificationCode) {
    Map<String, String> newAllowlistElement = new HashMap<>();
    newAllowlistElement.put(ALLOWLIST_UUID_KEY, uuid.toString());
    newAllowlistElement.put(ALLOWLIST_VERIFICATION_CODE_KEY, verificationCode);

    List<Map<?, ?>> allowlistMapList = plugin.getConfig().getMapList(ALLOWLIST_KEY);
    allowlistMapList.add(newAllowlistElement);

    plugin.getConfig().set(ALLOWLIST_KEY, allowlistMapList);
    plugin.saveConfig();
  }

  public boolean isAllowlisted(UUID uuid) {
    for (Map<?, ?> map : plugin.getConfig().getMapList(ALLOWLIST_KEY)) {
      try {
        String uuidString = (String) map.get(ALLOWLIST_UUID_KEY);
        if (Objects.equals(uuid.toString(), uuidString)) {
          return true;
        }
      } catch (Exception e) {
        logger.log(Level.WARNING, "Failed parsing config", e);
      }
    }
    return false;
  }

  public int getHttpPort() {
    return plugin.getConfig().getInt(HTTP_PORT_KEY);
  }

  public String getKickMessage() {
    return plugin.getConfig().getString(KICK_MESSAGE_KEY);
  }
}
