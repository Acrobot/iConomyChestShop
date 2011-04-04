package com.Acrobot.iConomyChestShop;

import com.Acrobot.iConomyChestShop.MinecartMania.MinecartManiaChest;
import java.util.logging.Level;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Acrobot
 */
public class ShopManager extends PlayerListener {

    public static void buy(PlayerInteractEvent event) {
        Sign sign = (Sign) event.getClickedBlock().getState();
        int price = SignManager.buyPrice(sign);
        Player player = event.getPlayer();
        if (price == 0) {
            player.sendMessage(ConfigManager.getLanguage("No_buying_from_this_shop"));
            return;
        }
        String[] line = sign.getLines();
        ItemStack is = Basic.getItemStack(line[3]);
        if (is == null) {
            return;
        }
        Inventory playerInv = player.getInventory();
        if (playerInv.firstEmpty() == -1) {
            player.sendMessage(ConfigManager.getLanguage("Your_inventory_is_full"));
            return;
        }
        int amount = Integer.parseInt(line[1]);
        Block chestBlock = event.getClickedBlock().getFace(BlockFace.valueOf(ConfigManager.getString("position").toUpperCase()));
        if(chestBlock.getTypeId() != 54){
            return;
        }
        String buyer = player.getName();
        String seller = line[0];
        if (!iConomyManager.hasEnough(buyer, price)) {
            player.sendMessage(ConfigManager.getLanguage("You_have_got_not_enough_money"));
            return;
        }
        if (line[0].toLowerCase().replace(" ", "").equals("adminshop")) {
            adminBuy(event);
            return;
        }
        Chest normalChest = (Chest) chestBlock.getState();
        MinecartManiaChest chest = new MinecartManiaChest(normalChest);
        is.setAmount(amount);
        int itemInChestAmount = ChestManager.getItemAmount(chest, is);
        
        if (itemInChestAmount < amount) {
            player.sendMessage(ConfigManager.getLanguage("Shop_is_out_of_stock"));
            Player ownerPlayer = iConomyChestShop.getBukkitServer().getPlayer(seller);
            if (ownerPlayer == null) {
                return;
            }
            ownerPlayer.sendMessage(ConfigManager.getLanguage("Your_shop_is_out_of_stock"));
            return;
        }
        
        
        player.getInventory().addItem(is);
        ChestManager.removeItems(chest, is);
        
        player.updateInventory(); // Do not use this unless really needed, like now
        
        iConomyManager.substract(buyer, price);
        iConomyManager.add(seller, price);
        
        Logging.log(buyer + " bought " + is.getType() + " with durability of " 
                + is.getDurability() + " from " + seller + " for " + price + " " 
                + iConomyManager.getCurrency());
        ConfigManager.buyingString(amount, is.getType().name(), seller, player, price);
        
    }

    public static void sell(PlayerInteractEvent event) {
        Sign sign = (Sign) event.getClickedBlock().getState();
        String[] line = sign.getLines();
        ItemStack is = Basic.getItemStack(line[3]);
        Player player = event.getPlayer();
        Inventory playerInv = player.getInventory();
        int price = SignManager.sellPrice(sign);
        int amount = Integer.parseInt(line[1]);
        String seller = player.getName();
        String shop = line[0];
        
        if (is == null) {
            return;
        }
        if (price == 0) {
            player.sendMessage(ConfigManager.getLanguage("No_selling_to_this_shop"));
            return;
        }
        Block chestBlock = event.getClickedBlock().getFace(BlockFace.valueOf(ConfigManager.getString("position").toUpperCase()));
        if(chestBlock.getTypeId() != 54){
            return;
        }
        Chest normalChest = (Chest) chestBlock.getState();
        MinecartManiaChest chest = new MinecartManiaChest(normalChest);
        if (ChestManager.firstEmpty(chest) == -1) {
            player.sendMessage(ConfigManager.getLanguage("Chest_is_full"));
            return;
        }
        is.setAmount(amount);
        int amountOfIS = Basic.getItemAmountFromInventory(playerInv, is);
        if(amountOfIS < amount){
            player.sendMessage(ConfigManager.getLanguage("You_have_not_enough_items"));
            return;
        }
        if (line[0].toLowerCase().replace(" ", "").equals("adminshop")) {
            adminSell(event);
            return;
        }
        if (!iConomyManager.hasEnough(shop, price)) {
            player.sendMessage(ConfigManager.getLanguage("Seller_has_not_enough_money"));
            return;
        }
        
        
        
        ChestManager.addItem(chest, is);
        //playerInv.remove(is);
        Basic.removeItemStackFromInventory(playerInv, is);
        
        player.updateInventory(); // Do not use this unless really needed, like now
        
        iConomyManager.substract(shop, price);
        iConomyManager.add(seller, price);
        
        Logging.log(seller + " sold " + is.getType() + " with durability of " 
                + is.getDurability() + " to " + shop + " for " + price + " " 
                + iConomyManager.getCurrency());
        ConfigManager.sellingString(amount, is.getType().name(), shop, player, price);
    }

    public static void adminSell(PlayerInteractEvent event) {
        Sign sign = (Sign) event.getClickedBlock().getState();
        String[] line = sign.getLines();
        ItemStack is = Basic.getItemStack(line[3]);
        Player player = event.getPlayer();
        Inventory playerInv = player.getInventory();
        int price = SignManager.sellPrice(sign);
        int amount = Integer.parseInt(line[1]);
        String seller = player.getName();
        
        
        is.setAmount(amount);
        Basic.removeItemStackFromInventory(playerInv, is);
        
        player.updateInventory(); // Do not use this unless really needed, like now
        iConomyManager.add(seller, price);
        
        Logging.log(seller + " sold " + is.getType() + " with durability of " 
                + is.getDurability() + " to admin shop for " + price + " " 
                + iConomyManager.getCurrency());
        ConfigManager.sellingString(amount, is.getType().name(), "admin shop", player, price);
    }

    public static void adminBuy(PlayerInteractEvent event) {
        Sign sign = (Sign) event.getClickedBlock().getState();
        int price = SignManager.buyPrice(sign);
        Player player = event.getPlayer();
        String[] line = sign.getLines();
        ItemStack is = Basic.getItemStack(line[3]);
        if (is == null) {
            return;
        }
        int amount = Integer.parseInt(line[1]);
        is.setAmount(amount);
        String buyer = player.getName();
        player.getInventory().addItem(is);
        
        player.updateInventory(); // Do not use this unless really needed, like now
        
        iConomyManager.substract(buyer, price);
        
        Logging.log(buyer + " bought " + is.getType() + " with durability of " 
                + is.getDurability() + " from admin shop for " + price + " " 
                + iConomyManager.getCurrency());
        ConfigManager.buyingString(amount, is.getType().name(), "admin shop", player, price);
    }
}