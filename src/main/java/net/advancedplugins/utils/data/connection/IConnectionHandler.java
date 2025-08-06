package net.advancedplugins.utils.data.connection;

import com.j256.ormlite.support.BaseConnectionSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.sql.SQLException;

public interface IConnectionHandler {
    ConnectionType getConnectionType();
    void retrieveCredentials(ConfigurationSection credentialsSection);
    BaseConnectionSource connect() throws IOException, SQLException;
    void close();
}
