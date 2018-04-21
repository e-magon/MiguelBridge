package com.em.miguelbridge.botmatrix;

import java.io.*;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;

/**
 * @author Emanuele Magon
 */
public class RequestHandler {
    public static String[] getRequest(String inUrl) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(inUrl);
        //add header
	request.setHeader("User-Agent", "Mozilla/5.0");
        request.addHeader("Content-Type", "charset=UTF_8");
        
	HttpResponse response = client.execute(request);

	BufferedReader rd = new BufferedReader(
		new InputStreamReader(response.getEntity().getContent()));

	StringBuffer result = new StringBuffer();
	String line = "";
	while ((line = rd.readLine()) != null)
		result.append(line);
        
        String[] risposta =
                new String[] {""+response.getStatusLine().getStatusCode(), result.toString()};
        return risposta;
    }
    
    public static String[] postRequestJSON(String inUrl, JSONObject inObj) throws IOException, URISyntaxException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(inUrl);
        
        //add header
	    post.setHeader("User-Agent", "Mozilla/5.0");
        post.addHeader("Content-Type", "charset=UTF_8");
        
        String jsonString = inObj.toJSONString();
        StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);

        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null)
            result.append(line);
        
        String[] risposta =
                new String[] {""+response.getStatusLine().getStatusCode(), result.toString()};
        return risposta;
    }

    public static String[] postRequestFile(String inUrl, File file) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(inUrl);
        httpPost.setHeader("Content-Type", "image/jpeg");

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);

        HttpEntity entity = new ByteArrayEntity(b);
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null)
            result.append(line);

        String[] risposta =
                new String[] {""+response.getStatusLine().getStatusCode(), result.toString()};
        return risposta;
    }
}
