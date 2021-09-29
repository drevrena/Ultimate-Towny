package it.flowzz.ultimatetowny.database;

import it.flowzz.ultimatetowny.database.credentials.Credentials;

public interface IDatabase {

    /**
     * Connect to database using the given credentials.
     *
     * @param credentials the credentials.
     */
    void connect(Credentials credentials);

    /**
     * Disconnect from Database
     */
    void disconnect();

    void load();

    void save();

    void deleteTown(String name);
}
