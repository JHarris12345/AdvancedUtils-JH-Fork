package net.advancedplugins.utils.data.connection;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.jdbc.db.MysqlDatabaseType;
import com.j256.ormlite.jdbc.db.PostgresDatabaseType;
import com.j256.ormlite.support.BaseConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.advancedplugins.utils.trycatch.TryCatchUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.sql.SQLException;

public class PostgreSQLConnectionHandler implements IConnectionHandler{

    private String host;
    private short port;
    private String username;
    private String password;
    private String database;
    private HikariDataSource dataSource;

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.POSTGRESQL;
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
    public BaseConnectionSource connect() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://"+this.host+":"+this.port+"/"+this.database);
        config.setUsername(this.username);
        config.setPassword(this.password);

        TryCatchUtil.tryAndReturn(() -> Class.forName("org.postgresql.Driver"));
        this.dataSource = new HikariDataSource(HikariHandler.configure(config));

        return new DataSourceConnectionSource(this.dataSource, this.dataSource.getJdbcUrl());
    }

    @Override
    public void close() {
        if(dataSource != null) this.dataSource.close();
    }
}
