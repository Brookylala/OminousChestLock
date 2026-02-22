package net.ozanarchy.chestlock.model;

import org.bukkit.World;

import java.util.UUID;

public record LocationData(String worldName, UUID worldUuid, String realm, int x, int y, int z) {
    public static String mapRealm(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> "overworld";
            case NETHER -> "nether";
            case THE_END -> "end";
            default -> "custom";
        };
    }
}
