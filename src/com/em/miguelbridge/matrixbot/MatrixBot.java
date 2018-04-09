package com.em.miguelbridge.matrixbot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * @author Emanuele Magon
 */
public class MatrixBot {
    //https://matrix.org/docs/guides/client-server.html
    private final String homeUrl;
    private final static String fileInfo = "files/MatrixBotInfo.txt";
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public MatrixBot() {
        homeUrl = "https://maxwell.ydns.eu/_matrix/client/r0";
    }
    
    public static String readUserName() throws FileNotFoundException, IOException {
        FileReader file = new FileReader(fileInfo);
        BufferedReader in = new BufferedReader(file);
        String str = in.readLine();
        in.close();
        
        return str;
    }
    
    public String readPswd() throws FileNotFoundException, IOException {
        FileReader file = new FileReader(fileInfo);
        BufferedReader in = new BufferedReader(file);
        in.readLine(); //Usato per leggere la seconda riga
        String str = in.readLine();
        in.close();
        
        return str;
    }
    
    /**
     * 
     * @return Access Token per il bot, da utilizzare nelle prossime richieste HTTP
     * @throws java.io.IOException
     * @throws org.json.simple.parser.ParseException
     * @throws java.net.URISyntaxException
     */
    public String login() throws IOException, ParseException, URISyntaxException {
        String requestUrl = homeUrl + "/login";
        
        String[][] reqParams = new String[][] {
            {"type", "m.login.password"},
            {"user", readUserName()},
            {"password", readPswd()}
        };
        
        String[] risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);
        
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(risposta[1]);
        return ""+obj.get("access_token");
    }
    
    public String joinRoom(String roomAddress) throws IOException, ParseException, URISyntaxException {
        String requestUrl = homeUrl + String.format("/rooms/%s/join?access_token=%s",
                roomAddress, accessToken);
        
        String[][] reqParams = null;        
        String[] risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);
        
        return risposta[0] + " - " + risposta[1];
    }
    
    public String sendMessage(String message, String roomAddress) throws IOException, URISyntaxException {
        String requestUrl = homeUrl + String.format("/rooms/%s/send/m.room.message?access_token=%s",
                roomAddress, accessToken);
        
        String[][] reqParams = new String[][] {
            {"msgtype", "m.text"},
            {"body", message}
        };
        
        String[] risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);
        
        return risposta[0] + " - " + risposta[1];
    }
    
    public String[] getLastMessage(String roomAddress) throws IOException, ParseException {
        String filtro = URLEncoder.encode("{\"room\":{\"timeline\":{\"limit\":1}}}", "UTF-8");
        String requestUrl = homeUrl +
                String.format("/sync?filter=%s&access_token=%s",
                filtro, accessToken);
        
        String[] risposta = RequestHandler.getRequest(requestUrl);
        
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(risposta[1]);
        JSONObject rooms = (JSONObject) obj.get("rooms");
        JSONObject joined = (JSONObject) rooms.get("join");
        JSONObject thisRoom = (JSONObject) joined.get(roomAddress);
        JSONObject timeline = (JSONObject) thisRoom.get("timeline");
        JSONArray events = (JSONArray) timeline.get("events");
        JSONObject last = (JSONObject) events.get(0);
        String sender = (String) last.get("sender");
        JSONObject content = (JSONObject) last.get("content");
        String body = (String) content.get("body");
        
        //Come prima stringa c'è l'id del mittente, come seconda il body del messaggio
        String[] lastMessage = new String[] {sender, body};
        return lastMessage;
    }
}
