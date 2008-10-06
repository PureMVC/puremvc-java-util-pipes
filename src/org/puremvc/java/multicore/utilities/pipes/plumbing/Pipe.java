/*
 PureMVC Java/MultiCore Utility – Pipes
 Your reuse is governed by the Creative Commons Attribution 3.0 License
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

/**
 * Pipe.
 * <P>
 * This is the most basic <code>IPipeFitting</code>,
 * simply allowing the connection of an output
 * fitting and writing of a message to that output.</P>
 */	
public class Pipe implements IPipeFitting {
	protected IPipeFitting output;
	
	public Pipe( IPipeFitting output)
	{
		if (output != null) connect(output);
	}
	
	public Pipe()
	{
		output = null;
	}

	/**
	 * Connect another PipeFitting to the output.
	 * 
	 * PipeFittings connect to and write to other 
	 * PipeFittings in a one-way, syncrhonous chain.</P>
	 * 
	 * @return Boolean true if no other fitting was already connected.
	 */
	public boolean connect( IPipeFitting output )
	{
		Boolean success = false;
		if (this.output == null) {
			this.output = output;
			success=true;
		}
		return success;
	}

	/**
	 * Disconnect the Pipe Fitting connected to the output.
	 * <P>
	 * This disconnects the output fitting, returning a 
	 * reference to it. If you were splicing another fitting
	 * into a pipeline, you need to keep (at least briefly) 
	 * a reference to both sides of the pipeline in order to 
	 * connect them to the input and output of whatever 
	 * fiting that you're splicing in.</P>
	 * 
	 * @return IPipeFitting the now disconnected output fitting
	 */
	public IPipeFitting disconnect(  ) 
	{
		IPipeFitting disconnectedFitting = this.output;
		this.output = null;
		return disconnectedFitting;
	}

	/**
	 * Write the message to the connected output.
	 * 
	 * @param message the message to write
	 * @return Boolean whether any connected downpipe outputs failed
	 */
	public boolean write( IPipeMessage message)
	{
		if (this.output == null) { return false; }
		return this.output.write( message );
	}

}
