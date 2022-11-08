package fi.clim8.clim8server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fi.clim8.clim8server.data.HadCRUTData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseService {

    private static final HikariConfig config = new HikariConfig();
    private HikariDataSource ds;

    private static final DatabaseService sql = new DatabaseService();

    public static DatabaseService getInstance() {
        return sql;
    }


    public void init() {
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:./localdata.db");
        config.setConnectionTestQuery("SELECT 1;");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");
        config.setMinimumIdle(2);
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(10000);
        ds = new HikariDataSource(config);

        try(Connection connection = ds.getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement("""
                CREATE TABLE [hadcurtdata] (
                  [year] INT NOT NULL,
                  [month] INT NOT NULL,
                  [summaryseries] CHAR(16) NOT NULL,
                  [degc] DOUBLE NOT NULL,
                  CONSTRAINT [PK_hadcurt_0] PRIMARY KEY ([year], [month], [summaryseries]),
                  CONSTRAINT [UK_hadcurt_0] UNIQUE ([year], [month], [summaryseries])
                );""")) {
                ps.execute();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }

    }

    public void refreshDataFromHadCRUT(List<HadCRUTData> data) {
        try(Connection connection = ds.getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement("INSERT INTO hadcurtdata (year, month, summaryseries, degc) VALUES (?,?,?,?) ON CONFLICT (year, month, summaryseries) DO UPDATE SET degc=?")) {
                data.forEach(hadCRUTData -> {
                    try {
                        ps.setInt(1, hadCRUTData.getYear());
                        ps.setInt(2, hadCRUTData.getMonth());
                        ps.setString(3, hadCRUTData.getSummarySeries().getSummarySeries());
                        ps.setDouble(4, hadCRUTData.getData());
                        ps.setDouble(5, hadCRUTData.getData());
                        ps.addBatch();
                    } catch(Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                });

                ps.executeLargeBatch();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
    }
}
