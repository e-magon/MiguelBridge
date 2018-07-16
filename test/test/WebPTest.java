package test;

import com.em.miguelbridge.telegrambot.WebPConverter;

public class WebPTest {
    public static void main(String args[]) throws InterruptedException {
        String iFile = "sticker.webp";
        String oFile = "sticker.png";
        
        
        if (WebPConverter.convert(iFile, oFile, true) == 0)
            System.out.println("Done");
        else
            System.err.println("Error");
    }
}
