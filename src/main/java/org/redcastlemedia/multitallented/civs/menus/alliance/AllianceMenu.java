package org.redcastlemedia.multitallented.civs.menus.alliance;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.redcastlemedia.multitallented.civs.localization.LocaleManager;
import org.redcastlemedia.multitallented.civs.alliances.Alliance;
import org.redcastlemedia.multitallented.civs.alliances.AllianceManager;
import org.redcastlemedia.multitallented.civs.civilians.Civilian;
import org.redcastlemedia.multitallented.civs.items.CVItem;
import org.redcastlemedia.multitallented.civs.items.ItemManager;
import org.redcastlemedia.multitallented.civs.menus.CivsMenu;
import org.redcastlemedia.multitallented.civs.menus.CustomMenu;
import org.redcastlemedia.multitallented.civs.menus.MenuIcon;
import org.redcastlemedia.multitallented.civs.menus.MenuManager;
import org.redcastlemedia.multitallented.civs.towns.Town;
import org.redcastlemedia.multitallented.civs.towns.TownManager;
import org.redcastlemedia.multitallented.civs.towns.TownType;
import org.redcastlemedia.multitallented.civs.util.Constants;
import org.redcastlemedia.multitallented.civs.util.Util;

@CivsMenu(name = "alliance") @SuppressWarnings("unused")
public class AllianceMenu extends CustomMenu {

    @Override
    protected ItemStack createItemStack(Civilian civilian, MenuIcon menuIcon, int count) {
        Alliance alliance = (Alliance) MenuManager.getData(civilian.getUuid(), "alliance");
        if (menuIcon.getKey().equals("members")) {
            int page = (int) MenuManager.getData(civilian.getUuid(), "page");
            int startIndex = page * menuIcon.getIndex().size();
            String[] memberNames = new String[alliance.getMembers().size()];
            memberNames = alliance.getMembers().toArray(memberNames);
            if (memberNames.length <= startIndex + count) {
                return new ItemStack(Material.AIR);
            }
            String townName = memberNames[startIndex + count];
            Town town = TownManager.getInstance().getTown(townName);
            CVItem cvItem = ItemManager.getInstance().getItemType(town.getType()).clone();
            cvItem.setDisplayName(town.getName());
            cvItem.getLore().clear();
            ItemStack itemStack = cvItem.createItemStack();
            putActions(civilian, menuIcon, itemStack, count);
            return itemStack;
        } else if (menuIcon.getKey().equals("last-rename")) {
            if (alliance == null || alliance.getLastRenamedBy() == null) {
                return new ItemStack(Material.AIR);
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(alliance.getLastRenamedBy());
            if (offlinePlayer.getName() != null) {
                CVItem lastRenameCVItem = menuIcon.createCVItem(civilian.getLocale(), count);
                lastRenameCVItem.setMat(Material.PLAYER_HEAD);
                ItemStack is = lastRenameCVItem.createItemStack();
                SkullMeta isMeta = (SkullMeta) is.getItemMeta();
                isMeta.setDisplayName(offlinePlayer.getName());
                isMeta.setLore(Util.textWrap(civilian, LocaleManager.getInstance().getTranslation(civilian.getLocale(),
                        "last-renamed-by").replace("$1", offlinePlayer.getName())));
                isMeta.setOwningPlayer(offlinePlayer);
                is.setItemMeta(isMeta);
                putActions(civilian, menuIcon, is, count);
                return is;
            }
        } else if (menuIcon.getKey().equals("icon")) {
            CVItem icon = menuIcon.createCVItem(civilian.getLocale(), count);
            icon.setDisplayName(alliance.getName());
            ItemStack itemStack = icon.createItemStack();
            putActions(civilian, menuIcon, itemStack, count);
            return itemStack;
        } else if (menuIcon.getKey().equals("rename") ||
                menuIcon.getKey().equals("leave-alliance")) {
            Town selectedTown = (Town) MenuManager.getData(civilian.getUuid(), "selectedTown");
            if (selectedTown == null) {
                return new ItemStack(Material.AIR);
            }
            CVItem cvItem = menuIcon.createCVItem(civilian.getLocale(), count);
            if (menuIcon.getDesc() != null && !menuIcon.getDesc().isEmpty()) {
                cvItem.setLore(Util.textWrap(civilian, LocaleManager.getInstance().getTranslation(civilian.getLocale(),
                        menuIcon.getDesc()).replace("$1", alliance.getName())));
            }
            ItemStack itemStack = cvItem.createItemStack();
            putActions(civilian, menuIcon, itemStack, count);
            return itemStack;
        } else if ("select-town".equals(menuIcon.getKey())) {
            Town selectedTown = (Town) MenuManager.getData(civilian.getUuid(), "selectedTown");
            if (selectedTown == null) {
                return new ItemStack(Material.AIR);
            }
            TownType selectedTownType = (TownType) ItemManager.getInstance().getItemType(selectedTown.getType());
            CVItem cvItem = selectedTownType.getShopIcon(civilian.getLocale()).clone();
            cvItem.setDisplayName(selectedTown.getName());
            if (menuIcon.getDesc() != null && !menuIcon.getDesc().isEmpty()) {
                cvItem.setLore(Util.textWrap(civilian, LocaleManager.getInstance().getTranslation(civilian.getLocale(),
                        menuIcon.getDesc())));
            }
            ItemStack itemStack = cvItem.createItemStack();
            putActions(civilian, menuIcon, itemStack, count);
            return itemStack;
        }
        return super.createItemStack(civilian, menuIcon, count);
    }

    @Override
    public Map<String, Object> createData(Civilian civilian, Map<String, String> params) {
        Map<String, Object> data = new HashMap<>();

        String allianceName = params.get("alliance");
        Alliance alliance = AllianceManager.getInstance().getAlliance(allianceName);
        data.put("alliance", alliance);

        if (params.containsKey("page")) {
            data.put("page", Integer.parseInt(params.get("page")));
        } else {
            data.put("page", 0);
        }
        int maxPage = (int) Math.ceil((double) alliance.getMembers().size() / (double) itemsPerPage.get("members"));
        maxPage = maxPage > 0 ? maxPage - 1 : 0;
        data.put("maxPage", maxPage);

        if (alliance.getLastRenamedBy() != null) {
            data.put("lastRename", alliance.getLastRenamedBy().toString());
        }
        if (!params.containsKey("selectedTown")) {
            for (String townName : alliance.getMembers()) {
                Town town = TownManager.getInstance().getTown(townName);
                if (town.getPeople().containsKey(civilian.getUuid()) &&
                        town.getPeople().get(civilian.getUuid()).contains(Constants.OWNER)) {
                    data.put("selectedTown", town);
                    break;
                }
            }
        } else {
            data.put("selectedTown",
                    TownManager.getInstance().getTown(params.get("selectedTown")));
        }
        return data;
    }

    @Override
    public boolean doActionAndCancel(Civilian civilian, String actionString, ItemStack clickedItem) {
        if ("leave-alliance".equals(actionString)) {
            Town town = (Town) MenuManager.getData(civilian.getUuid(), "selectedTown");
            if (town == null) {
                return true;
            }
            Alliance alliance = (Alliance) MenuManager.getData(civilian.getUuid(), "alliance");
            for (String townName : new HashSet<>(alliance.getMembers())) {
                if (townName.equals(town.getName())) {
                    continue;
                }
                Town currentTown = TownManager.getInstance().getTown(townName);
                AllianceManager.getInstance().unAlly(town, currentTown);
            }
            return true;
        }
        return super.doActionAndCancel(civilian, actionString, clickedItem);
    }
}
