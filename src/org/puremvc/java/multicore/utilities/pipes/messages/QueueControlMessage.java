/* 
 PureMVC Java MultiCore Pipes Utility Port by Matthieu Mauny <matthieu.mauny@puremvc.org> 
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.messages;

/**
 * Queue Control Message.
 * <P>
 * A special message for controlling the behavior of a Queue.</P>
 * <P>
 * When written to a pipeline containing a Queue, the type
 * of the message is interpreted and acted upon by the Queue.</P>
 * <P>
 * Unlike filters, multiple serially connected queues aren't 
 * very useful and so they do not require a name. If multiple
 * queues are connected serially, the message will be acted 
 * upon by the first queue only.</P>
 */ 
public class QueueControlMessage extends Message {
	
	protected static final String BASE = Message.BASE+"/queue/"; 

	/**
	 * Flush the queue.
	 */
	public static final String FLUSH 	= BASE+"flush";
	
	/**
	 * Toggle to sort-by-priority operation mode.
	 */
	public static final String SORT	= BASE+"sort";
	
	/**
	 * Toggle to FIFO operation mode (default behavior).
	 */
	public static final String FIFO	= BASE+"fifo";

	// Constructor
	public QueueControlMessage( String type )
	{
		super( type, null, null,1);
	}
}
