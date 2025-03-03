package com.kevinkwok.kkcomboskills.listeners;

import com.kevinkwok.kkcomboskills.actions.SkillAction;
import com.kevinkwok.kkcomboskills.KKComboSkills;
import com.kevinkwok.kkcomboskills.tasks.ResetComboAfterDelay;
import com.kevinkwok.kkcomboskills.utils.InputHandler;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnPlayerInteractEvent implements Listener {

    final KKComboSkills plugin;
    final SkillAction skillAction;
    final Map<Player, StringBuilder> playerCombos = new HashMap<>();
    final Map<Player, Long> lastInputTime = new HashMap<>();

    int inputCount = 0;

    public OnPlayerInteractEvent(KKComboSkills plugin) {
        this.plugin = plugin;
        this.skillAction = new SkillAction(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (isValidItem(item)) {
            String input = InputHandler.getMouseInput(event.getAction());
            if (input != null) {
                if (addCombo(player, input)) {
                    if (shouldDisplayTitle(player)) {
                        checkAndDisplayTitle(player);
                    }
                }
                checkCombo(player);
                resetComboAfterDelay(player);
            }
        }
    }

    private boolean isValidItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;

        String itemName = InputHandler.removeColorCodes(item.getItemMeta().getDisplayName());
        if (itemName == null) return false;

        File folder = new File(plugin.getDataFolder(), "Plans");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files != null) {
            for (File file : files) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                List<String> itemNameKeys = config.getStringList("ItemNameKey");
                String itemLoreKey = config.getString("ItemLoreKey");


                if (itemNameKeys.contains(itemName)) {

                    if (item.getItemMeta().getLore() != null &&
                            item.getItemMeta().getLore().stream().anyMatch(loreLine -> loreLine.contains(itemLoreKey))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean addCombo(Player player, String key) {
        StringBuilder combo = playerCombos.getOrDefault(player, new StringBuilder());

        if (combo.length() == 0) {
            char firstKey = key.charAt(0);
            boolean validCombo = false;

            File folder = new File(plugin.getDataFolder(), "Plans");
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));

            if (files != null) {
                for (File file : files) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                    for (String comboKey : config.getConfigurationSection("Combo").getKeys(false)) {
                        char expectedFirstChar = comboKey.charAt(0);
                        if (firstKey == expectedFirstChar) {
                            validCombo = true;
                            break;
                        }
                    }
                    if (validCombo) break;
                }
            }

            if (!validCombo) {
                resetCombo(player);
                return false;
            }
        }

        playerCombos.put(player, combo.append(key));
        lastInputTime.put(player, System.currentTimeMillis());
        return true;
    }

    private boolean shouldDisplayTitle(Player player) {
        StringBuilder combo = playerCombos.get(player);
        return combo != null && combo.length() > 0;
    }

    private void checkAndDisplayTitle(Player player) {
        StringBuilder combo = playerCombos.get(player);
        if (combo == null) return;

        StringBuilder display = new StringBuilder();
        int comboCount = plugin.getComboCount();

        for (int i = 0; i < comboCount; i++) {
            if (i < combo.length()) {
                display.append(combo.charAt(i)).append(" ");
                inputCount++;
            } else {
                display.append("_ ");
            }
        }

        player.sendTitle(display.toString().trim(), " ", 10, 20, 10);
    }

    private void checkCombo(Player player) {
        StringBuilder combo = playerCombos.get(player);
        int comboCount = plugin.getComboCount();
        if (combo == null) return;

        String comboString = combo.toString();
        for (String key : plugin.getComboPlans().keySet()) {
            if (comboString.endsWith(key)) {
                skillAction.castSkillWithDelay(plugin, player, key);
                resetCombo(player);
                return;
            }
        }

        if(inputCount >= comboCount * 2){
            resetCombo(player);
        }
    }

    public void resetCombo(Player player) {
        playerCombos.remove(player);
        lastInputTime.remove(player);
        inputCount=0;
    }

    private void resetComboAfterDelay(Player player) {
        long lastInput = lastInputTime.getOrDefault(player, 0L);
        if (System.currentTimeMillis() - lastInput >= 1000) {
            resetCombo(player);
        } else {
            new ResetComboAfterDelay(plugin, player).runTaskLater(plugin, 20L);
        }
    }
}
