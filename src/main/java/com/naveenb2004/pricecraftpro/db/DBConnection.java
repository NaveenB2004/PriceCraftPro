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

    public static Connection con() {
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
                + "PRIMARY KEY (id)"
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
                + "PRIMARY KEY (id)"
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
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table cart
                "CREATE TABLE cart("
                + "id INTEGER NOT NULL,"
                + "customer INTEGER NOT NULL,"
                + "material INTEGER NOT NULL,"
                + "plan INTEGER NOT NULL,"
                + "count INTEGER NOT NULL,"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // foreign key type -> login
                "ALTER TABLE login "
                + "ADD FOREIGN KEY (type) REFERENCES type(id);",
                //
                // foreign key category -> material
                "ALTER TABLE material "
                + "ADD FOREIGN KEY (category) REFERENCES category(id);",
                //
                // foreign key login -> customer
                "ALTER TABLE customer "
                + "ADD FOREIGN KEY (qs) REFERENCES login(id);",
                //
                // foreign key customer -> cart, material -> cart
                "ALTER TABLE cart "
                + "ADD FOREIGN KEY (customer) REFERENCES customer(id),"
                + "ADD FOREIGN KEY (material) REFERENCES material(id);",
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
                + "(2, 'QS X', 'qs', 'qs', '0', 'qs@example.com', 'password', 2);"
            };

            for (int i = 0; i < query.length; i++) {
                try (Statement stmt = con().createStatement()) {
                    stmt.executeQuery(query[i]);
                } catch (SQLException ex) {
                    Logger.getLogger(DBConnection.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
