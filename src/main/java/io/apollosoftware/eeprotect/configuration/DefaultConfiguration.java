package io.apollosoftware.eeprotect.configuration;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.lib.configuration.Configuration;
import lombok.Getter;

public class DefaultConfiguration extends Configuration<EEProtect> {


    public DefaultConfiguration() {
        super("config.yml");
    }


    @Getter
    private static String chatPrefix;


    @Getter
    private int maximumWaypoints;


    @Getter
    private int teleportRequestCooldown;


    @SuppressWarnings("unchecked")
    public void afterLoad() {
        chatPrefix = conf.getString("chatPrefix");
        maximumWaypoints = conf.getInt("maximumWaypoints");
        teleportRequestCooldown = conf.getInt("teleportRequestCooldown");
    }


    public void onSave() {
        // I got nothing m8
    }
}
