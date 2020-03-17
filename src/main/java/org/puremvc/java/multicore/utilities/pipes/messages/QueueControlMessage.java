//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2020 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.messages;

/**
 * <P>Queue Control Message.</P>
 *
 * <P>A special message for controlling the behavior of a Queue.</P>
 *
 * <P>When written to a pipeline containing a Queue, the type
 * of the message is interpreted and acted upon by the Queue.</P>
 *
 * <P>Unlike filters, multiple serially connected queues aren't
 * very useful and so they do not require a name. If multiple
 * queues are connected serially, the message will be acted
 * upon by the first queue only.</P>
 */
public class QueueControlMessage extends Message {

    protected static final String Base = Message.BASE + "queue/";

    /**
     * <P>Flush the queue.</P>
     */
    public static final String FLUSH = Base + "flush";

    /**
     * <P>Toggle to sort-by-priority operation mode.</P>
     */
    public static final String SORT = Base + "sort";

    /**
     * <P>Toggle to FIFO operation mode (default behavior).</P>
     */
    public static final String FIFO = Base + "fifo";

    // Constructor
    public QueueControlMessage(String type) {
        super(type);
    }
}
