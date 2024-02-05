package com.naveenb2004.pricecraftpro;

import com.formdev.flatlaf.FlatDarkLaf;
import com.naveenb2004.pricecraftpro.db.DBConnection;
import com.naveenb2004.pricecraftpro.ui.Home;

/**
 *
 * @author NaveenB2004
 */
public class PriceCraftPro {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        DBConnection.checkDb();
        new Home().setVisible(true);
    }
}
