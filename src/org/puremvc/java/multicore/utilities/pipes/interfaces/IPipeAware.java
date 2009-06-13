/* 
 PureMVC Java MultiCore Pipes Utility Port by Ima OpenSource <opensource@ima.eu>
 Maintained by Anthony Quinault <anthony.quinault@puremvc.org>
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved. 
 Your reuse is governed by the Creative Commons Attribution 3.0 License 
 */
package org.puremvc.java.multicore.utilities.pipes.interfaces;



/**
 * Pipe Aware interface.
 * <P>
 * Can be implemented by any PureMVC Core that wishes
 * to communicate with other Cores using the Pipes 
 * utility.</P>
 */
public interface IPipeAware {
	void acceptInputPipe(String name,IPipeFitting pipe);
	void acceptOutputPipe(String name,IPipeFitting pipe);
}