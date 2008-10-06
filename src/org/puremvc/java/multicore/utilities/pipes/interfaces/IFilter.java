/* 
 PureMVC Java MultiCore Pipes Utility Port by Matthieu Mauny <matthieu.mauny@puremvc.org> 
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.interfaces;

/**
 * Apply a filter on message.
 * Emulate function pointer  
 */
public interface IFilter {
	IPipeMessage apply(IPipeMessage message, Object params);
}
