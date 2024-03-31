package com.iluyf.mc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public class Reward {
    class MonthReward {
        String name, id, quantity;
    }

    public ItemStack randomReward(long seed, String playerName, long annoDay) {
        MonthReward monthReward = new MonthReward();
        short monthCycle;
        int randomNumber = new Random(seed - annoDay).nextInt(100);
        for (monthCycle = 0; monthCycle <= Compute.commonYearMonthCount; ++monthCycle) {
            if (randomNumber < Short
                    .valueOf(Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle) + ".weight"))) {
                monthReward.name = Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle - 1) + ".name");
                monthReward.id = Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle - 1) + ".id");
                monthReward.quantity = Anno.annoConfig
                        .getString("reward.month" + String.valueOf(monthCycle - 1) + ".quantity");
                break;
            }
            if (monthCycle == Compute.commonYearMonthCount) {
                monthReward.name = Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle) + ".name");
                monthReward.id = Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle) + ".id");
                monthReward.quantity = Anno.annoConfig
                        .getString("reward.month" + String.valueOf(monthCycle) + ".quantity");
            }
        }
        Bukkit.getServer().sendMessage(Component.text()
                .append(Component.text("给予").decoration(BOLD, true))
                .append(Component.text("[" + playerName + "]", BLUE, UNDERLINED))
                .append(Component.text("月中随机奖励").decoration(BOLD, true))
                .append(Component.text("[" + monthReward.name + "]", YELLOW, UNDERLINED))
                .append(Component.text(monthReward.quantity, GREEN))
                .append(Component.text("个")));
        return rewardOutput(monthReward, seed, annoDay);
    }

    public ItemStack normalReward(long seed, String playerName, long annoDay) {
        MonthReward monthReward = new MonthReward();
        short normalNumber = (short) new Compute().annoToValue(annoDay)[1];
        for (short monthCycle = 0; monthCycle <= Compute.commonYearMonthCount; ++monthCycle) {
            monthReward.name = Anno.annoConfig.getString("reward.month" + String.valueOf(normalNumber) + ".name");
            monthReward.id = Anno.annoConfig.getString("reward.month" + String.valueOf(normalNumber) + ".id");
            monthReward.quantity = Anno.annoConfig
                    .getString("reward.month" + String.valueOf(normalNumber) + ".quantity");
        }
        Bukkit.getServer().sendMessage(Component.text()
                .append(Component.text("给予").decoration(BOLD, true))
                .append(Component.text("[" + playerName + "]", BLUE, UNDERLINED))
                .append(Component.text("月初固定奖励：").decoration(BOLD, true))
                .append(Component.text("[" + monthReward.name + "]", YELLOW, UNDERLINED))
                .append(Component.text(monthReward.quantity, GREEN))
                .append(Component.text("个")));
        return rewardOutput(monthReward, seed, annoDay);
    }

    private ItemStack rewardOutput(MonthReward monthReward, long seed, Long annoDay) {
        if (monthReward.name.equals("随机音乐唱片")) {
            ItemStack itemReward = musicDisc(Short.valueOf(monthReward.quantity), seed - annoDay);
            return itemReward;
        } else {
            ItemStack itemReward = new ItemStack(Material.getMaterial(monthReward.id.toUpperCase()),
                    Integer.valueOf(monthReward.quantity));
            return luckOrEnchantedBook(monthReward, itemReward);
        }
    }

    private ItemStack luckOrEnchantedBook(MonthReward monthReward, ItemStack itemReward) {
        if (monthReward.id.equalsIgnoreCase("potion")) {
            PotionMeta potionMeta = (PotionMeta) itemReward.getItemMeta();
            potionMeta.setBasePotionType(PotionType.LUCK);
            itemReward.setItemMeta(potionMeta);
            return itemReward;
        }
        Enchantment enchantment;
        if (monthReward.id.equalsIgnoreCase("ENCHANTED_BOOK")) {
            switch (monthReward.name) {
                case "附魔书（迅捷潜行）":
                    enchantment = Enchantment.SWIFT_SNEAK;
                    break;
                case "附魔书（经验修补）":
                    enchantment = Enchantment.MENDING;
                    break;
                default:
                    return itemReward;
            }
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemReward.getItemMeta();
            enchantmentStorageMeta.addStoredEnchant(enchantment, enchantment.getMaxLevel(), false);
            itemReward.setItemMeta(enchantmentStorageMeta);
        }
        return itemReward;
    }

    private ItemStack musicDisc(short quantity, long seed) {
        int randomNumber = new Random(seed).nextInt(15);
        String id = Anno.annoConfig.getString("reward.month11.ids.id" + String.valueOf(randomNumber + 1));
        ItemStack itemReward = new ItemStack(Material.getMaterial(id.toUpperCase()), Integer.valueOf(quantity));
        return itemReward;
    }

    public void giveReward(long annoDay) {
        Collection<? extends Player> onlinePlayerList = Bukkit.getOnlinePlayers();
        for (Iterator<? extends Player> iterator = onlinePlayerList.iterator(); iterator.hasNext();) {
            Player player = iterator.next();
            PlayerInventory inventory = player.getInventory();
            switch ((short) new Compute().annoToValue(annoDay)[2]) {
                case 1:
                    inventory.addItem(normalReward(player.getName().hashCode(), player.getName(), annoDay));
                    break;
                case 11:
                    inventory.addItem(randomReward(player.getName().hashCode(), player.getName(), annoDay));
                    break;
            }
        }
    }
}