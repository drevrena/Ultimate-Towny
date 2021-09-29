package it.flowzz.ultimatetowny.enums;

import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.lang.Messages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UpgradeType {

    MEMBERS(UltimateTownyPlugin.getInstance().getConfig().getInt("town.upgrades.members.max-level"), Messages.MEMBERS_UPGRADE_TRANSLATION.getTranslation()),
    EXPERIENCE(UltimateTownyPlugin.getInstance().getConfig().getInt("town.upgrades.experience.max-level"), Messages.EXPERIENCE_UPGRADE_TRANSLATION.getTranslation()),
    MONEY_GEN(UltimateTownyPlugin.getInstance().getConfig().getInt("town.upgrades.money-gen.max-level"), Messages.MONEY_GEN_UPGRADE_TRANSLATION.getTranslation()),
    COIN_GEN(UltimateTownyPlugin.getInstance().getConfig().getInt("town.upgrades.coin-gen.max-level"), Messages.COIN_GEN_UPGRADE_TRANSLATION.getTranslation());

    private final int maxLevel;
    private final String translation;
}
