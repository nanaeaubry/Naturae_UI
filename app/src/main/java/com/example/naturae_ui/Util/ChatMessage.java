package com.example.naturae_ui.Util;

/**
 * Represents a single message sent by a user or friend
 */
public class ChatMessage {
    private String text;
    //timestamp is formed by toString() call of java.sql.Timestamp
    private String timestamp;
    private String name;
    private boolean isSentByUser;

    /**
     * Initalize message data
     * @param text
     * @param timestamp
     */
    public ChatMessage(String text, String name, String timestamp, boolean isSentByUser){
        this.text = text;
        this.timestamp = timestamp;
        this.isSentByUser = isSentByUser;
        this.name = name;
    }

    /**
     * @return the text body of the message as a String
     */
    public String getText(){
        return text;
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
