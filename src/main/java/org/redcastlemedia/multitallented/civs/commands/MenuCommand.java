package org.redcastlemedia.multitallented.civs.commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.redcastlemedia.multitallented.civs.Civs;
import org.redcastlemedia.multitallented.civs.localization.LocaleConstants;
import org.redcastlemedia.multitallented.civs.localization.LocaleManager;
import org.redcastlemedia.multitallented.civs.menus.MenuManager;
import org.redcastlemedia.multitallented.civs.util.Constants;

@CivsCommand(keys = { "menu" })
public class MenuCommand extends CivCommand {

    public boolean runCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Civs.getPrefix() + "Unable to open menu for non-players");
            return true;
        }
        Player player = (Player) commandSender;

        if (Civs.perm != null && !Civs.perm.has(player, Constants.MENU_PERMISSION)) {
            player.sendMessage(Civs.getPrefix() + LocaleManager.getInstance().getTranslation(player,
                    LocaleConstants.PERMISSION_DENIED));
            return true;
        }

        String menuName;
        HashMap<String, String> params = new HashMap<>();
        if (strings.length < 2) {
            menuName = "main";
        } else {
            menuName = strings[1].split("\\?")[0];
            if (strings[1].contains("?")) {
                for (String param : strings[1].split("\\?")[1].split("&")) {
                    params.put(param.split("=")[0], param.split("=")[1]);
                }
            }
        }
        MenuManager.clearHistory(player.getUniqueId());
        MenuManager.getInstance().openMenu(player, menuName, params);
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender commandSender) {
        return commandSender instanceof Player;
    }
}
