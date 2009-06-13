/* 
 PureMVC Java MultiCore Pipes Utility Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Anthony Quinault <anthony.quinault@puremvc.org>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.mediator.Mediator;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeListener;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

/**
 * Junction Mediator.
 * <P>
 * A base class for handling the Pipe Junction in an IPipeAware 
 * Core.</P>
 */
public class JunctionMediator extends Mediator implements IPipeListener {

	/**
	 * Accept input pipe notification name constant.
	 */ 
    public static final String ACCEPT_INPUT_PIPE 	= "acceptInputPipe";
	
	/**
	 * Accept output pipe notification name constant.
	 */ 
    public static final String ACCEPT_OUTPUT_PIPE 	= "acceptOutputPipe";
    
	/**
	 * Constructor.
	 */
	public JunctionMediator( String name, Junction viewComponent )
	{
		super( name, viewComponent );
	}
	
	/**
	 * List Notification Interests.
	 * <P>
	 * Returns the notification interests for this base class.
	 * Override in subclass and call <code>super.listNotificationInterests</code>
	 * to get this list, then add any sublcass interests to 
	 * the array before returning.</P>
	 */
	public String[] listNotificationInterests()
	{
		return new String[] { JunctionMediator.ACCEPT_INPUT_PIPE, 
		         			  JunctionMediator.ACCEPT_OUTPUT_PIPE
							};	
	}
	
	/**
	 * Handle Notification.
	 * <P>
	 * This provides the handling for common junction activities. It 
	 * accepts input and output pipes in response to <code>IPipeAware</code>
	 * interface calls.</P>
	 * <P>
	 * Override in subclass, and call <code>super.handleNotification</code>
	 * if none of the subclass-specific notification names are matched.</P>
	 */
	public void handleNotification(INotification note)
	{
		// accept an input pipe
		// register the pipe and if successful
		// set this mediator as its listener
		if (note.getName().equals(JunctionMediator.ACCEPT_INPUT_PIPE)) {
			String inputPipeName = note.getType();
			IPipeFitting inputPipe = (IPipeFitting) note.getBody();
			if (getJunction().registerPipe(inputPipeName, Junction.INPUT, inputPipe)) {
				getJunction().addPipeListener(inputPipeName, this);
			}
		}

		// accept an output pipe
		if (note.getName().equals(JunctionMediator.ACCEPT_OUTPUT_PIPE)) {
			String outputPipeName = note.getType();
			IPipeFitting outputPipe = (IPipeFitting) note.getBody();
			getJunction().registerPipe(outputPipeName, Junction.OUTPUT, outputPipe);
		}
	}
	
	/**
	 * Handle incoming pipe messages.
	 * <P>
	 * Override in subclass and handle messages appropriately for the module.</P>
	 */
	public void handlePipeMessage( IPipeMessage message)
	{
	}
	
	/**
	 * The Junction for this Module.
	 */
	protected Junction getJunction()
	{
		return (Junction)getViewComponent();
	}
}
