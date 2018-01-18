/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.service.Firebase;
import java.util.*;
import javax.swing.JFrame;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.model.FirebaseResponse;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author brand
 */
public class ChatRoom extends JFrame {

    public static ChatGui gui = new ChatGui();
    public static ChatHelper chatHelp = new ChatHelper();
    //Regular message board of Firebase db
    public static Firebase msgBrd;
    private static Firebase unreadBrd;
    public static Firebase chatterBrd, usersBrd;
    private FirebaseResponse response;
    public FirebaseResponse chatterRep, userRep;
    private Map<String, Object> msgMap = new LinkedHashMap<>();
    private Map<String, Object> unreadMap = new LinkedHashMap<>();
    public static Map<String, Object> chatterMap = new LinkedHashMap<>();
    private String userInput, tempKey; //used for textArea input 
    private static String chatterKey;
    private String serverMsgs; //all the messages that are stored in the firebase to be retrieved and displayed in new windows
    public static int readNo, notifNo; //used with serverMsgs 
     //the number of unread msgs that increase everytime a msg is sent
    public String notif; //number of unread msgs as the string; notifNo extracts and converts that string to Int 
    private String timeStamp;
    //users input their name before commencing to main program
    private static String userName_Input, username_Enc;
    //encrypted username
    public static int chatterDigit;
   
