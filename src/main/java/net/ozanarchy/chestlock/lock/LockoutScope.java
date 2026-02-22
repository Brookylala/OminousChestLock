package net.ozanarchy.chestlock.lock;

public enum LockoutScope {
    CHEST,
    PLAYER;

    public static LockoutScope fromConfig(String value) {
        if (value == null) {
            return CHEST;
        }
        return switch (value.toLowerCase()) {
            case "player" -> PLAYER;
            default -> CHEST;
        };
    }
}
