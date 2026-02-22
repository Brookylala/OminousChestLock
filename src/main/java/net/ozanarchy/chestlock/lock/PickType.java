package net.ozanarchy.chestlock.lock;

public enum PickType {
    RUSTY("rusty_pick", "Rusty Lock Pick", 11001),
    NORMAL("normal_pick", "Lock Pick", 11002),
    SILENCE("silence_pick", "Silence Lock Pick", 11003),
    LODESTONE("lodestone_pick", "Lodestone Lock Pick", 11004);

    private final String id;
    private final String displayName;
    private final int modelData;

    PickType(String id, String displayName, int modelData) {
        this.id = id;
        this.displayName = displayName;
        this.modelData = modelData;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public int modelData() {
        return modelData;
    }

    public static PickType fromId(String id) {
        for (PickType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
