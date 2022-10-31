package fi.clim8.clim8server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
            try(PreparedStatement ps = ds.getConnection().prepareStatement("""
                CREATE TABLE [hadcurtdata] (
                  [year] INT NOT NULL,
                  [month] INT NOT NULL,
                  [summaryseries] CHAR(16) NOT NULL,
                  [degc] DOUBLE NOT NULL,
                  [lowconfidencelimit] DOUBLE NOT NULL,
                  [upperconfidencelimit] DOUBLE NOT NULL,
                  CONSTRAINT [PK_hadcurt_0] PRIMARY KEY ([year], [month], [summaryseries]),
                  CONSTRAINT [UK_hadcurt_0] UNIQUE ([year], [month], [summaryseries])
                );""")) {
                ps.execute();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }

    }

    public void refreshDataFromHadCURT(Map<Integer, HashMap<Integer, Double[]>> data, String summarySeries) {
        try(Connection connection = ds.getConnection()) {
            try(PreparedStatement ps = ds.getConnection().prepareStatement("INSERT INTO hadcurtdata (year, month, summaryseries, degc, lowconfidencelimit, upperconfidencelimit) VALUES (?,?,?,?,?,?) ON CONFLICT (year, month, summaryseries) DO UPDATE SET degc=?,lowconfidencelimit=?,upperconfidencelimit=?")) {
                data.forEach((year, data2) -> data2.forEach((month, values) -> {
                    try {
                        ps.setInt(1, year);
                        ps.setInt(2, month);
                        ps.setString(3, summarySeries);
                        ps.setDouble(4, values[0]);
                        ps.setDouble(5, values[1]);
                        ps.setDouble(6, values[2]);
                        ps.setDouble(7, values[0]);
                        ps.setDouble(8, values[1]);
                        ps.setDouble(9, values[2]);
                        ps.addBatch();
                    } catch(Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                }));
                ps.executeLargeBatch();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
    }
}
