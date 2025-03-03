package com.kevinkwok.kkcomboskills.actions;

import com.kevinkwok.kkcomboskills.KKComboSkills;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import org.bukkit.entity.Player;

public class SkillAction {
    static BukkitAPIHelper apiHelper = MythicMobs.inst().getAPIHelper();

    KKComboSkills plugin;

    public SkillAction(KKComboSkills plugin) {
        this.plugin = plugin;
    }

    public static void castSkillWithDelay(KKComboSkills plugin, Player player, String comboKey) {

        KKComboSkills.ComboConfig config = plugin.getComboPlans().get(comboKey);
        if (config != null && player.hasPermission(config.getPermission())) {

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                castSkill(player, config.getSkillID());
            }, 5L); // 0.25秒 = 5 ticks
        }
    }

    public static void castSkill(Player player, String skillName) {
        apiHelper.castSkill(player, skillName);
    }
}
