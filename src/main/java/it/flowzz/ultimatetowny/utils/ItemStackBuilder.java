package it.flowzz.ultimatetowny.utils;

import com.google.common.collect.Lists;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.Placeholder;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ItemStackBuilder {

    private Material material;
    private String name;
    private List<String> lore;

    public static ItemStack fromConfig(String node, Placeholder... placeholders) {
        ConfigurationSection configurationSection = UltimateTownyPlugin.getInstance().getConfig().getConfigurationSection(node);
        List<String> newLore = Lists.newArrayList();
        List<String> oldLore = configurationSection.getStringList("lore");
        String name = configurationSection.getString("name");

        for (String line : oldLore) {
            for (Placeholder placeholder : placeholders) {
                line = line.replace(placeholder.getText(), placeholder.getValue());
            }
            newLore.add(line);
        }

        for (Placeholder placeholder : placeholders) {
            name = name.replace(placeholder.getText(), placeholder.getValue());
        }

        return builder()
                .material(Material.getMaterial(configurationSection.getString("id")))
                .name(name)
                .lore(newLore.isEmpty() ? oldLore : newLore)
                .build()
                .toItem();
    }

    public ItemStack toItem() {
        ItemStack result = new ItemStack(material);
        ItemMeta resultMeta = result.getItemMeta();
        resultMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', name)));
        List<Component> newLore = Lists.newArrayList();
        if (lore != null && !lore.isEmpty()) {
            List<Component> list = new ArrayList<>();
            for (String line : lore) {
                list.add(Component.text(ChatColor.translateAlternateColorCodes('&', line)));
            }
            newLore = list;
        }
        resultMeta.lore(newLore);
        result.setItemMeta(resultMeta);
        return result;
    }
}
