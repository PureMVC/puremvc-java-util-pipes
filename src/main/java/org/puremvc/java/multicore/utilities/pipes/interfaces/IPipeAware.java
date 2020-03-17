//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2020 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.interfaces;

/**
 * <P>Pipe Aware interface.</P>
 *
 * <P>Can be implemented by any PureMVC Core that wishes
 * to communicate with other Cores using the Pipes
 * utility.</P>
 */
public interface IPipeAware {
    void acceptInputPipe(String name, IPipeFitting pipe);
    void acceptOutputPipe(String name, IPipeFitting pipe);
}
