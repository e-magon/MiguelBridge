package com.em.miguelbridge.telegrambot;

import com.em.miguelbridge.Launcher;
import com.em.miguelbridge.botmatrix.MatrixBot;

import java.io.*;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/*
 * @author Emanuele Magon
 */
public class TGBot extends TelegramLongPollingBot {
    private MatrixBot matrixBot;
    
    public void linkMatrixBot(MatrixBot matrixBot) {
        this.matrixBot = matrixBot;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        /*Istruzione usata per prendere i gli ID dei file e documenti
        try {
            System.out.println(update.getMessage().getVoice().getFileId());
        } catch (Exception e) {System.out.println(e);}
        */        
        
        //Controllo per vedere se l'update è un messaggio testuale e che esso non sia vuoto
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equalsIgnoreCase("/chatid") ||
                    update.getMessage().getText().equalsIgnoreCase("/chatid@" + getBotUsername())) {
                String chat_id = "" + update.getMessage().getChatId();
                cEcho(chat_id, chat_id);
            }
            else if (update.getMessage().getText().equalsIgnoreCase("/info") ||
                    update.getMessage().getText().equalsIgnoreCase("/info@" + getBotUsername())) {
                String chat_id = "" + update.getMessage().getChatId();
                cInfo(chat_id);
            }

            else {
                //Testo e mittente
                String testoMessaggio = update.getMessage().getText();
                String chat_id = "" + update.getMessage().getChatId();
                String sender = update.getMessage().getFrom().getFirstName() + " "
                        + update.getMessage().getFrom().getLastName();
                String destination;
                try {
                    destination = getDestinationRoom(chat_id);
                    if (destination == null)
                        throw new Exception();
                    matrixBot.sendMessage(sender + ":\n" + testoMessaggio, destination);
                } catch (Exception ex) {
                    cEcho(chat_id, "Errore: questa chat non è collegata a matrix.");
                }
            }
        }

        else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            String chat_id = "" + update.getMessage().getChatId();
            String sender;
            String destination;

            if (update.getMessage().getFrom().getLastName() != null)
                sender = update.getMessage().getFrom().getFirstName() + " "
                        + update.getMessage().getFrom().getLastName();
            else
                sender = update.getMessage().getFrom().getFirstName();

            java.io.File downloadedFile = null;

            // When receiving a photo, you usually get different sizes of it
            List<PhotoSize> photos = update.getMessage().getPhoto();

            // We fetch the bigger photo
            PhotoSize foto =  photos.stream()
                    .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                    .findFirst()
                    .orElse(null);

            String filePath;
            // We create a GetFile method and set the file_id from the photo
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(foto.getFileId());

            try {
                // We execute the method using AbsSender::execute method.
                final org.telegram.telegrambots.api.objects.File file = execute(getFileMethod);
                // We now have the file_path
                filePath = file.getFilePath();
                // Download the file calling AbsSender::downloadFile method
                downloadedFile = downloadFile(filePath);
            } catch (TelegramApiException e) {
                e.printStackTrace(System.err);
            }


            try {
                destination = getDestinationRoom(chat_id);
                if (destination == null)
                    throw new Exception();
                matrixBot.sendMessage(sender + " ha inviato una foto:", destination);
                matrixBot.sendFile(destination, downloadedFile, null, true);
            } catch (Exception ex) {
                cEcho(chat_id, "Errore: questa chat non è collegata a matrix.");
                ex.printStackTrace(System.err);
            }
        }
        
        else if (update.hasMessage() && update.getMessage().hasDocument()) {
            String chat_id = "" + update.getMessage().getChatId();
            String sender;
            String destination;
            String nomeFile = update.getMessage().getDocument().getFileName();

            if (update.getMessage().getFrom().getLastName() != null)
                sender = update.getMessage().getFrom().getFirstName() + " "
                        + update.getMessage().getFrom().getLastName();
            else
                sender = update.getMessage().getFrom().getFirstName();

            java.io.File downloadedFile = null;
            Document documento = update.getMessage().getDocument();
            String filePath;
            
            // We create a GetFile method and set the file_id from the photo
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(documento.getFileId());

            try {
                // We execute the method using AbsSender::execute method.
                final org.telegram.telegrambots.api.objects.File file = execute(getFileMethod);
                // We now have the file_path
                filePath = file.getFilePath();
                // Download the file calling AbsSender::downloadFile method
                downloadedFile = downloadFile(filePath);
            } catch (TelegramApiException e) {
                e.printStackTrace(System.err);
            }


            try {
                destination = getDestinationRoom(chat_id);
                if (destination == null)
                    throw new Exception();
                matrixBot.sendMessage(sender + " ha inviato un file:", destination);
                matrixBot.sendFile(destination, downloadedFile, nomeFile, false);
            } catch (Exception ex) {
                cEcho(chat_id, "Errore: questa chat non è collegata a matrix.");
                ex.printStackTrace(System.err);
            }
        }
    }

    @Override
    public String getBotUsername() {
        //Return bot username
        //If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return "MiguelBridgeBot";
    }

    @Override
    public String getBotToken() {
        try {
            //Return bot token from BotFather
            FileReader file = new FileReader(Launcher.fileSettings);
            BufferedReader in = new BufferedReader(file);
            JSONObject obj = (JSONObject) new JSONParser().parse(in);
            in.close();
            String token = (String) obj.get("tgtoken");
            
            return token;
        } catch (Exception e) {
            System.out.println("Errore apertura file token: " + e);
        }
        return "";
    }
    
    private String getDestinationRoom(String sender_id) throws IOException, FileNotFoundException, ParseException {
        //Dalla chat mittente in telegram ritorna l'id della chat di matrix relativa
        JSONArray rooms = Launcher.getRooms();
        for (int k=0; k<rooms.size(); k++) {
            JSONObject room = (JSONObject) rooms.get(k);
            String roomTgId = (String) room.get("tgid");
            if (roomTgId.equals(sender_id))
                return (String) room.get("matrixid");
        }
        return null;
    }
    
    //----------COMANDI----------
    public void cEcho(String chat_id, String testoMessaggio){
        //Crea la stringa da mandare e la rende uguale al messaggio originale
        SendMessage messaggio;
        messaggio = new SendMessage()
                .setChatId(chat_id)
                .setText(testoMessaggio);

        try {
            //Invia il messaggio all'utente
            //sendMessage(messaggio);
            execute(messaggio);
        } catch (Exception e) {
            System.err.println("Errore: " + e);
        }
    }

    public void cInfo(String chat_id){
        String stringa = "Bot utilizzato per il bridge tra Telegram e Matrix.\n" +
                "Per informazioni: https://github.com/AhabHyde/MiguelBridge";
        cEcho(chat_id, stringa);
    }
}