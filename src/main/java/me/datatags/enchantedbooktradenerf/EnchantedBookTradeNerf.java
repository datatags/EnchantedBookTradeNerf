package me.datatags.enchantedbooktradenerf;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class EnchantedBookTradeNerf extends JavaPlugin implements Listener {
    private int maxLevel;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        maxLevel = getConfig().getInt("max level", 5);
        getServer().getPluginManager().registerEvents(this,this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.VILLAGER) return;
        Villager villager = (Villager) event.getRightClicked();
        for (int i = 0; i < villager.getRecipeCount(); i++) {
            MerchantRecipe recipe = villager.getRecipe(i);
            if (recipe.getResult().getType() != Material.ENCHANTED_BOOK) return;
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) recipe.getResult().getItemMeta();
            boolean changed = false;
            for (Map.Entry<Enchantment,Integer> entry : meta.getStoredEnchants().entrySet()) {
                if (entry.getValue() > maxLevel) {
                    meta.removeStoredEnchant(entry.getKey());
                    meta.addStoredEnchant(entry.getKey(), maxLevel, true);
                    changed = true;
                }
            }
            if (!changed) return;
            ItemStack newStack = new ItemStack(Material.ENCHANTED_BOOK);
            newStack.setItemMeta(meta);
            MerchantRecipe newRecipe = new MerchantRecipe(newStack, recipe.getUses(), recipe.getMaxUses(), recipe.hasExperienceReward(),
                    recipe.getVillagerExperience(), recipe.getPriceMultiplier(), recipe.getDemand(), recipe.getSpecialPrice());
            newRecipe.setIngredients(recipe.getIngredients());
            villager.setRecipe(i, newRecipe);
        }
    }
}