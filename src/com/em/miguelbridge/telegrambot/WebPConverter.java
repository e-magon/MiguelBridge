package com.em.miguelbridge.telegrambot;

import java.io.IOException;

public class WebPConverter {
    public static int convert(String iPath, String oPath) {
        //the "dwebp"'s path
        String binPath = "WebPConverter/libwebp-0.4.1-linux-x86-64/bin/dwebp";
        
        String[] args = new String[]{binPath, iPath, "-o", oPath};

        try {
            Runtime.getRuntime().exec(args);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace(System.err);
            return 1;
        }
        
        return 0;
    }
}
