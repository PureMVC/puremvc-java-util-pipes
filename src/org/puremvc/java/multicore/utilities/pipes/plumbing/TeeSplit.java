/* 
 PureMVC Java MultiCore Pipes Utility Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Anthony Quinault <anthony.quinault@puremvc.org>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import java.util.ArrayList;
import java.util.List;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

/** 
 * Splitting Pipe Tee.
 * <P>
 * Writes input messages to multiple output pipe fittings.</P>
 */
public class TeeSplit implements IPipeFitting {

	protected List<IPipeFitting> outputs = new ArrayList<IPipeFitting>();

	/**
	 * Constructor.
	 */
	public TeeSplit() 
	{
		super();
	}
	
	
	/**
	 * Constructor.
	 * <P>
	 * Create the TeeSplit and connect the up two optional outputs.
	 * This is the most common configuration, though you can connect
	 * as many outputs as necessary by calling <code>connect</code>.</P>
	 */
	public TeeSplit(IPipeFitting output1, IPipeFitting output2 ) 
	{
		if (output1!=null) connect(output1);
		if (output2!=null) connect(output2);
	}
	
	/** 
	 * Connect the output IPipeFitting.
	 * <P>
	 * NOTE: You can connect as many outputs as you want
	 * by calling this method repeatedly.</P>
	 * 
	 * @param output the IPipeFitting to connect for output.
	 */
	public boolean connect(IPipeFitting output )
	{
		outputs.add(output);
		return true;
	}
	
	/** 
	 * Disconnect the most recently connected output fitting. (LIFO)
	 * <P>
	 * To disconnect all outputs, you must call this 
	 * method repeatedly untill it returns null.</P>
	 * 
	 * @param output the IPipeFitting to connect for output.
	 */
	public IPipeFitting disconnect( )
	{
		if(outputs.isEmpty()){
			return null;
		} else {
			return outputs.remove(outputs.size()-1);
		}
		
	}
	
	/** 
	 * Disconnect an output fitting.
	 * 
	 * @param output the IPipeFitting to disconnect for output.
	 * @return true if this list contained the specified element.
	 */
	public boolean disconnect(IPipeFitting output)
	{
		return outputs.remove(output);
	}

	/**
	 * Write the message to all connected outputs.
	 * <P>
	 * Returns false if any output returns false, 
	 * but all outputs are written to regardless.</P>
	 * @param message the message to write
	 * @return Boolean whether any connected outputs failed
	 */
	public boolean write( IPipeMessage message )
	{
		boolean success = true;
		//A copy is made to avoid comodification exception
		Object[] tempArray = outputs.toArray();
		for (int i = 0; i < tempArray.length; i++) {
			IPipeFitting element =  (IPipeFitting)tempArray[i];
			if (! element.write( message ) ) success = false;
		}
		return success;			
	}

}
