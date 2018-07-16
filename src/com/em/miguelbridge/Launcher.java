package com.em.miguelbridge;

import com.em.miguelbridge.botmatrix.MatrixBot;
import com.em.miguelbridge.telegrambot.TGBot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
    private final static int sleepTime = 750;
    
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
            
            //Joina tutte le room presenti nel json del collegamento
            JSONArray rooms = getRooms();
            String roomid = "";
            for (int k=0; k<rooms.size(); k++) {
                JSONObject room = (JSONObject) rooms.get(k);
                roomid = (String) room.get("matrixid");
                matrixBot.joinRoom(roomid);
            }
            
            
            String[] newMessaggio;
            String lastMessageId = "";
            
            while (true) {
                //Main loop del bot di matrix
                Thread.sleep(sleepTime);
                rooms = getRooms();
                for (int roomNumber=0; roomNumber<rooms.size(); roomNumber++) {
                    JSONObject room = (JSONObject) rooms.get(roomNumber);
                    String matrixRoomId = (String) room.get("matrixid");
                    
                    lastMessageId = getLastMessageId(matrixRoomId);
                    

                    try {
                        newMessaggio = (String[]) matrixBot.getLastMessage(matrixRoomId);
                        if (!newMessaggio[0].equals(matrixBot.readBotUserName()) &&
                                !newMessaggio[2].equals(lastMessageId) &&
                                !newMessaggio[1].equals("") && newMessaggio[1] != null) {
                            String tgroomid = (String) room.get("tgid");

                            tgBot.cEcho(tgroomid, newMessaggio[0] + ":\n" + newMessaggio[1]);
                            saveLastMessageId(newMessaggio[2], matrixRoomId);
                        }
                    } catch (Exception e) {}
                }
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private static synchronized String getLastMessageId(String matrixRoomId) throws FileNotFoundException, IOException, ParseException {
        JSONParser jparser = new JSONParser();
        FileReader file;
        BufferedReader in;
        JSONObject obj;
        JSONArray rooms;
        JSONObject room;
        int roomNumber = -1;
        
        file = new FileReader(Launcher.fileSettings);
        in = new BufferedReader(file);
        obj = (JSONObject) jparser.parse(in);
        rooms = (JSONArray) obj.get("rooms");
        
        for (int k=0; k<rooms.size(); k++) {
            JSONObject thisRoom = (JSONObject) rooms.get(k);
            String thisRoomId = (String) thisRoom.get("matrixid");
            if (matrixRoomId.equals(thisRoomId)) {
                roomNumber = k;
                break;
            }
        }
        room = (JSONObject) rooms.get(roomNumber);
        
        file.close();
        in.close();
        
        return (String) room.get("lastmessageid");
    }
    
    private static synchronized void saveLastMessageId(String messageId, String matrixRoomId) throws FileNotFoundException, IOException, ParseException {
        JSONParser jparser = new JSONParser();
        FileReader file;
        BufferedReader in;
        JSONObject obj;
        JSONArray rooms;
        JSONObject room;
        int roomNumber = -1;
        
        file = new FileReader(Launcher.fileSettings);
        in = new BufferedReader(file);
        obj = (JSONObject) jparser.parse(in);
        rooms = (JSONArray) obj.get("rooms");
        
        for (int k=0; k<rooms.size(); k++) {
            JSONObject thisRoom = (JSONObject) rooms.get(k);
            String thisRoomId = (String) thisRoom.get("matrixid");
            if (matrixRoomId.equals(thisRoomId)) {
                roomNumber = k;
                break;
            }
        }
        
        room = (JSONObject) rooms.get(roomNumber);
        room.put("lastmessageid", messageId);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(obj.toJSONString());
        String prettyJsonString = gson.toJson(je);
        
        new File(fileSettings).createNewFile();
        PrintWriter writer = new PrintWriter(fileSettings);
        writer.print(prettyJsonString);
        writer.close();
        
        file.close();
        in.close();
    }
    
    public static synchronized JSONArray getRooms() throws FileNotFoundException, IOException, ParseException {
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
        
        file.close();
        in.close();
        
        return rooms;
    }
}
