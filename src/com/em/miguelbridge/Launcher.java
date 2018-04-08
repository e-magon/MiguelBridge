package com.em.miguelbridge;

import com.em.miguelbridge.telegrambot.TGBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/*
 * @author Emanuele Magon
 */
public class Launcher {
    public static void main(String[] args) {
        // Inizializza il context delle API Telegram (richiesto)
        ApiContextInitializer.init();

        // Instanzia le API dei bot di Telegram (richiesto)
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Avvia il bot di Telegram
        try {
            TGBot bot = new TGBot();
            System.out.println("Caricamento del bot @" + bot.getBotUsername() + " su telegram...");
            botsApi.registerBot(bot);
            System.out.println("Bot Telegram avviato! @" + bot.getBotUsername());
        } catch (TelegramApiException e) {
            System.err.println("Errore avvio bot: " + e);
        }
    }
}
