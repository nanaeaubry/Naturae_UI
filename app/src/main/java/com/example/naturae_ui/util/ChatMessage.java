package com.example.naturae_ui.util;

/**
 * Represents a single message sent by a user or friend
 */
public class ChatMessage {
    private String messageBody;
    //timestamp is formed by toString() call of java.sql.Timestamp
    private String timestamp;
    private String name;
    private boolean isSentByUser;

    /**
     * Initalize a text message that was received from the server-end chatlog
     * @param messageBody
     * @param name
     * @param timestamp
     * @param isSentByUser
     */
    public ChatMessage(String messageBody, String name, String timestamp, boolean isSentByUser){
        this.messageBody = messageBody;
        //this.timestamp = timestamp;
        this.isSentByUser = isSentByUser;
        this.name = name;
    }

    /**
     * @return the text body of the message as a String
     */
    public String getMessageBody(){
        return messageBody;
    }

    /**
     *
     * @return the username of the message sender
     */
    public String getName(){
        return name;
    }

    /**
     * @return the recorded date-time of when the message was sent
     */
    public String getTimestamp(){
        return timestamp;
    }

    /**
     * @return the recorded date-time of when the message was sent
     */
    public boolean isSentByUser() {
        return isSentByUser;
    }
}
