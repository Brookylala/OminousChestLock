package net.ozanarchy.chestlock.model;

import net.ozanarchy.chestlock.lock.PickType;
import org.bukkit.inventory.EquipmentSlot;

public record PickMatch(PickType type, EquipmentSlot slot) {
}
