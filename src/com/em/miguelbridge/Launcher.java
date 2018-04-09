package com.em.miguelbridge;

import com.em.miguelbridge.matrixbot.MatrixBot;
import com.em.miguelbridge.telegrambot.TGBot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

/*
 * @author Emanuele Magon
 */
public class Launcher {
    public final static String fileSettings = "files/botsettings.json";
    
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
            
            
            System.out.println("\nCaricamento del bot Matrix " + MatrixBot.readBotUserName() + "...");
            matrixBot.setAccessToken(matrixBot.login());
            System.out.println("Bot Matrix avviato! " + matrixBot.readBotUserName());
            
            String roomAddress = "!mPkXwqjuGdhEVSopiG:maxwell.ydns.eu";
            
            
            String[] newMessaggio;
            String lastMessageId = "";
            
            while (true) {
                //Main loop del bot di matrix
                Thread.sleep(3 * 1000);
                lastMessageId = getLastMessageId();
                newMessaggio = (String[]) matrixBot.getLastMessage(roomAddress);

                if (!newMessaggio[0].equals(matrixBot.readBotUserName()) && !newMessaggio[2].equals(lastMessageId)) {
                    tgBot.cEcho("18200812", newMessaggio[0] + " da matrix dice: " + newMessaggio[1]);
                }

                saveLastMessageId(newMessaggio[2]);
            }
            
        } catch (Exception ex) {
            System.err.println("Avvio caricamento bot:");
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private static synchronized String getLastMessageId() throws FileNotFoundException, IOException, ParseException {
        JSONParser jparser = new JSONParser();
        FileReader file;
        BufferedReader in;
        JSONObject obj;
        JSONArray rooms;
        JSONObject room;
        
        file = new FileReader(Launcher.fileSettings);
        in = new BufferedReader(file);
        obj = (JSONObject) jparser.parse(in);
        rooms = (JSONArray) obj.get("rooms");
        room = (JSONObject) rooms.get(0);
        
        file.close();
        in.close();
        
        return (String) room.get("lastmessageid");
    }
    
    private static synchronized void saveLastMessageId(String id) throws FileNotFoundException, IOException, ParseException {
        JSONParser jparser = new JSONParser();
        FileReader file;
        BufferedReader in;
        JSONObject obj;
        JSONArray rooms;
        JSONObject room;
        
        file = new FileReader(Launcher.fileSettings);
        in = new BufferedReader(file);
        obj = (JSONObject) jparser.parse(in);
        rooms = (JSONArray) obj.get("rooms");
        room = (JSONObject) rooms.get(0);
        room.put("lastmessageid", id);
        
        new File(fileSettings).createNewFile();
        PrintWriter writer = new PrintWriter(fileSettings);
        writer.print(obj.toJSONString());
        writer.close();
        
        file.close();
        in.close();
    }
}
