package it.flowzz.ultimatetowny.listeners;

import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class JoinListener implements Listener {

    private final UltimateTownyPlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
        if (townyPlayer == null){
            plugin.getTownHandler().getPlayers().put(player.getUniqueId(), new TownyPlayer(player));
        }
    }
}
