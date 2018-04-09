package com.em.miguelbridge.matrixbot;

import com.em.miguelbridge.Launcher;
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
    
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public MatrixBot() throws IOException, FileNotFoundException, ParseException {
        homeUrl = getHomeServer();
    }
    
    public static String readBotUserName() throws FileNotFoundException, IOException, ParseException {
        FileReader file = new FileReader(Launcher.fileSettings);
        BufferedReader in = new BufferedReader(file);
        
        
        JSONObject obj = (JSONObject) new JSONParser().parse(in);
        in.close();
        
        String userName = (String) obj.get("matrixuser");
        return userName;
    }
    
    public String readPswd() throws FileNotFoundException, IOException, ParseException {
        FileReader file = new FileReader(Launcher.fileSettings);
        BufferedReader in = new BufferedReader(file);
        
        JSONObject obj = (JSONObject) new JSONParser().parse(in);
        in.close();
        
        String pswd = (String) obj.get("matrixpswd");
        
        return pswd;
    }
    
    public String getHomeServer() throws FileNotFoundException, IOException, ParseException {
        FileReader file = new FileReader(Launcher.fileSettings);
        BufferedReader in = new BufferedReader(file);
        
        JSONObject obj = (JSONObject) new JSONParser().parse(in);
        in.close();
        
        String server = (String) obj.get("matrixhomeserver");
        
        return server;
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
            {"user", readBotUserName()},
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
        String eventid = (String) last.get("event_id");
        String sender = (String) last.get("sender");
        JSONObject content = (JSONObject) last.get("content");
        String body = (String) content.get("body");
        
        //Come prima stringa c'è l'id del mittente, come seconda il body del messaggio
        String[] lastMessage = new String[] {sender, body, eventid};
        return lastMessage;
    }
}
