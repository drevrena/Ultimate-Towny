package it.flowzz.ultimatetowny.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Rate {

    private UUID player;
    private int rating;

    public Rate(ResultSet ratingResult) throws SQLException {
        this(UUID.fromString(ratingResult.getString("player")), ratingResult.getInt("rate"));
    }
}
