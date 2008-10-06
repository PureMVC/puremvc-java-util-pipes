/*
 PureMVC Java/MultiCore Utility – Pipes
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
