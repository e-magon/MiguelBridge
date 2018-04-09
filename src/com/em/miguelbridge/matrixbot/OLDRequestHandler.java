package com.em.miguelbridge.matrixbot;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Emanuele Magon
 */
public class OLDRequestHandler {
    public static String[] getRequest(String inURL) throws MalformedURLException, IOException {
        String[] risposta = new String[2];
        
        URL url = new URL(inURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int status = connection.getResponseCode();
        BufferedReader in = new BufferedReader(
        new InputStreamReader(connection.getInputStream()));
        String inputLine;
        String risp = "";
        while ((inputLine = in.readLine()) != null) {
            risp += inputLine;
        }
        in.close();
        
        risposta[0] = "" + status;
        risposta[1] = risp;
        
        return risposta;
    }
    
    public static String[] postRequest(String inURL, String[][] reqParams) throws MalformedURLException, IOException {
        String[] risposta = new String[2];
        
        URL url = new URL(inURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        
        // Send post request
        connection.setDoOutput(true);
        String postParam = "";
        for (String[] reqParam : reqParams) {
            postParam += reqParam[0] + "=" + reqParam[1] + "&";
        }
        if (postParam.length() > 0)
            postParam = postParam.substring(0, postParam.length()-1);
        System.out.println(postParam);
        
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(postParam);
        wr.flush();
        wr.close();
        
        int status = connection.getResponseCode();
        BufferedReader in = new BufferedReader(
        new InputStreamReader(connection.getInputStream()));
        String inputLine;
        String risp = "";
        while ((inputLine = in.readLine()) != null) {
            risp += inputLine;
        }
        in.close();
        
        risposta[0] = "" + status;
        risposta[1] = risp;
        
        return risposta;
    }
}
