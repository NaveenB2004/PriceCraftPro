package com.sachi.pricecraftpro;

import com.formdev.flatlaf.FlatDarkLaf;
import com.sachi.pricecraftpro.helper.DBConnection;
import com.sachi.pricecraftpro.ui.Home;
import com.sachi.pricecraftpro.ui.Splash;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PriceCraftPro {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        Splash s = new Splash();
        s.setVisible(true);
        DBConnection.checkDb();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PriceCraftPro.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        s.dispose();
        new Home().setVisible(true);
    }
}
