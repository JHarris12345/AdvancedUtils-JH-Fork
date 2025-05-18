package net.advancedplugins.utils.data.connection;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.jdbc.db.MysqlDatabaseType;
import com.j256.ormlite.jdbc.db.PostgresDatabaseType;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.sql.SQLException;

public class PostgreSQLConnectionHandler implements IConnectionHandler{

    private String host;
    private short port;
    private String username;
    private String password;
    private String database;

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.MYSQL;
    }

    @Override
    public void retrieveCredentials(ConfigurationSection section) {
        if(section.contains("host")) {
            this.host = section.getString("host");
            this.port = (short) section.getInt("port", 5432);
        } else {
            String[] socket = section.getString("address", "").split(":", 2);
            this.host = socket[0];

            if(socket.length == 2) this.port = Short.parseShort(socket[1]);
            else this.port = 5432;
        }

        this.username = section.getString("username");
        this.password = section.getString("password");
        this.database = section.getString("database");
    }

    @Override
    public JdbcPooledConnectionSource connect() throws IOException, SQLException {
        String url = "jdbc:postgresql://"+this.host+":"+this.port+"/"+this.database;
        return new JdbcPooledConnectionSource(url,username,password,new PostgresDatabaseType());
    }
}
