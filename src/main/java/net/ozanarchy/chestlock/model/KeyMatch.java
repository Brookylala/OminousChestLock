package net.ozanarchy.chestlock.model;

import org.bukkit.inventory.EquipmentSlot;

public record KeyMatch(String name, EquipmentSlot slot, boolean normal) {
}
