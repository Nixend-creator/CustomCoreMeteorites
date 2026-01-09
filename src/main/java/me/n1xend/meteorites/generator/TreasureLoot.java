package me.n1xend.meteorites.generator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;
import java.util.stream.Collectors;

public class TreasureLoot {

    private static final Random RANDOM = new Random();

    public static void fillChest(Inventory inventory, ConfigurationSection lootTable) {
        if (lootTable == null) return;

        List<ItemStack> items = new ArrayList<>();

        for (String key : lootTable.getKeys(false)) {
            ConfigurationSection entry = lootTable.getConfigurationSection(key);
            if (entry == null) continue;
            if (!entry.getBoolean("enabled", true)) continue;

            double chance = entry.getDouble("chance", 100.0);
            if (RANDOM.nextDouble() * 100.0 > chance) continue;

            ItemStack item = createItem(entry);
            if (item != null) items.add(item);
        }

        for (ItemStack item : items) {
            inventory.addItem(item);
        }
    }

    private static ItemStack createItem(ConfigurationSection entry) {
        String materialName = entry.getString("item-type");
        if (materialName == null) return null;
        Material mat = Material.matchMaterial(materialName.toUpperCase());
        if (mat == null) return null;

        // Поддержка диапазона количества, например: "4-12"
        int amount = 1;
        String amountStr = entry.getString("amount", "1");
        if (amountStr.contains("-")) {
            String[] parts = amountStr.split("-", 2);
            try {
                int min = Integer.parseInt(parts[0].trim());
                int max = Integer.parseInt(parts[1].trim());
                if (min <= max) {
                    amount = min + RANDOM.nextInt(max - min + 1);
                } else {
                    amount = min;
                }
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        } else {
            try {
                amount = Integer.parseInt(amountStr.trim());
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        }
        amount = Math.max(1, Math.min(amount, mat.getMaxStackSize()));

        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String rarity = entry.getString("rarity", "common").toLowerCase(Locale.ROOT);

        String displayName = entry.getString("display-name");
        if (displayName != null && !displayName.trim().isEmpty()) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }

        List<String> lore = entry.getStringList("lore");
        List<String> coloredLore = new ArrayList<>();
        if (!lore.isEmpty()) {
            coloredLore = lore.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
        }
        coloredLore.add("");
        coloredLore.add(getRarityTag(rarity));
        meta.setLore(coloredLore);

        if (entry.getBoolean("unbreakable", false)) {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        // 🔧 ЗАЧАРОВАНИЯ — БЕЗ СКРЫТИЯ!
        ConfigurationSection enchants = entry.getConfigurationSection("enchants");
        if (enchants != null) {
            for (String enchantKey : enchants.getKeys(false)) {
                Enchantment ench = Enchantment.getByName(enchantKey.toUpperCase());
                if (ench == null) continue;
                int level = enchants.getInt(enchantKey, 1);
                if (level <= 0) continue;
                meta.addEnchant(ench, level, true);
            }
            // ❌ УДАЛЕНО: meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            // Теперь зачарования видны, как после ванильного стола зачарования.
        }

        if (meta instanceof LeatherArmorMeta leatherMeta) {
            switch (rarity) {
                case "rare" -> leatherMeta.setColor(org.bukkit.Color.fromRGB(64, 64, 255));
                case "epic" -> leatherMeta.setColor(org.bukkit.Color.fromRGB(160, 32, 255));
                case "legendary" -> leatherMeta.setColor(org.bukkit.Color.fromRGB(255, 128, 0));
                default -> {}
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private static String getRarityTag(String rarity) {
        return switch (rarity) {
            case "rare" -> ChatColor.BLUE + "Редкость: РЕДКИЙ";
            case "epic" -> ChatColor.DARK_PURPLE + "Редкость: ЭПИЧЕСКИЙ";
            case "legendary" -> ChatColor.GOLD + "Редкость: ЛЕГЕНДАРНЫЙ";
            default -> ChatColor.GRAY + "Редкость: ОБЫЧНЫЙ";
        };
    }
}
