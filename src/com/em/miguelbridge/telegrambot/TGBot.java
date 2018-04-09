package com.em.miguelbridge.telegrambot;

import com.em.miguelbridge.matrixbot.MatrixBot;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/*
 * @author Emanuele Magon
 */
public class TGBot extends TelegramLongPollingBot {
    //Costanti con il mio id e il nome del file delle richieste
    private final String admin_id = "18200812";
    private final String fileToken = "files/TGbot.token";
    private MatrixBot matrixBot;
    
    public void linkMatrixBot(MatrixBot matrixBot) {
        this.matrixBot = matrixBot;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        /*Istruzione usata per prendere i gli ID dei file e documenti
        try {
            System.out.println(update.getMessage().getVoice().getFileId());
        } catch (Exception e) {System.out.println(e);}
        */        
        
        //Controllo per vedere se l'update è un messaggio testuale e che esso non sia vuoto
        if (update.hasMessage() && update.getMessage().hasText()) {
            //Testo e mittente
            String testoMessaggio = update.getMessage().getText();
            String chat_id = "" + update.getMessage().getChatId();
            System.out.println(chat_id);
            sendToMatrix(testoMessaggio);
            }
        }

    @Override
    public String getBotUsername() {
        //Return bot username
        //If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return "MiguelBridgeBot";
    }

    @Override
    public String getBotToken() {
        try {
            //Return bot token from BotFather
            //Legge il file di testo con il nome passato. Mantiene gli a capo e tabulazioni
            BufferedReader reader;
            reader = new BufferedReader(new FileReader (fileToken));
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Errore apertura file token: " + e);
        }
        return "";
    }
    
    
    //----------COMANDI----------
    public void cEcho(String chat_id, String testoMessaggio){
        //Crea la stringa da mandare e la rende uguale al messaggio originale
        SendMessage messaggio;
        messaggio = new SendMessage()
                .setChatId(chat_id)
                .setText(testoMessaggio);
        
        try {
            //Invia il messaggio all'utente
            sendMessage(messaggio);
        } catch (Exception e) {
            System.err.println("Errore: " + e);
        }
    }

    private void sendToMatrix(String testoMessaggio) {
        try {
            String roomAddress = "!mPkXwqjuGdhEVSopiG:maxwell.ydns.eu";
            matrixBot.sendMessage("Qualcuno da Telegram dice: " + testoMessaggio, roomAddress);
        } catch (Exception ex) {
            Logger.getLogger(TGBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}