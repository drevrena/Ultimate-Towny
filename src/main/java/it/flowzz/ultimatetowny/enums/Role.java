package it.flowzz.ultimatetowny.enums;

import it.flowzz.ultimatetowny.lang.Messages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    MEMBER(Messages.MEMBER_TRANSLATION.getTranslation()),
    MODERATOR(Messages.MODERATOR_TRANSLATION.getTranslation()),
    COLEADER(Messages.COLEADER_TRANSLATION.getTranslation()),
    LEADER(Messages.LEADER_TRANSLATION.getTranslation());

    private final String translation;

    public boolean isAtLeast(Role role) {
        switch (role) {
            case MEMBER -> {
                return true;
            }
            case MODERATOR -> {
                return this == MODERATOR || this == COLEADER || this == LEADER;
            }
            case COLEADER -> {
                return this == COLEADER || this == LEADER;
            }
            default -> {
                return this == LEADER;
            }
        }
    }

    public boolean isGreaterThan(Role role) {
        switch (role) {
            case MEMBER -> {
                return this == MODERATOR || this == COLEADER || this == LEADER;
            }
            case MODERATOR -> {
                return this == COLEADER || this == LEADER;
            }
            case COLEADER -> {
                return this == LEADER;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isLessThan(Role role) {
        if (role == LEADER)
            return this == MEMBER || this == MODERATOR || this == COLEADER;
        if (role == COLEADER)
            return this == MEMBER || this == MODERATOR;
        if (role == MODERATOR)
            return this == MEMBER;
        return false;
    }
}
