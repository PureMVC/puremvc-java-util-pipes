/*
 PureMVC Java/MultiCore Utility – Pipes
 Your reuse is governed by the Creative Commons Attribution 3.0 License
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeListener;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

/**
 * Pipe Listener.
 * <P>
 * Allows a class that does not implement <code>IPipeFitting</code> to
 * be the final recipient of the messages in a pipeline.</P>
 * 
 * @see Junction
 */ 
public class PipeListener implements IPipeFitting {

	private IPipeListener listener;
	
	public PipeListener(IPipeListener listener) {
		this.listener = listener;
	}
	
	/**
	 *  Can't connect anything beyond this.
	 */
	public boolean connect(IPipeFitting output) {
		return false;
	}

	/**
	 *  Can't disconnect since you can't connect, either.
	 */
	public IPipeFitting disconnect() {
		return null;
	}
	
	//	 Write the message to the listener
	public boolean write(IPipeMessage message) {
		listener.handlePipeMessage(message);
		return true;
	}

}
