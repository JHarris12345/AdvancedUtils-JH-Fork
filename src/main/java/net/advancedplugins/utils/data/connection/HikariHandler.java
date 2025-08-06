package net.advancedplugins.utils.data.connection;

import com.zaxxer.hikari.HikariConfig;

public class HikariHandler {
	public static HikariConfig configure(HikariConfig config) {
		config.setPoolName("hikari");
		config.setMaximumPoolSize(10);
		config.setMinimumIdle(10);
		config.setMaxLifetime(1800000L);
		config.setConnectionTimeout(5000);
		config.addDataSourceProperty("characterEncoding", "utf8");
		return config;
	}
}
