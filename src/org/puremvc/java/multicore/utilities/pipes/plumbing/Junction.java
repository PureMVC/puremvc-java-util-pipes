/* 
 PureMVC Java MultiCore Pipes Utility Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Matthieu Mauny <matthieu.mauny@puremvc.org>
 And Anthony Quinault <aquinault@gmail.com>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeListener;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

/**
 * Pipe Junction.
 * 
 * <P>
 * Manages Pipes for a Module. 
 * 
 * <P>
 * When you register a Pipe with a Junction, it is 
 * declared as being an INPUT pipe or an OUTPUT pipe.</P> 
 * 
 * <P>
 * You can retrieve or remove a registered Pipe by name, 
 * check to see if a Pipe with a given name exists,or if 
 * it exists AND is an INPUT or an OUTPUT Pipe.</P> 
 * 
 * <P>
 * You can send an <code>IPipeMessage</code> on a named INPUT Pipe 
 * or add a <code>PipeListener</code> to registered INPUT Pipe.</P>
 */
public class Junction {
	/**
	 *  INPUT Pipe Type
	 */
	public static final String INPUT = "input";
	/**
	 *  OUTPUT Pipe Type
	 */
	public static final String OUTPUT = "output";
	
	/**
	 *  The names of the INPUT pipes
	 */
	protected  Collection<String> inputPipes = new ArrayList<String>();
	
	/**
	 *  The names of the OUTPUT pipes
	 */
	protected List<String> outputPipes = new ArrayList<String>();
	
	/** 
	 * The map of pipe names to their pipes
	 */
	protected Map<String, IPipeFitting> pipesMap = new HashMap<String, IPipeFitting>();
	
	/**
	 * The map of pipe names to their types
	 */
	protected Map<String, String> pipeTypesMap = new HashMap<String, String>();
	
	// Constructor. 
	public Junction( )
	{
	}
	
	/**
	 * Register a pipe with the junction.
	 * <P>
	 * Pipes are registered by unique name and type,
	 * which must be either <code>Junction.INPUT</code>
	 * or <code>Junction.OUTPUT</code>.</P>
		 * <P>
	 * NOTE: You cannot have an INPUT pipe and an OUTPUT
	 * pipe registered with the same name. All pipe names
	 * must be unique regardless of type.</P>
	 * 
	 * @return Boolean true if successfully registered. false if another pipe exists by that name.
	 */
	public Boolean registerPipe( String name, String type, IPipeFitting pipe )
	{ 
		Boolean success = true;
		if ( pipesMap.get(name) == null )
		{
			pipesMap.put(name, pipe);
			pipeTypesMap.put(name ,type);
			if (type.equals(INPUT)){
				inputPipes.add(name);	
			}else if (type.equals(OUTPUT)){
				outputPipes.add(name);	
			} else {
				success=false;
			}
		} else {
			success=false;
		}
		return success;
	}
	
	/**
	 * Does this junction have a pipe by this name?
	 * 
	 * @param name the pipe to check for 
	 * @return Boolean whether as pipe is registered with that name.
	 */ 
	public Boolean hasPipe( String name)
	{
		return ( pipesMap.containsKey(name));
	}
	
	/**
	 * Does this junction have an INPUT pipe by this name?
	 * 
	 * @param name the pipe to check for 
	 * @return Boolean whether an INPUT pipe is registered with that name.
	 */ 
	public Boolean hasInputPipe( String name )
	{
		return ( hasPipe(name) && (pipeTypesMap.get(name) == INPUT) );
	}
	
	/**
	 * Does this junction have an OUTPUT pipe by this name?
	 * 
	 * @param name the pipe to check for 
	 * @return Boolean whether an OUTPUT pipe is registered with that name.
	 */ 
	public Boolean hasOutputPipe( String name )
	{
		return ( hasPipe(name) && (pipeTypesMap.get(name) == OUTPUT) );
	}
	
	/**
	 * Remove the pipe with this name if it is registered.
	 * <P>
	 * NOTE: You cannot have an INPUT pipe and an OUTPUT
	 * pipe registered with the same name. All pipe names
	 * must be unique regardless of type.</P>
	 * 
	 * @param name the pipe to remove
	 */
	public void removePipe( String name)
	{
		if ( hasPipe(name) ) 
		{
			String type = pipeTypesMap.get(name);
			if(type.equals(INPUT)) {
				inputPipes.remove(name);
			}
			if(type.equals(OUTPUT)) {
				outputPipes.remove(name);
			}
			pipesMap.remove(name);
			pipeTypesMap.remove(name);
		}
	}
	
	/**
	 * Retrieve the named pipe.
	 * 
	 * @param name the pipe to retrieve
	 * @return IPipeFitting the pipe registered by the given name if it exists
	 */
	public IPipeFitting retrievePipe( String name ) 
	{
		return pipesMap.get(name);
	}
	
	/**
	 * Add a PipeListener to an INPUT pipe.
	 * <P>
	 * NOTE: there can only be one PipeListener per pipe,
	 * and the listener function must accept an IPipeMessage
	 * as its sole argument.</P> 
	 * 
	 * @param name the INPUT pipe to add a PipeListener to
	 * @param context the calling context or 'this' object  
	 * @param listener the function on the context to call
	 */
	public Boolean addPipeListener( String inputPipeName, IPipeListener listener ) 
	{
		Boolean success = false;
		if ( hasInputPipe(inputPipeName) )
		{
			IPipeFitting pipe = pipesMap.get(inputPipeName);
			success = pipe.connect( new PipeListener(listener) );
		}
		return success;
	}
	
	/**
	 * Send a message on an OUTPUT pipe.
	 * 
	 * @param name the OUTPUT pipe to send the message on
	 * @param message the IPipeMessage to send  
	 */
	public Boolean sendMessage( String outputPipeName, IPipeMessage message)
	{
		Boolean success=false;
		if ( hasOutputPipe(outputPipeName) )
		{
			IPipeFitting pipe = pipesMap.get(outputPipeName);
			success = pipe.write(message);
		} 
		return success;
	}
}
