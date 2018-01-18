/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

import static chatroom.ChatRoom.chatterBrd;
import static chatroom.ChatRoom.chatterDigit;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.model.FirebaseResponse;
import net.thegreshams.firebase4j.service.Firebase;

/**
 *
 * @author brand
 */
public class ChatGui extends JFrame{
    public ChatRoom chatter = new ChatRoom();
    //flagSent turns 1 when sendButton is pressed, sending something to the server. Resetted to 0 in the flagSentcheck code
    public int flagSent=0;
    //counter increases by one everytime there is a msg sent and stored in Unread->unread=counter
    public int counter;
    private FirebaseResponse response;
    private String newNum;
    
    public ChatGui(){
        super("Message Board");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        
        JMenuItem leaveRoom = new JMenuItem("Leave");
        JMenuItem customize = new JMenuItem("Customize");
        JMenuItem clearChat = new JMenuItem("Clear Sreen");
        
        roomMenu.add(leaveRoom);
        leaveRoom.addActionListener(new LeaveRoomListener());
        optionsMenu.add(customize);
        customize.addActionListener(new CustomizeListener());
        optionsMenu.add(clearChat); 
        clearChat.addActionListener(new ClearListener());
    }
    
    private class LeaveRoomListener implements ActionListener{
        /**
         * This function makes the "Leave" menu item act exactly like if you were to close the window via
         * 'x' icon
         * @param ae 
         */
        @Override
        public void actionPerformed(ActionEvent ae) {
            //decrement Firebase chatters by 1
            WindowEvent closing = new WindowEvent(ChatGui.this, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closing);
            
        }
    }
    /**
     * Customize menu item class
     */
    private class CustomizeListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            ChatGui frame = new ChatGui();
            ImageIcon icon = new ImageIcon();
            Object[] colourWheel = {"orange", "red", "yellow", "green", "pink", "gray"};
            String colourCust = (String)JOptionPane.showInputDialog(frame,"Change colour of text area to the follow colours:","Customize",JOptionPane.PLAIN_MESSAGE,icon,colourWheel,"red");
            
