package it.flowzz.ultimatetowny.lang;

import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public enum Messages {

    ALREADY_IN_TOWN,
    ALREADY_MEMBER,
    BLACKLISTED_TOWN_NAME,
    CANNOT_DEMOTE_LEADER,
    CANNOT_KICK_HIGHER_ROLE,
    CANNOT_KICK_YOURSELF,
    CANNOT_PROMOTE,
    COINS_DEPOSITED,
    COINS_GENERATED,
    COIN_GEN_UPGRADE_TRANSLATION,
    COINS_WITHDRAW,
    COMMAND_HELP,
    COMMAND_HELP_FOOTER,
    COMMAND_HELP_HEADER,
    COMMAND_NOT_FOUND,
    COMMAND_ONLY_PLAYER,
    EXPERIENCE_UPGRADE_TRANSLATION,
    INVALID_AMOUNT,
    INVALID_TOWN_NAME,
    INVITE_RECEIVED,
    LEADER_CANNOT_LEAVE,
    LEADER_TRANSLATION,
    MEMBERS_UPGRADE_TRANSLATION,
    MEMBER_TRANSLATION,
    MODERATOR_TRANSLATION,
    MONEY_DEPOSITED,
    MONEY_GENERATED,
    MONEY_GEN_UPGRADE_TRANSLATION,
    MONEY_WITHDRAW,
    NOT_ENOUGH_COINS,
    NOT_ENOUGH_MONEY,
    NOT_ENOUGH_TOWN_COINS,
    NOT_ENOUGH_TOWN_MONEY,
    NOT_IN_TOWN,
    NOT_LEADER,
    NOT_MODERATOR,
    NO_PERMISSION,
    OFFLINE_COLOR,
    ONLINE_COLOR,
    PLAYER_ALREADY_IN_TOWN,
    PLAYER_DEMOTED,
    PLAYER_GOT_KICKED,
    PLAYER_INVITED,
    PLAYER_JOINED,
    PLAYER_KICKED,
    PLAYER_LEFT,
    PLAYER_NOT_FOUND,
    PLAYER_NOT_IN_TOWN,
    PLAYER_PROMOTED,
    RECRUIT_COOLDOWN,
    RECRUIT_MESSAGE,
    TOWN_ALREADY_EXIST,
    TOWN_CREATED,
    TOWN_DISBANDED,
    TOWN_INVITE_EXPIRED,
    TOWN_LEFT,
    TOWN_MAX_PLAYERS,
    TOWN_NOT_FOUND,
    TOWN_NOT_INVITED,
    TOWN_PLAYER_HOVER,
    TOWN_RENAMED,
    TOWN_SHOW,
    TOWN_UPGRADE,
    TOWNLESS_PLACEHOLDER,
    UPGRADE_MAX_LEVEL,
    WARPED_TO_TOWN,
    WARP_ALREADY_SET,
    WARP_NOT_SET,
    WARP_REMOVED,
    WARP_SET,
    WRONG_SYNTAX;

    public String getTranslation(){
        return translateHexColorCodes(ChatColor.translateAlternateColorCodes('&', UltimateTownyPlugin.getInstance().getLang().getString(name().toLowerCase().replace("_", "-"), "&7Message not found.")));
    }

    public final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
    public final char colorChar = ChatColor.COLOR_CHAR;

    public String translateHexColorCodes(String message) {
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }


}