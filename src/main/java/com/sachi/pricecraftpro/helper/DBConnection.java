package com.sachi.pricecraftpro.helper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static final String DB = "Database.db";

    public final Connection CONN() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + DB);
            Statement stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            Logger.getLogger(DBConnection.class.getName())
                    .log(Level.SEVERE, null, e);
        }
        return conn;
    }

    public static void checkDb() {
        Path path = Paths.get(DB);
        if (Files.notExists(path)) {
            String[] query = {
                // turn off foreign keys
                "PRAGMA foreign_keys = OFF",
                //
                // table login
                "CREATE TABLE login("
                + "id INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "username TEXT NOT NULL,"
                + "password TEXT NOT NULL,"
                + "email TEXT NOT NULL,"
                + "key TEXT NOT NULL,"
                + "type INTEGER NOT NULL," // 1 = QS, 2 = Seller
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table material
                "CREATE TABLE material("
                + "id INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "price REAL NOT NULL,"
                + "category INTEGER NOT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (category) REFERENCES category(id) "
                + "ON DELETE CASCADE"
                + ");",
                //
                // table category
                "CREATE TABLE category("
                + "id INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table customer
                "CREATE TABLE customer("
                + "id INTEGER NOT NULL,"
                + "qs INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "email TEXT NOT NULL,"
                + "count INTEGER NOT NULL"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (qs) REFERENCES login(id) ON DELETE CASCADE"
                + ");",
                //
                // table cart
                "CREATE TABLE cart("
                + "id INTEGER NOT NULL,"
                + "customer INTEGER NOT NULL,"
                + "material INTEGER NOT NULL,"
                + "units INTEGER NOT NULL,"
                + "plan TEXT NOT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (customer) REFERENCES customer(id) "
                + "ON DELETE CASCADE,"
                + "FOREIGN KEY (material) REFERENCES material(id) "
                + "ON DELETE CASCADE"
                + ");",
                //
                // turn on foreign keys
                "PRAGMA foreign_keys = ON"
            };

            Connection conn = new DBConnection().CONN();
            for (String queryX : query) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(queryX);
                } catch (SQLException ex) {
                    Logger.getLogger(DBConnection.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

}