            if(colourCust == null){
                //do nothing... just continue
            }
            else{
                switch (colourCust) {
                    case "red":
                        convoTextArea.setBackground(Color.RED);
                        msgTextArea.setBackground(Color.RED);
                        break;
                    case "yellow":
                        convoTextArea.setBackground(Color.YELLOW);
                        msgTextArea.setBackground(Color.YELLOW);
                        break;
                    case "green":
                        convoTextArea.setBackground(Color.GREEN);
                        msgTextArea.setBackground(Color.GREEN);
                        break;
                    case "pink":
                        convoTextArea.setBackground(Color.PINK);
                        msgTextArea.setBackground(Color.PINK);
                        break;
                    case "gray":
                        convoTextArea.setBackground(Color.LIGHT_GRAY);
                        msgTextArea.setBackground(Color.LIGHT_GRAY);
                        break;
                    case "orange":
                        convoTextArea.setBackground(Color.ORANGE);
                        msgTextArea.setBackground(Color.ORANGE);
                        break;
                    default:
                        //do nothing
                        convoTextArea.setBackground(Color.ORANGE);
                        msgTextArea.setBackground(Color.ORANGE);
                        break;
                }
            }
        }   
    }
    /**
     * Clearchat menu item class
     */
    private class ClearListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent ae) {
            convoTextArea.setText("");
        }
    }
    /**
     * This function will simply get the Unread child of the firebase msg board
     * and reset the unread value = 0 so that when everyone/anyone leaves, the old 
     * conversation is overwritten with new text
     */
    public void resetFireBase(){
        try {
            Firebase unreadKey = new Firebase("https://messengers-ae4dd.firebaseio.com/Unread");
            //unreadKey.addQuery("Unread", "unread");
            response = unreadKey.get("unread");

            Map<String, Object> unreadMap = new LinkedHashMap<>();
            //reset unread value = 0
            unreadMap.put("unread", 0);
            unreadKey.patch(unreadMap);
        } catch (FirebaseException | UnsupportedEncodingException | JacksonUtilityException ex) {
            Logger.getLogger(ChatGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Confirm window that only applies to if the user presses the X at the top right of the window
    @Override
    public void processWindowEvent(WindowEvent e) {
        String filePath;
        filePath = System.getProperty("user.dir");
        try {
            chatter.chatterRep = ChatRoom.chatterBrd.get();
        } catch (FirebaseException ex) {
            Logger.getLogger(ChatGui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ChatGui.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
        chatter.chatterMap = chatter.chatterRep.getBody();
        int chatterNotif = (int) chatter.chatterMap.get("chatters");
        
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {          
            File historyFile;
            boolean fvar = false;
            FileWriter historyWrite;
            
            int exit = JOptionPane.showConfirmDialog(this, "Save conversation before exiting?");
            if (exit == JOptionPane.YES_OPTION) {
                try {
                    //decrement Firebase chatters by 1 (and in No_option as well)
                    historyFile = new File(filePath + "/Chat_History.txt");  
                    fvar = historyFile.createNewFile();
                    historyFile.delete();
                    //for some reason the file is created below
                    fvar = historyFile.createNewFile();
                    
                    String historyText = convoTextArea.getText();
                    String[] breakdown;
                    historyWrite = new FileWriter(historyFile);
                    //breakdown the text area to make it normal single strings 
                    breakdown = historyText.split("\n");
                    for(String words: breakdown){
                        historyWrite.write(words);
                        historyWrite.append(System.getProperty("line.separator"));
                    }
                    historyWrite.flush();
                    historyWrite.close();
                    resetFireBase();
                    ChatterLeave();
                    
                    System.exit(0);
                } catch (IOException ex) {
                    Logger.getLogger(ChatGui.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
            else if(exit == JOptionPane.NO_OPTION){
                resetFireBase();     
                ChatterLeave();
                System.exit(0);
            }
        } 
        else {
            super.processWindowEvent(e);
        }
    }
    /**
     * Decrement the number of chatters by 1
     */
    public void ChatterLeave(){
        try {
            Map<String,Object> chatMap = new LinkedHashMap<>();
            
            chatMap.put("chatters", chatterDigit-1);
            chatterBrd.patch(chatMap);
        } catch (FirebaseException | JacksonUtilityException | UnsupportedEncodingException ex) {
            Logger.getLogger(ChatGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        //num_of_users.setText(Integer.toString(chatterDigit));
    }
    
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        msgTextArea = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        convoTextArea = new javax.swing.JTextArea();
        msgBoardTitle = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        roomMenu = new javax.swing.JMenu();
        optionsMenu = new javax.swing.JMenu();
        userJoin = new javax.swing.JLabel();
        num_of_users = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        msgTextArea.setColumns(20);
        msgTextArea.setRows(5);
        jScrollPane1.setViewportView(msgTextArea);
        
        msgTextArea.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent ke) {
//                if((ke.getKeyCode() == KeyEvent.VK_ALT) && (ke.getKeyCode() == KeyEvent.VK_R)){
//                    System.out.println("\n\n!!!!!!" + "yes" + "!!!!!!!!!\n\n");
//                }
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if((ke.getKeyCode() == KeyEvent.VK_F1)){
                    convoTextArea.setText("");
                }
                else if(ke.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        chatter.sendToServer();
                        ke.consume();
                    } catch (FirebaseException | JacksonUtilityException | UnsupportedEncodingException ex) {
                        Logger.getLogger(ChatGui.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent ke) {
            
            }            
        });

        sendButton.setText("Send");
        
        
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChatRoom chatter = new ChatRoom();
                try {
                    chatter.sendButtonActionPerformed(e);
                } catch (FirebaseException | JacksonUtilityException | UnsupportedEncodingException ex) {
                    Logger.getLogger(ChatGui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        convoTextArea.setColumns(20);
        convoTextArea.setRows(5);
        convoTextArea.setEditable(false);
        jScrollPane2.setViewportView(convoTextArea);   
        
        msgBoardTitle.setText("Anonymous Message Board");
        userJoin.setText("Users joined: ");
//        newNum = Integer.toString(chatterDigit);
//        num_of_users.setText(newNum);

        roomMenu.setText("Room");
        jMenuBar1.add(roomMenu);
        
        optionsMenu.setText("Options");
        jMenuBar1.add(optionsMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendButton))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(msgBoardTitle)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                            .addComponent(userJoin)
                        .addComponent(num_of_users)))
                    
                .addGap(0, 23, Short.MAX_VALUE))
                //.addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(msgBoardTitle)
                    .addComponent(userJoin)
                    .addComponent(num_of_users))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>    
    
    public javax.swing.JButton sendButton;
    private javax.swing.JLabel msgBoardTitle;
    private javax.swing.JLabel userJoin;
    public javax.swing.JLabel num_of_users;
    private javax.swing.JMenu roomMenu;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JTextArea msgTextArea;
    public javax.swing.JTextArea convoTextArea;
}
