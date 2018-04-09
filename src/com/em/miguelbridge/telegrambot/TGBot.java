package com.em.miguelbridge.telegrambot;

import com.em.miguelbridge.Launcher;
import com.em.miguelbridge.matrixbot.MatrixBot;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/*
 * @author Emanuele Magon
 */
public class TGBot extends TelegramLongPollingBot {
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
            String sender = update.getMessage().getFrom().getFirstName() + " "
                    + update.getMessage().getFrom().getLastName();
            
            //Per capire qual'è l'id della chat di telegram
            //System.out.println(chat_id);
            
            echoToMatrix(testoMessaggio, sender);
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
            FileReader file = new FileReader(Launcher.fileSettings);
            BufferedReader in = new BufferedReader(file);
            JSONObject obj = (JSONObject) new JSONParser().parse(in);
            in.close();
            String token = (String) obj.get("tgtoken");
            
            return token;
        } catch (Exception e) {
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

    private void echoToMatrix(String testoMessaggio, String sender) {
        try {
            String roomAddress = "!mPkXwqjuGdhEVSopiG:maxwell.ydns.eu";
            matrixBot.sendMessage(sender + " da Telegram dice: " + testoMessaggio, roomAddress);
        } catch (Exception ex) {
            Logger.getLogger(TGBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}