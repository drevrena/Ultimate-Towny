package it.flowzz.ultimatetowny.enums;

import it.flowzz.ultimatetowny.lang.Messages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    MEMBER(Messages.MEMBER_TRANSLATION.getTranslation()),
    MODERATOR(Messages.MODERATOR_TRANSLATION.getTranslation()),
    LEADER(Messages.LEADER_TRANSLATION.getTranslation());

    private final String translation;

    public boolean isAtLeast(Role role) {
        if (role == MEMBER)
            return true;
        if (role == MODERATOR)
            return this == MODERATOR || this == LEADER;
        return this == LEADER;
    }
    public boolean isGreaterThan(Role role) {
        if (role == MEMBER)
            return this == MODERATOR || this == LEADER;
        if (role == MODERATOR)
            return this == LEADER;
        return false;
    }

    public boolean isLessThan(Role role) {
        if (role == LEADER)
            return this == MEMBER || this == MODERATOR;
        if (role == MODERATOR)
            return this == MEMBER;
        return false;
    }
}
