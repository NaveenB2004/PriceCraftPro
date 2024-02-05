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
            String[] qury = {
                // table login
                "CREATE TABLE login("
                + "id INT(3) NOT NULL,"
                + "name VARCHAR(50) NOT NULL,"
                + "username VARCHAR(16) NOT NULL,"
                + "password VARCHAR(16) NOT NULL,"
                + "lastLogin VARCHAR(50) NOT NULL,"
                + "email VARCHAR(50) NOT NULL,"
                + "key VARCHAR(20) NOT NULL,"
                + "type INT(2) NOT NULL,"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table type
                "CREATE TABLE type("
                + "id INT(3) NOT NULL,"
                + "name VARCHAR(20) NOT NULL,"
                + "description VARCHAR(50),"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table material
                "CREATE TABLE material("
                + "id INT(5) NOT NULL,"
                + "name VARCHAR(50) NOT NULL,"
                + "price DECIMAL(8, 2) NOT NULL,"
                + "category INT(3) NOT NULL,"
                + "parent INT(3) NOT NULL,"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table category
                "CREATE TABLE category("
                + "id INT(3) NOT NULL,"
                + "name VARCHAR(30) NOT NULL,"
                + "description VARCHAR(50),"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table parent
                "CREATE TABLE parent("
                + "id INT(3) NOT NULL,"
                + "name VARCHAR(30) NOT NULL,"
                + "description VARCHAR(50),"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table customer
                "CREATE TABLE customer("
                + "id INT(6) NOT NULL,"
                + "qs INT(3) NOT NULL,"
                + "plans INT(2) NOT NULL,"
                + "name VARCHAR(50) NOT NULL,"
                + "email VARCHAR(50),"
                + "PRIMARY KEY (id)"
                + ");",
                //
                // table cart
                "CREATE TABLE cart("
                + "id INT(6) NOT NULL,"
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
                // foreign key category -> material, parent -> material
                "ALTER TABLE material "
                + "ADD FOREIGN KEY (category) REFERENCES category(id),"
                + "ADD FOREIGN KEY (parent) REFERENCES parent(id);",
                //
                // foreign key parent -> material
                ""
            };
        }
    }

}
