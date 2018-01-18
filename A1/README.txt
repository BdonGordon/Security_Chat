*****************************************************
Readme.txt
*****************************************************
Name: Brandon Gordon 
Student ID: 0850874
Course: CIS*4110 Computer Security
Professor: D. Rozita
Assignment: 1
*****************************************************

****************
EXECUTION
****************
Unzip .zip folder then change directory into "dist" directory inside the unzipped folder.
The .jar file is present in this directory named "ChatRoom". 
Double click on ChatRoom and a Gui should appear.
Please note that wifi/internet access is mandatory for the program to work.

****************
DESIGN STRATEGY
****************
The Chatserver ...
For the chat server, my objective was to find a way to store messages onto an established online server as recommended by classmates. Firebase was the server I decided to use. Firebase is essentially a database that allows you to create a database and use a hyperlink to connect to it. It allows you to send and receive any type of information the user desires. With this in mind, I knew I had to accomplish simple steps in order to firstly send and retrieve character strings to and from the server: 
1) Connect to Firebase database 
2) Get user input (string)
3) Send the user input into the database server and store it into a local java string array
4) Once the input is stored into the databse, retrieve and print it

Once that was done, I implemented the GUI before anything else. After that, I had to think about how several users can communicate with eachother. As a result, I created an integer variable on the database that would incrememt by one if a message was sent. If that condition was true a self-created Timer function, that runs every 1 second, would detect that and update the users' GUIs to recieve the most recent message. When a message is sent, I append it to the conversation JTextArea and stored the entire conversation in the Firebase database.
Everytime a user enters the conversation program, they are updated with the conversation that previously occurred before they entered. This was done by simply appending the Firebase messages into the conversation JTextArea.

The Anonymity ...
When the program is executed, the user is prompted with a pop-up window to input a username. I take this username and pass it through a function that would encrypt it with random characters. The GUI message textarea would notify any of the users currently in the chat that an anonymous user, displayed with the encrypted name, has joined. 

****************
RESOURCES
****************
1. Firebase -> https://firebase.google.com/
2. Encryption -> http://stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java
3. Pop-up dialogs -> http://stackoverflow.com/questions/8689122/joptionpane-yes-no-options-confirm-dialog-box-issue-java
4. Enter key submit strings -> http://stackoverflow.com/questions/25252558/javafx-how-to-make-enter-key-submit-textarea

****************
BONUS FEATURES
****************
1. Customize colour of the conversation JTextAreas via menu item "Customiz" under "Options"
2. F1 Key to clear the conversation JTextArea
3. Clear Screen with "Clear Screen" menuitem under "Options"
4. ENTER key to send message 
5. Timestamp is included with every message sent
6. Shows number of users in the conversation program
7. When you exit the program, it prompts the user to save the conversation history. 
****NOTE: The History text is saved in the current directory (dist) called Chat_History******