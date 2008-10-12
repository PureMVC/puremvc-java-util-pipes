/* 
 PureMVC Java MultiCore Pipes Utility Port by Matthieu Mauny <matthieu.mauny@puremvc.org> 
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;
import org.puremvc.java.multicore.utilities.pipes.messages.QueueControlMessage;

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
	protected Vector<IPipeMessage> messages = new Vector<IPipeMessage>();
	
	
	public Queue( IPipeFitting output)
	{
		super( output );
	}
	
	public Queue()
	{
		super( null );
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
	public boolean write( IPipeMessage message )
	{
		boolean success = true;
		// Store normal messages
		if (message.getType().equals(Message.NORMAL))
			this.store(message);

		// Flush the queue
		if (message.getType().equals(QueueControlMessage.FLUSH))
			success = this.flush();

		// Put Queue into Priority Sort or FIFO mode
		// Subsequent messages written to the queue
		// will be affected. Sorted messages cannot
		// be put back into FIFO order!
		if (message.getType().equals(QueueControlMessage.SORT) || message.getType().equals(QueueControlMessage.FIFO))
			mode = message.getType();
		return success;
	} 
	
	/**
	 * Store a message.
	 * 
	 * @param message
	 *            the IPipeMessage to enqueue.
	 * @return int the new count of messages in the queue
	 */
	protected void store(IPipeMessage message )
	{
		messages.add( message );
		if (mode.equals(QueueControlMessage.SORT) ) 
			Collections.sort(messages, new Comparator<IPipeMessage>(){
				public int compare(IPipeMessage msgA, IPipeMessage msgB) {
					int num = 0;
					if ( msgA.getPriority() < msgB.getPriority() ) num = -1;
					if ( msgA.getPriority() > msgB.getPriority() ) num = 1;
					return num;
				}});
	}
	
	/**
	 * Flush the queue.
	 * <P>
	 * NOTE: This empties the queue.</P>
	 * @return Boolean true if all messages written successfully.
	 */
	protected boolean flush()
	{
		boolean success=true;
		while (!messages.isEmpty() ) 
		{
			IPipeMessage message = (IPipeMessage)messages.firstElement();
			boolean ok = output.write( message );
			messages.remove(message);
			if ( !ok ) success = false;
		} 
		return success;
	}

}
