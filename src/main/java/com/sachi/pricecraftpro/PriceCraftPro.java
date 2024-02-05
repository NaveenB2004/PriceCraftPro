package com.sachi.pricecraftpro;

import com.formdev.flatlaf.FlatDarkLaf;
import com.sachi.pricecraftpro.db.DBConnection;
import com.sachi.pricecraftpro.ui.Home;

public class PriceCraftPro {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        DBConnection.checkDb();
        new Home().setVisible(true);
    }
}