    public void ChatRoom() throws FirebaseException, JacksonUtilityException, UnsupportedEncodingException {
        readNo = 1;
        notifNo = 0;
        chatterDigit = 0;
        msgBrd = new Firebase("https://messengers-ae4dd.firebaseio.com/");
        unreadBrd = new Firebase("https://messengers-ae4dd.firebaseio.com/Unread");
        chatterBrd = new Firebase("https://messengers-ae4dd.firebaseio.com/Chatters");
        usersBrd  = new Firebase("https://messengers-ae4dd.firebaseio.com/Users");
        response = msgBrd.get();
        msgMap = response.getBody();
        serverMsgs = response.getRawBody();
        //upload previous text to new windows opened
        while ((serverMsgs = (String) msgMap.get(Integer.toString(readNo))) != null) {
            gui.convoTextArea.append(serverMsgs + "\n");
            notifNo++;
            readNo++;
        }
        
        /**
         * Timer function checks for an update in the firebase every second. If
         * there is an update, the conversation text area is updated.
         *
         */
        Timer timed = new Timer();
        TimerTask newTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    timeStamp = new SimpleDateFormat("HH.mm.ss").format(new Date());

                    response = unreadBrd.get();
                    chatterRep = chatterBrd.get();
                    
                    chatterMap = chatterRep.getBody();
                    unreadMap = response.getBody();
                    /*typecast notif which reads the unread value (originally as a string) 
                    and has the original index*/
                    int notif = (int) unreadMap.get("unread");
                    int chatterNotif = (int) chatterMap.get("chatters");

                    //if this case is true it means there is an update on the firebase server
                    if (notif != notifNo) {
                        int currentNumber = notifNo;
                        response = msgBrd.get();
                        msgMap.clear();
                        msgMap = response.getBody();

                        ArrayList<String> messages = new ArrayList<>();
                        ArrayList<String> newMessages = new ArrayList<>();

                        int currentIndex = 1;
                        String newText;
                        while ((newText = (String) msgMap.get(Integer.toString(currentIndex))) != null) {
                            messages.add(newText);
                            currentIndex++;
                        }
                        for (int i = currentNumber; i < messages.size(); i++) {
                            //add newly sent message into the arraylist to be added 
                            newMessages.add(messages.get(i));
                        }
                        for (int i = 0; i < newMessages.size(); i++) {
                            gui.convoTextArea.append("[" + timeStamp + "] " + newMessages.get(i) + "\n");
                        }
                        notifNo = notif;
                    }
                    if(chatterNotif != chatterDigit){
                        //add encrpyted username to the Chatters map so the name is different
                        if(username_Enc != null){
                            //gui.convoTextArea.append("User " + username_Enc + " has joined the chat\n");
                        }
                        
                        if(chatterDigit > chatterNotif){
                            //when a user leaves
                            gui.convoTextArea.append("A user has left the chat.\n");
                        }
                        else if(chatterDigit < chatterNotif){
                            UserReveal();
                        }
                        chatterDigit = chatterNotif;
                        gui.num_of_users.setText(Integer.toString(chatterDigit));
                    }
                } catch (FirebaseException | UnsupportedEncodingException ex) {
                    Logger.getLogger(ChatRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        timed.schedule(newTask, 1000, 1000);
    }

    /**
     * @param args the command line arguments
     * @throws net.thegreshams.firebase4j.error.FirebaseException
     * @throws net.thegreshams.firebase4j.error.JacksonUtilityException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws FirebaseException, JacksonUtilityException, UnsupportedEncodingException, InterruptedException {
        ChatGui frame = new ChatGui();
        int certified = 0;
        
        ChatRoom obj = new ChatRoom();
        obj.ChatRoom();
        
        do {
            userName_Input = (String) JOptionPane.showInputDialog(frame, "Enter username before commencing to chat");
            //cancel button
            if (userName_Input == null) {
                JOptionPane.showMessageDialog(null, "Session Terminated.");
                System.exit(0);
            } else if (!userName_Input.isEmpty() && !userName_Input.contains(" ")) {
                certified = 1;
                System.out.println(userName_Input + "\n");
                username_Enc = ChatHelper.decrypter(userName_Input);
                InsertChatter(username_Enc);
                chatterDigit++;
                Thread.sleep(1000);
                gui.num_of_users.setText(Integer.toString(chatterDigit));
                gui.convoTextArea.append("Your encrypted username is: "+ username_Enc.substring(0,5) + "\n");
                //increase a variable to the firebase because if "ok", a user is added
            } else {
                JOptionPane.showMessageDialog(null, "No space characters allowed.");
            }
        } while (certified == 0);

        if (certified == 1) {
            gui.setVisible(true);
        }
    }
    
    void sendButtonActionPerformed(ActionEvent e) throws FirebaseException, JacksonUtilityException, UnsupportedEncodingException {
        if (e.getSource() == gui.sendButton) {
            sendToServer();
        }
    }

    /**
     * Send messages into the firebase server
     *
     * @throws FirebaseException
     * @throws JacksonUtilityException
     * @throws UnsupportedEncodingException
     */
    public void sendToServer() throws FirebaseException, JacksonUtilityException, UnsupportedEncodingException {
        userInput = gui.msgTextArea.getText();
        //gui.convoTextArea.append(userInput + "\n");
        gui.msgTextArea.setText("");

        gui.flagSent = 1;

        Map<String, Object> unreadMapL = new LinkedHashMap<>();

        unreadMapL.put("unread", notifNo + 1);
        unreadBrd.patch(unreadMapL);
        tempKey = ChatHelper.keyCreation(notifNo + 1);

        msgMap.put(tempKey, userInput);
        msgBrd.patch(msgMap);
    }
    /**
     * Insert user into the chat room and keep tabs of how many users are in it 
     * @param userName
     * @throws FirebaseException
     * @throws JacksonUtilityException
     * @throws UnsupportedEncodingException 
     */
    private static void InsertChatter(String userName) throws FirebaseException, JacksonUtilityException, UnsupportedEncodingException{
        Map<String,Object> chatMap = new LinkedHashMap<>();
        Map<String, Object> userMap = new LinkedHashMap<>();
        
        chatMap.put("chatters", chatterDigit+1);
        chatterBrd.patch(chatMap);
        
        userMap.put("users", userName);
        usersBrd.patch(userMap);
    }
    
    /**
     * Return the user's encrypted name into the chat area
     * @throws FirebaseException
     * @throws UnsupportedEncodingException 
     */
    private void UserReveal() throws FirebaseException, UnsupportedEncodingException{
        String username;
        Map<String, Object> nameMap = new LinkedHashMap<>();
        userRep = usersBrd.get();
        nameMap = userRep.getBody();
        //username = userRep.getRawBody();
        username = (String)nameMap.get("users");
        //while((username = (String)nameMap.get("users"))!= null){
        gui.convoTextArea.append("User " + username + " has joined the chat.\n");
    }
   
}
