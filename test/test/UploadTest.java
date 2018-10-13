package test;

import com.em.miguelbridge.botmatrix.MatrixBot;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class UploadTest {
    public static void main(String[] args) throws IOException, ParseException, URISyntaxException {
        /*
        MatrixBot bot = new MatrixBot();
        String token = bot.login();
        String url = "https://maxwell.ydns.eu/_matrix/media/r0/" +
                "upload?filename=prova.png&access_token=" + token;

        File file = new File("prova.png");
        FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", fileBody);
        HttpEntity entity = builder.build();

        HttpPost request = new HttpPost(url);
        request.setEntity(entity);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);


        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null)
            result.append(line);

        System.out.println(response.getStatusLine() + " -e- " + result.toString());
        */

        MatrixBot bot = new MatrixBot();
        String token = bot.login();
        bot.setAccessToken(token);
        File file = new File("prova.jpg");
        //bot.sendMessage("provaa", "!mPkXwqjuGdhEVSopiG:maxwell.ydns.eu");

        System.out.println(bot.sendFile("!mPkXwqjuGdhEVSopiG:maxwell.ydns.eu", file, null, true));
    }
}
