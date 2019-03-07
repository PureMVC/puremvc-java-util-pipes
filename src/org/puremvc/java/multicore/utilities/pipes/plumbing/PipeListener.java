//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

import java.util.function.Consumer;

/**
 * Pipe Listener.
 * <P>
 * Allows a class that does not implement <code>IPipeFitting</code> to
 * be the final recipient of the messages in a pipeline.</P>
 *
 * @see Junction
 */
public class PipeListener implements IPipeFitting {

    private Object context;

    private Consumer<IPipeMessage> listener;

    public PipeListener(Object context, Consumer<IPipeMessage> listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     *  Can't connect anything beyond this.
     */
    public boolean connect(IPipeFitting output) {
        return false;
    }

    /**
     *  Can't disconnect since you can't connect, either.
     */
    public IPipeFitting disconnect() {
        return null;
    }

    // Write the message to the listener
    public boolean write(IPipeMessage message) {
        listener.accept(message);
        return true;
    }
}
