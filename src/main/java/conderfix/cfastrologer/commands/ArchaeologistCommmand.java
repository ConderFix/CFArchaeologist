package conderfix.cfastrologer.commands;

import conderfix.cfastrologer.ArchaeologistPlugin;
import conderfix.cfastrologer.gui.MenuArchaeologist;
import conderfix.cfastrologer.utils.ConfigUtil;
import conderfix.cfastrologer.utils.HexUtil;
import conderfix.cfastrologer.utils.TypeRarities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArchaeologistCommmand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            if (sender.hasPermission("astrologger.admin")) {
                for (String list : ArchaeologistPlugin.inst.getConfig().getStringList("messages.help")) {
                    player.sendMessage(list);
                }
                return true;
            }
        }
        if (args[0].equals("open")) {
            MenuArchaeologist.open(player);
        }
        if (args[0].equals("reload")) {
            if (sender.hasPermission("astrologger.admin")) {
                ArchaeologistPlugin.inst.reloadConfig();
                player.sendMessage("Успешно! (Может работать криво, так что используйте лучше /plugman reload CFAstrologer)");
                return true;
            }
            return true;
        }
        if (args[0].equals("give")) {
            if (sender.hasPermission("astrologger.admin")) {
                final Player target = Bukkit.getPlayer(args[1]);
                final Integer amount = Integer.valueOf(args[2]);

                if (target == null) {
                    sender.sendMessage("Укажи правильный ник");
                    return true;
                }
                ItemStack itemStack = new ItemStack(Material.valueOf(ConfigUtil.getString("exchange-item.material")), amount);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ConfigUtil.getString("exchange-item.name"));
                List<String> lore = ArchaeologistPlugin.inst.getConfig().getStringList("exchange-item.lore");
                List<String> translatedLore = new ArrayList<>();
                for (String line : lore) {
                    String translatedLine = HexUtil.translate(line);
                    translatedLore.add(translatedLine);
                }
                NamespacedKey key = new NamespacedKey(ArchaeologistPlugin.inst, "astrologer-item");
                itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");
                final boolean isGlow = ArchaeologistPlugin.inst.getConfig().getBoolean("exchange-item.glow");
                if (isGlow) {
                    itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                itemStack.setItemMeta(itemMeta);
                target.getInventory().addItem(itemStack);
            }
        }
        if (args[0].equals("addloot")) {
            if (sender.hasPermission("astrologger.admin")) {
                if (args[1] == null) {
                    sender.sendMessage("Укажи шансы!");
                    return true;
                }
                if (args[2] == null) {
                    sender.sendMessage("Укажи тип редкости! (Плохой - SHIT, средний - FINE, хороший - VERYWELL)");
                    return true;
                }
                TypeRarities typeRarities = TypeRarities.valueOf(args[2].toUpperCase());
                ArchaeologistPlugin.addItem(String.valueOf(UUID.randomUUID()), player.getItemInHand(), Integer.valueOf(args[1]), String.valueOf(typeRarities));
                return true;
            }
        }
        return true;
    }
}
