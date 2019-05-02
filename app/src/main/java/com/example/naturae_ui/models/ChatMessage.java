package com.example.naturae_ui.models;
/**
 * Represents a single message sent by a user or friend
 */
public class ChatMessage {
    private String messageBody;
    private long timestamp;
    private String name;
    private boolean isSentByUser;
    private String timeElapsed;


    /**
     * Initalize a text message that was received from the server-end chatlog
     * @param messageBody
     * @param name
     * @param timestamp
     * @param isSentByUser
     */
    public ChatMessage(String messageBody, String name, long timestamp, boolean isSentByUser){
        this.messageBody = messageBody;
        this.isSentByUser = isSentByUser;
        this.name = name;
        this.timestamp = timestamp;
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
     * @return the recorded date-time of when the message was sent, converts a unix long value into a proper string format
     */
    public long getTimestamp(){
        return timestamp;
    }

    /**
     * Set the amount of time that has passed since the message was sent
     * @param timeElapsed
     */
    public void setTimeElapsed(String timeElapsed){
        this.timeElapsed = timeElapsed;
    }

    /**
     * Return the amount of time that has passes since the message was sent
     * @return
     */
    public String getTimeElapsed(){
        return timeElapsed;
    }

    /**
     * @return the recorded date-time of when the message was sent
     */
    public boolean isSentByUser() {
        return isSentByUser;
    }
}
