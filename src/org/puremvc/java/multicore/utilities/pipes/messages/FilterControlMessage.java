/* 
 PureMVC Java MultiCore Pipes Utility Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Matthieu Mauny <matthieu.mauny@puremvc.org>
 And Anthony Quinault <aquinault@gmail.com>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.messages;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IFilter;


/**
 * Filter Control Message.
 * <P>
 * A special message type for controlling the behavior of a Filter.</P>
 * <P> 
 * The <code>FilterControlMessage.SET_PARAMS</code> message type tells the Filter
 * to retrieve the filter parameters object.</P> 
 * 
 * <P> 
 * The <code>FilterControlMessage.SET_FILTER</code> message type tells the Filter
 * to retrieve the filter function.</P>
 * 
 * <P> 
 * The <code>FilterControlMessage.BYPASS</code> message type tells the Filter
 * that it should go into Bypass mode operation, passing all normal
 * messages through unfiltered.</P>
 * 
 * <P>
 * The <code>FilterControlMessage.FILTER</code> message type tells the Filter
 * that it should go into Filtering mode operation, filtering all
 * normal normal messages before writing out. This is the default
 * mode of operation and so this message type need only be sent to
 * cancel a previous  <code>FilterControlMessage.BYPASS</code> message.</P>
 * 
 * <P>
 * The Filter only acts on a control message if it is targeted 
 * to this named filter instance. Otherwise it writes the message
 * through to its output unchanged.</P>
 */ 
public class FilterControlMessage extends Message {
	/**
	 * Message type base URI
	 */
	protected static final String BASE  = Message.BASE+"/filter/";
	
	/**
	 * Set filter parameters.
	 */ 
	public static final String SET_PARAMS	= BASE+"setparams";
	
	/**
	 * Set filter function.
	 */ 
	public static final String SET_FILTER	= BASE+"setfilter";

	/**
	 * Toggle to filter bypass mode.
	 */
	public static final String BYPASS		= BASE+"bypass";
	
	/**
	 * Toggle to filtering mode. (default behavior).
	 */
	public static final String FILTER 		= BASE+"filter";
	
	
	protected Object params;
	protected IFilter filter;
	protected String name;


	// Constructor
	public FilterControlMessage( String type, String name, IFilter pFilter, Object pParams)
	{
		super( type,null, null, 1 );
		setName( name );
		setFilter( pFilter );
		setParams( pParams );
	}
	
	// Constructor
	public FilterControlMessage( String type, String name, IFilter pFilter)
	{
		super( type,null, null, 1 );
		setName( name );
		setFilter( pFilter );
	}
	
	// Constructor
	public FilterControlMessage( String type, String name)
	{
		super( type,null, null, 1 );
		setName( name );
	}

	/**
	 * Set the target filter name.
	 */
	public void setName( String name) 
	{
		this.name = name;
	}
	
	/**
	 * Get the target filter name.
	 */
	public String getName( )
	{
		return this.name;
	}
	
	/**
	 * Set the filter function.
	 */
	public void setFilter( IFilter filter )
	{
		this.filter = filter;
	}
	
	/**
	 * Get the filter function.
	 */
	public IFilter getFilter( )
	{
		return this.filter;
	}
	
	/**
	 * Set the parameters object.
	 */
	public void setParams( Object params )
	{
		this.params = params;
	}
	
	/**
	 * Get the parameters object.
	 */
	public Object getParams( )
	{
		return this.params;
	}

}
