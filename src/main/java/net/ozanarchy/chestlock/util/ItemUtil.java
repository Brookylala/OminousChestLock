package net.ozanarchy.chestlock.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.ozanarchy.chestlock.lock.PickType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtil {

    private static final PlainTextComponentSerializer TEXT_SERIALIZER = PlainTextComponentSerializer.plainText();
    private static NamespacedKey pickTypeKey; // This will need to be initialized from the plugin

    public static void setPickTypeKey(NamespacedKey key) {
        pickTypeKey = key;
    }

    public static ItemStack createPick(PickType type) {
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(type.displayName()));
            meta.setCustomModelData(type.modelData());
            if (pickTypeKey != null) {
                meta.getPersistentDataContainer().set(pickTypeKey, PersistentDataType.STRING, type.id());
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static PickType getPickType(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.TRIPWIRE_HOOK) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || pickTypeKey == null) {
            return null;
        }
        String id = meta.getPersistentDataContainer().get(pickTypeKey, PersistentDataType.STRING);
        if (id == null || id.isBlank()) {
            return null;
        }
        return PickType.fromId(id);
    }

    public static String getKeyName(ItemStack itemStack, boolean allowNormalKeys) {
        if (itemStack == null) {
            return null;
        }
        Material type = itemStack.getType();
        if (type != Material.OMINOUS_TRIAL_KEY && !(allowNormalKeys && type == Material.TRIAL_KEY)) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return null;
        }
        Component displayName = meta.displayName();
        if (displayName == null) {
            return null;
        }
        String name = TEXT_SERIALIZER.serialize(displayName).strip();
        return name.isEmpty() ? null : name;
    }

    public static boolean isDecorationItem(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        Material type = itemStack.getType();
        if (type == Material.ITEM_FRAME || type == Material.GLOW_ITEM_FRAME) {
            return true;
        }
        return Tag.SIGNS.isTagged(type) || type.name().endsWith("_HANGING_SIGN");
    }
}
