//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.messages;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

/**
 * Pipe Message.
 * <P>
 * Messages travelling through a Pipeline can
 * be filtered, and queued. In a queue, they may
 * be sorted by priority. Based on type, they
 * may used as control messages to modify the
 * behavior of filter or queue fittings connected
 * to the pipleline into which they are written.</P>
 */
public class Message implements IPipeMessage {

    // High priority Messages can be sorted to the front of the queue
    public static final int PRIORITY_HIGH = 1;

    // Medium priority Messages are the default
    public static final int PRIORITY_MED = 5;

    // Low priority Messages can be sorted to the back of the queue
    public static final int PRIORITY_LOW = 10;

    /**
     * Normal Message type.
     */
    protected static final String BASE = "http://puremvc.org/namespaces/pipes/messages/";

    public static final String NORMAL = BASE + "normal/";

    // TBD: Messages in a queue can be sorted by priority.
    protected int priority;

    // Messages can be handled differently according to type
    protected String type;

    // Header properties describe any meta data about the message for the recipient
    protected Object header;

    // Body of the message is the precious cargo
    protected Object body;

    // Constructor
    public Message(String type, Object header, Object body, int priority) {
        setType(type);
        setHeader(header);
        setBody(body);
        setPriority(priority);
    }

    // Constructor
    public Message(String type, Object header, Object body) {
        this(type, header, body, PRIORITY_MED);
    }

    // Constructor
    public Message(String type, Object header) {
        this(type, header, null, PRIORITY_MED);
    }

    // Constructor
    public Message(String type) {
        this(type, null, null, PRIORITY_MED);
    }

    // Get the type of this message
    public String getType() {
        return type;
    }

    // Set the type of this message
    public void setType(String type) {
        this.type = type;
    }

    // Get the priority of this message
    public int getPriority() {
        return priority;
    }

    // Set the priority of this message
    public void setPriority(int priority) {
        this.priority = priority;
    }

    // Get the header of this message
    public Object getHeader() {
        return header;
    }

    // Set the header of this message
    public void setHeader(Object header) {
        this.header = header;
    }

    // Get the body of this message
    public Object getBody() {
        return body;
    }

    // Set the body of this message
    public void setBody(Object body) {
        this.body = body;
    }
}
