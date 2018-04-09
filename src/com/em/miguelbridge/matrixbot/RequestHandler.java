package com.em.miguelbridge.matrixbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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
    
    public static String[] postRequestJSON(String inUrl, String[][] reqParams) throws IOException, URISyntaxException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(inUrl);
        
        //add header
	post.setHeader("User-Agent", "Mozilla/5.0");
        post.addHeader("Content-Type", "charset=UTF_8");

        JSONObject obj = new JSONObject();
        if (reqParams != null) {
            for (String[] param : reqParams)
                obj.put(param[0], param[1]);
        }
        
        String jsonString = obj.toJSONString();
        

        StringEntity requestEntity = new StringEntity(obj.toJSONString(), ContentType.APPLICATION_JSON);
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
}
