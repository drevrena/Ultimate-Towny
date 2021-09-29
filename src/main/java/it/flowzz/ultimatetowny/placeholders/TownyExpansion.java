package it.flowzz.ultimatetowny.placeholders;

import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class TownyExpansion extends PlaceholderExpansion {

    private final UltimateTownyPlugin plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "ultimatetowny";
    }

    @Override
    public @NotNull String getAuthor() {
        return "_FlowZz_";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }
        switch (identifier.toLowerCase()) {
            case "town":
                TownyPlayer town = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
                return town.getTownId() == null ? Messages.TOWNLESS_PLACEHOLDER.getTranslation() : town.getTownId();
            case "role":
                return plugin.getTownHandler().getPlayers().get(player.getUniqueId()).getRole().getTranslation();
            case "playtime":
                return String.format("%d", plugin.getTownHandler().getPlayers().get(player.getUniqueId()).getPlaytime());
            case "town_money":
                return String.format("%d", Math.round(plugin.getTownHandler().getTowns().get(plugin.getTownHandler().getPlayers().get(player.getUniqueId()).getTownId()).getMoney()));
            case "town_coins":
                return String.format("%d", Math.round(plugin.getTownHandler().getTowns().get(plugin.getTownHandler().getPlayers().get(player.getUniqueId()).getTownId()).getCoins()));
            case "town_total":
                return String.format("%d", Math.round(plugin.getTownHandler().getTowns().get(plugin.getTownHandler().getPlayers().get(player.getUniqueId()).getTownId()).getTotalValue()));
            default:
                return null;
        }
    }
}
