/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author brand
 */
public class ChatHelper {
    static String EditServerInput(String inputServer) {
        String rawString;
        String stripEnd;

        rawString = inputServer.substring(1);
        stripEnd = rawString.substring(0, rawString.length() - 1);

        return stripEnd;
    }
    /**
     * Encrypt the user's name
     *
     * @param userName
     * @return
     */
    public static String decrypter(String userName) {
        try {
            //String text = "Helbuddy good jobrld";
            String key = "Bar12345Bar12345"; // 128 bit key
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(userName.getBytes());
            userName = new String(encrypted);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
        }
        
        return userName.substring(0,5) ;
    }
    
    public static String keyCreation(int i) {
        return Integer.toString(i);
    }
    //Potentially make the 'X' button not do anything... rather just have a leave
    //button and then that would delete the database and reset unread to 0
}
