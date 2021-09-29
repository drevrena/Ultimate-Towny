package it.flowzz.ultimatetowny.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSerializer {

    public static String serialize(Location location) {
        return location == null ? null : location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static Location deserialize(String locationString) {
        if (locationString == null) return null;
        String[] splitted = locationString.split(";");
        return new Location(Bukkit.getWorld(splitted[0]), Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
    }
}