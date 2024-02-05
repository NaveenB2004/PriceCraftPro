package com.naveenb2004.pricecraftpro.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NaveenB2004
 */
public class DBConnection {

    private static final String DB = "Database.db";

    public static final Connection CONN() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:" + DB);
            Statement stmt = con.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            Logger.getLogger(DBConnection.class.getName())
                    .log(Level.SEVERE, null, e);
        }
        return con;
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
                + "lastLogin TEXT NOT NULL,"
                + "email TEXT NOT NULL,"
                + "key TEXT NOT NULL,"
                + "type INTEGER NOT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (type) REFERENCES type(id) ON DELETE CASCADE"
                + ");",
                //
                // table type
                "CREATE TABLE type("
                + "id INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table material
                "CREATE TABLE material("
                + "id INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "price REAL NOT NULL,"
                + "category INTEGER NOT NULL,"
                + "parent INTEGER NOT NULL,"
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
                + "plans INTEGER NOT NULL,"
                + "name TEXT NOT NULL,"
                + "email TEXT,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (qs) REFERENCES login(id) ON DELETE CASCADE"
                + ");",
                //
                // table cart
                "CREATE TABLE cart("
                + "id INTEGER NOT NULL,"
                + "customer INTEGER NOT NULL,"
                + "material INTEGER NOT NULL,"
                + "plan INTEGER NOT NULL,"
                + "count INTEGER NOT NULL,"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (customer) REFERENCES customer(id) "
                + "ON DELETE CASCADE,"
                + "FOREIGN KEY (material) REFERENCES material(id) "
                + "ON DELETE CASCADE"
                + ");",
                //
                // add account types
                "INSERT INTO type VALUES "
                + "(1, 'seller', 'Manage materials'),"
                + "(2, 'qs', 'Make estimates');",
                //
                // add accounts
                "INSERT INTO login VALUES "
                + "(1, 'Seller X', 'seller', 'seller', '0', 'seller@example.com',"
                + " 'password', 1),"
                + "(2, 'QS X', 'qs', 'qs', '0', 'qs@example.com', 'password', 2);",
                //
                // turn on foreign keys
                "PRAGMA foreign_keys = ON"
            };

            for (String queryX : query) {
                try (Statement stmt = CONN().createStatement()) {
                    stmt.execute(queryX);
                } catch (SQLException ex) {
                    Logger.getLogger(DBConnection.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
