package it.flowzz.ultimatetowny.tasks;

import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayTimeTask implements Runnable {

    private final UltimateTownyPlugin plugin;

    @Override
    public void run() {
        plugin.getTownHandler().getPlayers().values().stream()
                .filter(townyPlayer -> townyPlayer.getTownId() != null && townyPlayer.isOnline())
                .forEach(townyPlayer -> townyPlayer.setPlaytime(townyPlayer.getPlaytime() + 1));
    }
}
