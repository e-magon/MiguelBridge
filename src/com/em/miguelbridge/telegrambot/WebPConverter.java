package com.em.miguelbridge.telegrambot;

import java.io.IOException;

public class WebPConverter {
    public static int convert(String iPath, String oPath, boolean macOS) {
        String binPath;
        //the "dwebp"'s path
        if (macOS)
            binPath = "WebPConverter/libwebp-0.4.1-mac-10.8/bin/dwebp";
        else
            binPath = "~/WebPConverter/libwebp-0.4.1-linux-x86-32/bin/dwebp";
        
        String[] args = new String[]{binPath, iPath, "-o", oPath};

        try {
            //Runtime.getRuntime().exec(args);
            ProcessBuilder pb = new ProcessBuilder(new String[]{
                "/bin/sh",
                "-c",
                String.format("%s %s -o %s", binPath, iPath, oPath)
            });
            pb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace(System.err);
            return 1;
        }
        
        return 0;
    }
}
