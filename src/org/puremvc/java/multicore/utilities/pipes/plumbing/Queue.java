//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;
import org.puremvc.java.multicore.utilities.pipes.messages.QueueControlMessage;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Pipe Queue.
 * <P>
 * The Queue always stores inbound messages until you send it
 * a FLUSH control message, at which point it writes its buffer
 * to the output pipe fitting. The Queue can be sent a SORT
 * control message to go into sort-by-priority mode or a FIFO
 * control message to cancel sort mode and return the
 * default mode of operation, FIFO.</P>
 *
 * <P>
 * NOTE: There can effectively be only one Queue on a given
 * pipeline, since the first Queue acts on any queue control
 * message. Multiple queues in one pipeline are of dubious
 * use, and so having to name them would make their operation
 * more complex than need be.</P>
 */
public class Queue extends Pipe {

    protected String mode = QueueControlMessage.SORT;

    protected Vector<IPipeMessage> messages = new Vector<>();

    public Queue(){

    }

    public Queue(IPipeFitting output) {
        super(output);
    }

    /**
     * Handle the incoming message.
     * <P>
     * Normal messages are enqueued.</P>
     * <P>
     * The FLUSH message type tells the Queue to write all
     * stored messages to the ouptut PipeFitting, then
     * return to normal enqueing operation.</P>
     * <P>
     * The SORT message type tells the Queue to sort all
     * <I>subsequent</I> incoming messages by priority. If there
     * are unflushed messages in the queue, they will not be
     * sorted unless a new message is sent before the next FLUSH.
     * Sorting-by-priority behavior continues even after a FLUSH,
     * and can be turned off by sending a FIFO message, which is
     * the default behavior for enqueue/dequeue.</P>
     */
    public boolean write(IPipeMessage message) {
        boolean success = true;

        switch (message.getType()) {
            // Store normal messages
            case Message.NORMAL:
                this.store(message);
                break;

            // Flush the queue
            case QueueControlMessage.FLUSH:
                success = this.flush();
                break;

            // Put Queue into Priority Sort or FIFO mode
            // Subsequent messages written to the queue
            // will be affected. Sorted messages cannot
            // be put back into FIFO order!
            case QueueControlMessage.SORT:
            case QueueControlMessage.FIFO:
                mode = message.getType();
                break;
        }
        return success;
    }

    /**
     * Sort the Messages by priority.
     *
     * @param msgA message A
     * @param msgB message B
     *
     * @return int for priority indication, -1 less than, 0 equal, 1 greater than
     */
    protected int sortMessagesByPriority(IPipeMessage msgA, IPipeMessage msgB) {
        int num = 0;
        if(msgA.getPriority() < msgB.getPriority()) num = -1;
        if(msgA.getPriority() > msgB.getPriority()) num = 1;
        return num;
    }

    /**
     * Store a message.
     * @param message the IPipeMessage to enqueue.
     */
    protected void store(IPipeMessage message) {
        messages.add(message);
        if(mode == QueueControlMessage.SORT) {
            messages.sort(this::sortMessagesByPriority);
        }
    }

    /**
     * Flush the queue.
     * <P>
     * NOTE: This empties the queue.</P>
     * @return Boolean true if all messages written successfully.
     */
    protected boolean flush() {
        final boolean[] success = {true};
        ArrayList<IPipeMessage> temp = new ArrayList<>(messages);
        temp.forEach(msg -> {
            if(!output.write(msg)) success[0] = false;
            messages.remove(0);
        });
        return success[0];
    }
}
