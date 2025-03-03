package com.kevinkwok.kkcomboskills;

import com.kevinkwok.kkcomboskills.commands.CommandHandler;
import com.kevinkwok.kkcomboskills.listeners.OnPlayerInteractEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KKComboSkills extends JavaPlugin {

    OnPlayerInteractEvent onPlayerInteractEvent;
    Map<String, ComboConfig> comboPlans;
    List<String> itemNameKeys;
    String itemLoreKey;
    int comboCount;

    public static KKComboSkills Instance;

    @Override
    public void onEnable() {

        getLogger().info("KKComboSkill has been enabled!");
        onPlayerInteractEvent = new OnPlayerInteractEvent(this);
        getServer().getPluginManager().registerEvents(onPlayerInteractEvent, this);
        getCommand("kkcs").setExecutor(new CommandHandler());

        saveDefaultConfig();
        loadComboPlans();

        Instance = this;

    }

    private void loadComboPlans() {
        // 确保 Plans 文件夹存在
        File folder = new File(getDataFolder(), "Plans");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 加载 plan1.yml 文件
        File file = new File(folder, "plan1.yml");
        if (!file.exists()) {
            saveResource("Plans/plan1.yml", false); // 如果文件不存在，则从资源中复制
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // 读取 ItemNameKey, ItemLoreKey 和 ComboCount
        itemNameKeys = config.getStringList("ItemNameKey");
        itemLoreKey = config.getString("ItemLoreKey");
        comboCount = config.getInt("ComboCount");

        // 读取 Combo 配置
        comboPlans = new HashMap<>();
        for (String key : config.getConfigurationSection("Combo").getKeys(false)) {
            String skillID = config.getString("Combo." + key + ".SkillID");
            String permission = config.getString("Combo." + key + ".Permission");
            comboPlans.put(key, new ComboConfig(skillID, permission));
        }
    }

    public List<String> getItemNameKeys() {
        return itemNameKeys;
    }

    public String getItemLoreKey() {
        return itemLoreKey;
    }

    public int getComboCount() {
        return comboCount;
    }

    public Map<String, ComboConfig> getComboPlans() {
        return comboPlans;
    }

    public static class ComboConfig {
        private final String skillID;
        private final String permission;

        public ComboConfig(String skillID, String permission) {
            this.skillID = skillID;
            this.permission = permission;
        }

        public String getSkillID() {
            return skillID;
        }

        public String getPermission() {
            return permission;
        }
    }

    @Override
    public void onDisable() {

        getLogger().info("KKComboSkill has been disabled!");

    }

}
