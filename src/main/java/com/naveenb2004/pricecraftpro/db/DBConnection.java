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
                + "id INT(3) NOT NULL AUTOINCREMENT,"
                + "name VARCHAR(50) NOT NULL,"
                + "username VARCHAR(16) NOT NULL,"
                + "password VARCHAR(16) NOT NULL,"
                + "lastLogin VARCHAR(50) NOT NULL,"
                + "email VARCHAR(100) NOT NULL,"
                + "key VARCHAR(20) NOT NULL,"
                + "type INT(2) NOT NULL,"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table type
                "CREATE TABLE type("
                + "id INT(2) NOT NULL AUTOINCREMENT,"
                + "name VARCHAR(10) NOT NULL,"
                + "description VARCHAR(50),"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table material
                "CREATE TABLE material("
                + "id INT(5) NOT NULL AUTOINCREMENT,"
                + "name VARCHAR(50) NOT NULL,"
                + "price DECIMAL(8, 2) NOT NULL,"
                + "category INT(3) NOT NULL,"
                + "parent INT(3) NOT NULL,"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table category
                "CREATE TABLE category("
                + "id INT(3) NOT NULL AUTOINCREMENT,"
                + "name VARCHAR(30) NOT NULL,"
                + "description VARCHAR(50),"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table customer
                "CREATE TABLE customer("
                + "id INT(6) NOT NULL AUTOINCREMENT,"
                + "qs INT(3) NOT NULL,"
                + "plans INT(2) NOT NULL,"
                + "name VARCHAR(50) NOT NULL,"
                + "email VARCHAR(50),"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table cart
                "CREATE TABLE cart("
                + "id INT(6) NOT NULL AUTOINCREMENT,"
                + "customer INT(6) NOT NULL,"
                + "material INT(5) NOT NULL,"
                + "plan INT(2) NOT NULL,"
                + "count INT(3) NOT NULL,"
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
