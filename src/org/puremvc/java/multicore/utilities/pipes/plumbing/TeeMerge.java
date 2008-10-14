/* 
 PureMVC Java MultiCore Pipes Utility Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Matthieu Mauny <matthieu.mauny@puremvc.org>
 And Anthony Quinault <aquinault@gmail.com>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;

/** 
 * Merging Pipe Tee.
 * <P>
 * Writes the messages from multiple input pipelines into
 * a single output pipe fitting.</P>
 */
public class TeeMerge extends Pipe {

	/**
	 * Constructor.
	 * <P>
	 */
	public TeeMerge(  ) 
	{
		super();
	}
	
	/**
	 * Constructor.
	 * <P>
	 * Create the TeeMerge and the two optional constructor inputs.
	 * This is the most common configuration, though you can connect
	 * as many inputs as necessary by calling <code>connectInput</code>
	 * repeatedly.</P>
	 * <P>
	 * Connect the single output fitting normally by calling the 
	 * <code>connect</code> method, as you would with any other IPipeFitting.</P>
	 */
	public TeeMerge( IPipeFitting input1, IPipeFitting input2 ) 
	{
		if (input1!=null) connectInput(input1);
		if (input2!=null) connectInput(input2);
	}
	
	
	/** 
	 * Connect an input IPipeFitting.
	 * <P>
	 * NOTE: You can connect as many inputs as you want
	 * by calling this method repeatedly.</P>
	 * 
	 * @param input the IPipeFitting to connect for input.
	 */
	public Boolean connectInput( IPipeFitting input )
	{
		return input.connect(this);
	}
}
