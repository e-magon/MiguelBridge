package com.em.miguelbridge.matrixbot;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emanuele Magon
 */
public class prova {
    public static void main(String[] args) {
        try {
            MatrixBot bot = new MatrixBot();
            
            System.out.println(bot.readUserName() + " - " + bot.readPswd());
            System.out.println(bot.login());
        } catch (IOException ex) {
            Logger.getLogger(prova.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
