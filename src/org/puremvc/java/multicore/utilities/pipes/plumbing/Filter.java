/* 
 PureMVC Java MultiCore Pipes Utility Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Anthony Quinault <anthony.quinault@puremvc.org>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IFilter;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.FilterControlMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

/**
 * Pipe Filter.
 * <P>
 * Filters may modify the contents of messages before writing them to 
 * their output pipe fitting. They may also have their parameters and
 * filter function passed to them by control message, as well as having
 * their Bypass/Filter operation mode toggled via control message.</p>  
 */ 
public class Filter extends Pipe {
	
	protected String mode = FilterControlMessage.FILTER;
	protected IFilter filter;
	protected Object params = null;
	protected String name;

	/**
	 * Constructor.
	 * <P>
	 * Optionally connect the output and set the parameters.</P>
	 */
	public Filter( String name, IPipeFitting output, IFilter filter, Object params ) 
	{
		super( output );
		this.name = name;
		if ( filter != null ) setFilter( filter );
		if ( params != null ) setParams( params );
	}
	
	/**
	 * Constructor.
	 * <P>
	 * Optionally connect the output and set the parameters.</P>
	 */
	public Filter( String name) 
	{
		super( null );
		this.name = name;
	}

	/**
	 * Handle the incoming message.
	 * <P>
	 * If message type is normal, filter the message (unless in BYPASS mode) and
	 * write the result to the output pipe fitting if the filter operation is
	 * successful.
	 * </P>
	 * 
	 * <P>
	 * The FilterControlMessage.SET_PARAMS message type tells the Filter that
	 * the message class is FilterControlMessage, which it casts the message to
	 * in order to retrieve the filter parameters object if the message is
	 * addressed to this filter.
	 * </P>
	 * 
	 * <P>
	 * The FilterControlMessage.SET_FILTER message type tells the Filter that
	 * the message class is FilterControlMessage, which it casts the message to
	 * in order to retrieve the filter function.
	 * </P>
	 * 
	 * <P>
	 * The FilterControlMessage.BYPASS message type tells the Filter that it
	 * should go into Bypass mode operation, passing all normal messages through
	 * unfiltered.
	 * </P>
	 * 
	 * <P>
	 * The FilterControlMessage.FILTER message type tells the Filter that it
	 * should go into Filtering mode operation, filtering all normal normal
	 * messages before writing out. This is the default mode of operation and so
	 * this message type need only be sent to cancel a previous BYPASS message.
	 * </P>
	 * 
	 * <P>
	 * The Filter only acts on the control message if it is targeted to this
	 * named filter instance. Otherwise it writes through to the output.
	 * </P>
	 * 
	 * @return Boolean True if the filter process does not throw an error and
	 *         subsequent operations in the pipeline succede.
	 */
	public boolean write( IPipeMessage message)
	{
		IPipeMessage outputMessage;
		boolean success = true;

		// Filter normal messages
		if (message.getType().equals(Message.NORMAL)) {
			try {
				if (mode == FilterControlMessage.FILTER) {
					outputMessage = applyFilter(message);
				} else {
					outputMessage = message;
				}
				success = output.write(outputMessage);
			} catch (Exception e) {
				success = false;
			} catch (Error e) {
				success = false;
			}
		} else{
			
		// Accept parameters from control message
		if (message.getType().equals(FilterControlMessage.SET_PARAMS)) {
			if (isTarget(message)) {
				setParams(filterControleMessage(message).getParams());
			} else {
				success = output.write(message);
			}
		} else{

		// Accept filter function from control message
		if (message.getType().equals(FilterControlMessage.SET_FILTER)) {
			if (isTarget(message)) {
				setFilter(filterControleMessage(message).getFilter());
			} else {
				success = output.write(message);
			}

		} else{ 

		// Toggle between Filter or Bypass operational modes
		if (message.getType().equals(FilterControlMessage.BYPASS) || 
			message.getType().equals(FilterControlMessage.FILTER)){
			if (isTarget(message)) {
				mode = filterControleMessage(message).getType();
			} else {
				success = output.write(message);
			}
		} else{
			// Write control messages for other fittings through
			success = output.write( message );
		}}}}
		
		return success;
	}

	/**
	 * Cast a Message into a FilterControlMessage Emulate AS3
	 */
	protected FilterControlMessage filterControleMessage(IPipeMessage msg){
		return (FilterControlMessage)msg;
	}
	
	/**
	 * Is the message directed at this filter instance?
	 */
	protected boolean isTarget(IPipeMessage msg)
	{
		return ( filterControleMessage(msg).getName() == this.name );
	}
	
	/**
	 * Set the Filter parameters.
	 * <P>
	 * This can be an object can contain whatever arbitrary 
	 * properties and values your filter method requires to
	 * operate.</P>
	 * 
	 * @param params the parameters object
	 */
	public void setParams( Object params )
	{
		this.params = params;
	}

	/**
	 * Set the Filter function.
	 * <P>
	 * It must accept two arguments; an IPipeMessage, 
	 * and a parameter Object, which can contain whatever 
	 * arbitrary properties and values your filter method 
	 * requires.</P>
	 * 
	 * @param filter the filter function. 
	 */
	public void setFilter( IFilter filter )
	{
		this.filter = filter;
	}
	
	/**
	 * Filter the message.
	 */
	protected IPipeMessage applyFilter( IPipeMessage message )
	{
		message = filter.apply(message, params);
		return message;
	}
}
