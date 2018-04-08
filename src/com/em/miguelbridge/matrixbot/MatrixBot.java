package com.em.miguelbridge.matrixbot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Emanuele Magon
 */
public class MatrixBot {
    //https://matrix.org/docs/guides/client-server.html
    private String homeUrl;
    private String fileInfo;
    
    public MatrixBot() {
        homeUrl = "https://maxwell.ydns.eu/_matrix/client/";
        fileInfo = "files/MatrixBotInfo.txt";
    }
    
    public String readUserName() throws FileNotFoundException, IOException {
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
     * @return Access Token per il bot, da utilizzare nelle prossime chiamate
     */
    public String login() throws IOException {
        String requestUrl = "r0/login";
        String[][] reqParams = new String[][] {
            {"\"type\"", "\"m.login.password\""},
            {"\"user\"", "\"" + readUserName() + "\""},
            {"\"password\"", "\"" + readPswd() + "\""}
        };
        
        String[] risposta = RequestHandler.postRequest(homeUrl, reqParams);
        
        
        return (risposta[0] + " - " + risposta[1]);
    }
}
