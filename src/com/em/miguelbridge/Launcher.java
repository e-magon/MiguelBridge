package com.em.miguelbridge;

import com.em.miguelbridge.matrixbot.MatrixBot;
import com.em.miguelbridge.telegrambot.TGBot;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

/*
 * @author Emanuele Magon
 */
public class Launcher {
    public static void main(String[] args) {
        // Inizializza il context delle API Telegram (richiesto)
        ApiContextInitializer.init();

        // Instanzia le API dei bot di Telegram (richiesto)
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Avvia i bot
        try {
            TGBot tgBot = new TGBot();
            MatrixBot matrixBot = new MatrixBot();
            
            System.out.println("Caricamento del bot telegram @" + tgBot.getBotUsername() + "...");
            tgBot.linkMatrixBot(matrixBot);
            botsApi.registerBot(tgBot);
            System.out.println("Bot Telegram avviato! @" + tgBot.getBotUsername());
            
            
            System.out.println("\nCaricamento del bot Matrix " + MatrixBot.readUserName() + "...");
            matrixBot.setAccessToken(matrixBot.login());
            System.out.println("Bot Matrix avviato! " + matrixBot.readUserName());
            
            String roomAddress = "!mPkXwqjuGdhEVSopiG:maxwell.ydns.eu";
            while (true) {
                //Main loop del bot di matrix
                String[] newMessaggio;
                String lastMessaggio = "";
                
                while (true) {
                    newMessaggio = matrixBot.getLastMessage(roomAddress);


                    if (!newMessaggio[0].equals(matrixBot.readUserName()) && !newMessaggio[1].equals(lastMessaggio)) {
                        tgBot.cEcho("18200812", "Qualcono da matrix dice: " + newMessaggio[1]);
                    }
                    lastMessaggio = newMessaggio[1];
                }
            }
            
        } catch (Exception ex) {
            System.err.println("Avvio caricamento bot:");
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
