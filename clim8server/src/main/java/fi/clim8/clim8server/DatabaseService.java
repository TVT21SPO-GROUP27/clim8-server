package fi.clim8.clim8server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fi.clim8.clim8server.data.AbstractData;
import fi.clim8.clim8server.data.EHadCRUTSummarySeries;
import fi.clim8.clim8server.data.HadCRUTData;
import fi.clim8.clim8server.data.IceCoreData;
import fi.clim8.clim8server.data.MaunaLoaData;
import fi.clim8.clim8server.data.VostokData;
import fi.clim8.clim8server.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        config.addDataSourceProperty("prepStmtCacheSize", "512");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");
        config.setMinimumIdle(2);
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(10000);
        ds = new HikariDataSource(config);

        try (Connection connection = ds.getConnection()) {
            // Makes database fetching faster, but increases potential corruptions...
            // Pffh...
            try (PreparedStatement ps = connection.prepareStatement("""
                    PRAGMA synchronous = off;
                    """)) {
                ps.execute();
            }
            try (PreparedStatement ps = connection.prepareStatement("""
                    CREATE TABLE [hadcrutdata] (
                      [year] INT NOT NULL,
                      [month] INT NOT NULL,
                      [summaryseries] CHAR(16) NOT NULL,
                      [degc] DOUBLE NOT NULL,
                      CONSTRAINT [PK_hadcurt_0] PRIMARY KEY ([year], [month], [summaryseries]),
                      CONSTRAINT [UK_hadcurt_0] UNIQUE ([year], [month], [summaryseries])
                    );""")) {
                ps.execute();
            }
            try (PreparedStatement ps = connection.prepareStatement("""
                    CREATE TABLE [mobergdata] (
                      [year] INT NOT NULL,
                      [data] DOUBLE NOT NULL,
                      CONSTRAINT [PK_moberg_0] PRIMARY KEY ([year]),
                      CONSTRAINT [UK_moberg_0] UNIQUE ([year])
                    );""")) {
                ps.execute();
            }
            try (PreparedStatement ps = connection.prepareStatement("""
                    CREATE TABLE [users] (
                      [id] INTEGER PRIMARY KEY AUTOINCREMENT,
                      [username] TEXT NOT NULL,
                      [email] TEXT NOT NULL,
                      [password] TEXT NOT NULL
                    );""")) {
                ps.execute();
            }
            try (PreparedStatement ps = connection.prepareStatement("""
                    CREATE TABLE [maunaloadata] (
                      [year] INT NOT NULL,
                      [month] INT NOT NULL,
                      [co2] DOUBLE NOT NULL,
                      CONSTRAINT [PK_maunaloa_0] PRIMARY KEY ([year], [month]),
                      CONSTRAINT [UK_maunaloa_0] UNIQUE ([year], [month])
                    );""")) {
                ps.execute();
            }
            try (PreparedStatement ps = connection.prepareStatement("""
                    CREATE TABLE [icecoredata] (
                      [year] INTE NOT NULL,
                      [series] CHAR(15) NOT NULL,
                      [data] DOUBLE NOT NULL,
                      CONSTRAINT [PK_icecore_0] PRIMARY KEY ([year], [series]),
                      CONSTRAINT [UK_icecore_0] UNIQUE ([year], [series])
                    );""")) {
                ps.execute();
            }
            try (PreparedStatement ps = connection.prepareStatement("""
                    CREATE TABLE [vostokcoredata] (
                      [year] INT NOT NULL,
                      [data] DOUBLE NOT NULL,
                      CONSTRAINT [PK_vostok_0] PRIMARY KEY ([year]),
                      CONSTRAINT [UK_vostok_0] UNIQUE ([year])
                    );""")) {
                ps.execute();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }

    }

    public void refreshDataFromHadCRUT(List<HadCRUTData> data) {
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO hadcrutdata (year, month, summaryseries, degc) VALUES (?,?,?,?) ON CONFLICT (year, month, summaryseries) DO UPDATE SET degc=?")) {
                data.forEach(hadCRUTData -> {
                    try {
                        ps.setInt(1, hadCRUTData.getYear());
                        ps.setInt(2, hadCRUTData.getMonth());
                        ps.setString(3, hadCRUTData.getSummarySeries().getSummarySeries());
                        ps.setDouble(4, hadCRUTData.getData());
                        ps.setDouble(5, hadCRUTData.getData());
                        ps.addBatch();
                    } catch (Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                });
                ps.executeLargeBatch();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
    }

    public List<HadCRUTData> fecthHadCRUTData() {
        List<HadCRUTData> data = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM hadcrutdata")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final HadCRUTData temp = new HadCRUTData(rs.getInt(1), rs.getInt(2),
                            EHadCRUTSummarySeries.find(rs.getString(3)));
                    temp.setData(rs.getDouble(4));
                    data.add(temp);
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return data;
    }

    public void refreshDataFromMoberg2005(List<AbstractData> data) {
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO mobergdata (year, data) VALUES (?,?) ON CONFLICT (year) DO UPDATE SET data=?")) {
                data.forEach(mobergData -> {
                    try {
                        ps.setInt(1, mobergData.getYear());
                        ps.setDouble(2, mobergData.getData());
                        ps.setDouble(3, mobergData.getData());
                        ps.addBatch();
                    } catch (Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                });
                ps.executeLargeBatch();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
    }

        public void refreshDataFromMaunaLoa(List<MaunaLoaData> data) {
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO maunaloadata (year, month, co2) VALUES (?,?,?) ON CONFLICT (year, month) DO UPDATE SET co2=?")) {
                data.forEach(maunaLoaData -> {
                    try {
                        ps.setInt(1, maunaLoaData.getYear());
                        ps.setInt(2, maunaLoaData.getMonth());
                        ps.setDouble(3, maunaLoaData.getData());
                        ps.addBatch();
                    } catch (Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                });
                ps.executeLargeBatch();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
    }
    public void refreshDataFromIceCore(List<IceCoreData> data) {
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO icecoredata (year, series, data) VALUES (?,?,?) ON CONFLICT (year, series) DO UPDATE SET data=?")) {
                data.forEach(iceCoreData -> {
                    try {
                        ps.setInt(1, iceCoreData.getYear());
                        ps.setString(2, iceCoreData.getSeries());
                        ps.setDouble(3, iceCoreData.getData());
                        ps.setDouble(4, iceCoreData.getData());
                        ps.addBatch();

                    } catch (Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                });
                ps.executeLargeBatch();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
    }

    public void refreshDataFromVostokCore(List<VostokData> data) {
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO vostokcoredata (year, data) VALUES (?,?) ON CONFLICT (year) DO UPDATE SET data=?")) {
                data.forEach(vostokData -> {
                    try {
                        ps.setInt(1, vostokData.getYear());
                        ps.setDouble(2, vostokData.getData());
                        ps.setDouble(3, vostokData.getData());
                        ps.addBatch();
                    } catch (Exception e) {
                        Logger.getGlobal().info(e.getMessage());
                    }
                });
                ps.executeLargeBatch();
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
    }

    public List<AbstractData> fetchMoberg2005Data() {
        List<AbstractData> data = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM mobergdata")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final AbstractData temp = new AbstractData(rs.getInt(1));
                    temp.setData(rs.getDouble(2));
                    data.add(temp);
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return data;
    }

        public List<MaunaLoaData> fetchMaunaLoaData() {
        List<MaunaLoaData> data = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM maunaloadata")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final MaunaLoaData temp = new MaunaLoaData(rs.getInt(1), rs.getInt(2));
                    temp.setData(rs.getDouble(3));
                    data.add(temp);
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return data;
    }
    public List<IceCoreData> fetchIceCoreData() {
        List<IceCoreData> data = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM icecoredata")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final IceCoreData temp = new IceCoreData(rs.getInt(1));
                    temp.setSeries(rs.getString(2));
                    temp.setData(rs.getDouble(3));
                    data.add(temp);
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return data;
    }

    public List<VostokData> fetchVostokCoreData() {
        List<VostokData> data = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM vostokcoredata")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final VostokData temp = new VostokData(rs.getInt(1));
                    temp.setData(rs.getDouble(2));
                    data.add(temp);
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return data;
    }


    public List<User> fetchAllUsers() {
        List<User> user = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM users")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final User results = new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"),
                            rs.getString("password"));
                    user.add(results);
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return user;
    }

    public void addNewUser(User user) {
        try (Connection connection = ds.getConnection()) {
            Logger.getGlobal().info(user.getName());
            try (PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO [users] ([username], [email], [password]) VALUES (?,?,?)")) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());

                System.out.println(user.getName());
                System.out.println(user.getEmail());
                System.out.println(user.getPassword());

                if (checkUserNameExistance(user) == true) {
                    System.out.println("Käyttäjänimi on käytössä.");
                } if (checkEmailExistance(user) == true){
                    System.out.println("Sähköposti on jo käytössä.");
                } else {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
    }

    public boolean login(User user) {
        boolean exists;
        try (Connection connection = ds.getConnection()) {
            Logger.getGlobal().info(user.getName());
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
                ps.setString(1, user.getName());
                ps.setString(2, user.getPassword());
                ResultSet rs = ps.executeQuery();

                System.out.println(user.getName());
                System.out.println(user.getPassword());

                if (rs.next()) {
                    exists = true;
                    System.out.println("Logged in!");
                } else {
                    exists = false;
                    System.out.println("Wrong username/password...");
                }
                return exists;
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
            System.out.println("An unexpected error occurred...");
            return false;
        }

    }

    public List<User> getUserById(User user) {
        List<User> userResults = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                ps.setLong(1, user.getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final User results = new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"),
                            rs.getString("password"));
                    userResults.add(results);
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return userResults;
    }

    public List<User> getUserByName(User user) {
        List<User> userResults = new ArrayList<>();
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
                ps.setString(1, user.getName());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final User results = new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"),
                            rs.getString("password"));
                    userResults.add(results);
                }
            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return userResults;
    }

    public void deleteUser(User user) {
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
                ps.setLong(1, user.getId());
                ps.executeUpdate();

            }
        } catch (SQLException e) {
            Logger.getGlobal().info(e.getMessage());
        }

    }

    public boolean checkUserNameExistance(User user) throws SQLException {
        boolean exists;
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
                ps.setString(1, user.getName());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    exists = true;
                } else {
                    exists = false;
                }
            }
            return exists;

        }

    }

    public boolean checkEmailExistance(User user) throws SQLException {
        boolean exists;
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
                ps.setString(1, user.getEmail());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    exists = true;
                } else {
                    exists = false;
                }
            }
            return exists;

        }

    }

}