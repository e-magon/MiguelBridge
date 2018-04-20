package test;

import com.em.miguelbridge.botmatrix.MatrixBot;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emanuele Magon
 */
public class MatrixTest {
    public static void main(String[] args) {
        String accessToken, roomAddress = "!mPkXwqjuGdhEVSopiG:maxwell.ydns.eu";
        try {
            MatrixBot bot = new MatrixBot();
            
            //System.out.println(bot.readUserName() + " - " + bot.readPswd());
            accessToken = bot.login();
            
            //System.out.println(bot.joinRoom(chatID));
            
            //System.out.println(bot.sendMessage("ciaoo", chatID));
            
            String[] ultimoMess;
            while (true) {
                ultimoMess = bot.getLastMessage(roomAddress);
                

                if (!ultimoMess[0].equals(bot.readBotUserName())) {
                    System.out.println(ultimoMess[0] + " dice: " + ultimoMess[1]);
                    bot.sendMessage(ultimoMess[1], roomAddress);
                }
            }
            
        } catch (Exception ex) {
            Logger.getLogger(MatrixTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
