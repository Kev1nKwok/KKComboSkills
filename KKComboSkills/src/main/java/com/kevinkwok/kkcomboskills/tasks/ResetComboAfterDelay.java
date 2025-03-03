package com.kevinkwok.kkcomboskills.tasks;

import com.kevinkwok.kkcomboskills.KKComboSkills;
import com.kevinkwok.kkcomboskills.listeners.OnPlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ResetComboAfterDelay extends BukkitRunnable {

    public final KKComboSkills plugin;
    private final Player player;
    private OnPlayerInteractEvent onPlayerInteractEvent;

    public ResetComboAfterDelay(KKComboSkills plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis();
        // 检查时间
        if (System.currentTimeMillis() - lastTime >= 800) {
            onPlayerInteractEvent.resetCombo(player);
        }
    }

}
