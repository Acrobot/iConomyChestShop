package com.Acrobot.iConomyChestShop;

import java.io.File;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author Acrobot
 */
public class ConfigManager {

    public static boolean getBoolean(String node) {
        load();
        return ChestShopConfig.getBoolean(node, false);
    }

    public static String getString(String node) {
        load();
        return ChestShopConfig.getString(node);
    }

    public static int getInt(String node) {
        load();
        return ChestShopConfig.getInt(node, 0);
    }

    public static String getLanguage(String node) {
        load();
        String str = "";
        String prefix = ChestShopConfig.getNode("lang").getString("prefix", "[Shop]");
        str = prefix + ChestShopConfig.getNode("lang").getString(node, "Error - no translation for " + node);
        str = Basic.colorChat(str);

        return str;
    }

    public static int getAlias(String node) {
        load();
        return ChestShopConfig.getNode("aliases").getInt(node, 0);
    }

    public static void load() {
        ChestShopConfig.load();
    }

    public static void buyingString(int amount, String item, String owner, Player player, int cost) {
        String str = getLanguage("You_bought_items");
        str = str.replace("<amount>", amount + "");
        String alias = Basic.returnAlias(item);
        String itemAlias = "";
        if (alias != null) {
            itemAlias = alias;
        } else {
            itemAlias = item;
        }
        str = str.replace("<item>", itemAlias);
        str = str.replace("<owner>", owner);

        player.sendMessage(str);
        moneyLeft(player);
        buyingStringForShopOwner(amount, itemAlias, owner, player, cost);
    }

    public static void buyingStringForShopOwner(int amount, String itemAlias, String owner, Player player, int cost) {
        String str = getLanguage("Somebody_bought_items_from_your_shop");
        str = str.replace("<amount>", amount + "");
        str = str.replace("<item>", itemAlias);
        str = str.replace("<buyer>", player.getName());
        str = str.replace("<cost>", formattedBalance(cost));
        Player ownerPlayer = iConomyChestShop.getBukkitServer().getPlayer(owner);
        if (ownerPlayer == null) {
            return;
        }
        ownerPlayer.sendMessage(str);
    }
    
    public static void sellingStringForShopOwner(int amount, String itemAlias, String owner, Player player, int cost) {
        String str = getLanguage("Somebody_sold_items_to_your_shop");
        str = str.replace("<amount>", amount + "");
        str = str.replace("<item>", itemAlias);
        str = str.replace("<seller>", player.getName());
        str = str.replace("<cost>", formattedBalance(cost));
        Player ownerPlayer = iConomyChestShop.getBukkitServer().getPlayer(owner);
        if (ownerPlayer == null) {
            return;
        }
        ownerPlayer.sendMessage(str);
    }

    public static void sellingString(int amount, String item, String owner, Player player, int cost) {
        String str = getLanguage("You_sold_items");
        str = str.replace("<amount>", amount + "");
        String alias = Basic.returnAlias(item);
        String itemAlias = "";
        if (alias != null) {
            itemAlias = alias;
        } else {
            itemAlias = item;
        }
        str = str.replace("<item>", itemAlias);
        str = str.replace("<owner>", owner);
        str = str.replace("<cost>", formattedBalance(cost));

        player.sendMessage(str);
        sellingStringForShopOwner(amount, itemAlias, owner, player, cost);
        moneyLeft(player);
    }

    public static String formattedBalance(double balance) {
        String formatedBalance = iConomyManager.formatedBalance(balance);
        return formatedBalance;
    }

    public static void moneyLeft(Player player) {
        double balance = iConomyManager.balance(player.getName());
        String msg = getLanguage("Your_balance");
        msg = msg.replace("<money>", formattedBalance(balance));
        player.sendMessage(msg);
    }
    public static Configuration ChestShopConfig = new Configuration(new File("plugins/iConomyChestShop/config.yml"));
}
