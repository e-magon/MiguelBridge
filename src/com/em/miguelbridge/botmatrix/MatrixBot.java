package com.em.miguelbridge.botmatrix;

import com.em.miguelbridge.Launcher;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.json.simple.*;
import org.json.simple.parser.*;

import javax.imageio.ImageIO;

/**
 * @author Emanuele Magon
 */
public class MatrixBot {
    //https://matrix.org/docs/guides/client-server.html
    private final String homeUrl;
    private String accessToken;
    private final int timeoutMs;
    
    public MatrixBot() throws IOException, FileNotFoundException, ParseException {
        homeUrl = getHomeServer();
        timeoutMs = 30 * 1000;  //30 seconds
    }
    
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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
        String requestUrl = homeUrl + "client/r0/login";

        /*
        String[][] reqParams = new String[][] {
            {"type", "m.login.password"},
            {"user", readBotUserName()},
            {"password", readPswd()}
        };
        */

        JSONObject reqParams = new JSONObject();
        reqParams.put("type", "m.login.password");
        reqParams.put("user", readBotUserName());
        reqParams.put("password", readPswd());
        
        String[] risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);
        
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(risposta[1]);
        return ""+obj.get("access_token");
    }
    
    public String joinRoom(String roomAddress) throws IOException, ParseException, URISyntaxException {
        String requestUrl = homeUrl + String.format("client/r0/rooms/%s/join?access_token=%s",
                roomAddress, accessToken);
        
        JSONObject reqParams = new JSONObject();
        String[] risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);
        
        return risposta[0];
    }
    
    public synchronized String sendMessage(String message, String roomAddress) throws IOException, URISyntaxException {
        String requestUrl = homeUrl + String.format("client/r0/rooms/%s/send/m.room.message?access_token=%s",
                roomAddress, accessToken);

        /*
        String[][] reqParams = new String[][] {
            {"msgtype", "m.text"},
            {"body", message}
        };
        */
        JSONObject reqParams = new JSONObject();
        reqParams.put("msgtype", "m.text");
        reqParams.put("body", message);
        
        String[] risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);
        
        return risposta[0] + " - " + risposta[1];
    }

    public synchronized String sendFile(String roomAddress, File file,
            String nomeFile, String type) throws IOException, URISyntaxException, ParseException {
        String requestUrl;
        if (type.equals("jpg")) {
            requestUrl = homeUrl + String.format("media/r0/upload?filename=%s&access_token=%s",
                    file.getName()+".jpg", accessToken);
        }
        else {
            requestUrl = homeUrl + String.format("media/r0/upload?filename=%s&access_token=%s",
                    nomeFile, accessToken);
        }
        
        String[] risposta = RequestHandler.postRequestFile(requestUrl, file, type);

        JSONObject uriFileObj = (JSONObject) new JSONParser().parse(risposta[1]);
        String uriFile = (String) uriFileObj.get("content_uri");

        /*
        System.out.println("Il file è " + uriFile);
        for (String tmp : risposta)
            System.out.println(tmp);
        */
        
        
        requestUrl = homeUrl + String.format("client/r0/rooms/%s/send/m.room.message?access_token=%s",
                roomAddress, accessToken);

        if (type.equals("jpg")) {
            JSONObject reqParams = new JSONObject();
            JSONObject objInfo = new JSONObject();
            JSONObject thumb = new JSONObject();
            BufferedImage bimg = ImageIO.read(file);
            int width = bimg.getWidth();
            int height = bimg.getHeight();

            thumb.put("mimetype", "image/jpeg");
            thumb.put("h", height);
            thumb.put("w", width);
            thumb.put("size", file.length());

            objInfo.put("mimetype", "image/jpeg");
            objInfo.put("size", file.length());
            //objInfo.put("thumbnail_info", thumb);
            //objInfo.put("thumbnail_url", uriFile);
            objInfo.put("h", height);
            objInfo.put("w", width);
            //objInfo.put("orientation", 0);

            reqParams.put("info", objInfo);
            reqParams.put("msgtype", "m.image");
            reqParams.put("body", file.getName());
            reqParams.put("url", uriFile);

            risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);

            return risposta[0] + " - " + risposta[1];
        }
        
        if (type.equals("png")) {
            JSONObject reqParams = new JSONObject();
            JSONObject objInfo = new JSONObject();
            JSONObject thumb = new JSONObject();
            BufferedImage bimg = ImageIO.read(file);
            int width = bimg.getWidth();
            int height = bimg.getHeight();

            thumb.put("mimetype", "image/png");
            thumb.put("h", height);
            thumb.put("w", width);
            thumb.put("size", file.length());

            objInfo.put("mimetype", "image/png");
            objInfo.put("size", file.length());
            //objInfo.put("thumbnail_info", thumb);
            //objInfo.put("thumbnail_url", uriFile);
            objInfo.put("h", height);
            objInfo.put("w", width);
            //objInfo.put("orientation", 0);

            reqParams.put("info", objInfo);
            reqParams.put("msgtype", "m.image");
            reqParams.put("body", file.getName());
            reqParams.put("url", uriFile);

            risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);

            return risposta[0] + " - " + risposta[1];
        }
        
        else {
            JSONObject reqParams = new JSONObject();
            JSONObject objInfo = new JSONObject();
            BufferedImage bimg = ImageIO.read(file);

            objInfo.put("mimetype", "text/plain");   //TODO
            objInfo.put("size", file.length());

            reqParams.put("info", objInfo);
            reqParams.put("msgtype", "m.file");
            reqParams.put("body", nomeFile);
            reqParams.put("url", uriFile);

            risposta = RequestHandler.postRequestJSON(requestUrl, reqParams);

            return risposta[0] + " - " + risposta[1];
        }
    }
    
    public String[] getLastMessage(String roomAddress) {
        try {
            String filtro = URLEncoder.encode("{\"room\":{\"timeline\":{\"limit\":1}}}", "UTF-8");
            String requestUrl = homeUrl +
                    String.format("client/r0/sync?filter=%s&timeout=" + timeoutMs + "&access_token=%s",
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
            
            //Come prima stringa c'è l'id del mittente, come seconda il body del messaggio e come terzo l'id del messaggio
            String[] lastMessage = new String[] {sender, body, eventid};
            return lastMessage;
        } catch (Exception ex) {
            return new String[] {"", "", ""};
        }
    }
}
