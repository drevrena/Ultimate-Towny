package it.flowzz.ultimatetowny.models;

import it.flowzz.ultimatetowny.enums.UpgradeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@AllArgsConstructor
public class Upgrade {

    private UpgradeType type;
    @Setter
    private int level;

    public Upgrade(ResultSet upgradesResult) throws SQLException {
        this(UpgradeType.valueOf(upgradesResult.getString("upgrade")), upgradesResult.getInt("level"));
    }
}
