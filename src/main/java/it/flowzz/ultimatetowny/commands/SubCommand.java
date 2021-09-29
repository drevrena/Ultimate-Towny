package it.flowzz.ultimatetowny.commands;

import com.google.common.collect.Lists;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class SubCommand {

    protected final UltimateTownyPlugin plugin;

    private final String name;
    private final String commandSyntax;
    private String permission;
    private List<String> aliases;

    protected SubCommand(String name, String commandSyntax) {
        this.plugin = UltimateTownyPlugin.getInstance();
        this.name = name;
        this.commandSyntax = commandSyntax;
        this.aliases = Lists.newArrayList();
    }

    public void broadcast(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    public void broadcast(String message, Collection<TownyPlayer> players) {
        players.stream()
                .map(player -> Bukkit.getPlayer(player.getUuid()))
                .filter(Objects::nonNull)
                .forEach(toSend -> toSend.sendMessage(message));
    }

    public abstract void execute(CommandSender sender, String label, String[] args);

    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    public abstract int getMinArguments();

}