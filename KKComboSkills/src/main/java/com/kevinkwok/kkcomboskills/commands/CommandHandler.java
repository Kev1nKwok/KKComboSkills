package com.kevinkwok.kkcomboskills.commands;

import com.kevinkwok.kkcomboskills.KKComboSkills;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length < 1) {
            sender.sendMessage("使用方法: /kkcs reload");
            return false;
        }

        if(args[0].equals("reload")){
            handleReloadCommand(sender);
        }

        return false;
    }

    private boolean handleReloadCommand(CommandSender sender) {
        KKComboSkills.Instance.reloadConfig();
        reloadPlanFiles();
        sender.sendMessage("相關文件已重新加载。");
        return true;
    }

    private void reloadPlanFiles() {
        File menuFolder = new File(KKComboSkills.Instance.getDataFolder(), "Plans");
        if (menuFolder.exists() && menuFolder.isDirectory()) {
            File[] menuFiles = menuFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (menuFiles != null) {
                for (File file : menuFiles) {
                    YamlConfiguration.loadConfiguration(file);
                }
            }
        }
    }
}
