package com.kevinkwok.kkcomboskills.utils;

import org.bukkit.ChatColor;
import org.bukkit.event.block.Action;

public class InputHandler {

    public static String getMouseInput(Action action) {
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            return "L"; // 左键
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            return "R"; // 右键
        }
        return null;
    }

    public static String removeColorCodes(String text) {
        return text == null ? null : ChatColor.stripColor(text);
    }


}
