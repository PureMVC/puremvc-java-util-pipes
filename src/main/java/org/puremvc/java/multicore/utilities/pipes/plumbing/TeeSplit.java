//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2020 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

import java.util.ArrayList;
import java.util.Vector;

/**
 * <P>Splitting Pipe Tee.</P>
 *
 * <P>Writes input messages to multiple output pipe fittings.</P>
 */
public class TeeSplit implements IPipeFitting {

    protected Vector<IPipeFitting> outputs = new Vector<>();

    /**
     * <P>Constructor.</P>
     */
    public TeeSplit() {

    }

    /**
     * <P>Constructor.</P>
     *
     * @param output output pipe
     */
    public TeeSplit(IPipeFitting output) {
        this(output, null);
    }

    /**
     * <P>Constructor.</P>
     *
     * <P>Create the TeeSplit and connect the up two optional outputs.
     * This is the most common configuration, though you can connect
     * as many outputs as necessary by calling <code>connect</code>.</P>
     *
     * @param output1 pipe 1
     * @param output2 pipe 2
     */
    public TeeSplit(IPipeFitting output1, IPipeFitting output2) {
        if(output1 != null) connect(output1);
        if(output2 != null) connect(output2);
    }

    /**
     * <P>Connect the output IPipeFitting.</P>
     *
     * <P>NOTE: You can connect as many outputs as you want
     * by calling this method repeatedly.</P>
     *
     * @param output the IPipeFitting to connect for output.
     */
    public boolean connect(IPipeFitting output) {
        outputs.add(output);
        return true;
    }

    /**
     * <P>Disconnect the most recently connected output fitting. (LIFO)</P>
     *
     * <P>To disconnect all outputs, you must call this
     * method repeatedly untill it returns null.</P>
     *
     */
    public IPipeFitting disconnect() {
        return outputs.isEmpty() ? null : outputs.remove(outputs.size() - 1);
    }

    /**
     * <P>Disconnect a given output fitting.</P>
     *
     * <P>If the fitting passed in is connected
     * as an output of this <code>TeeSplit</code>, then
     * it is disconnected and the reference returned.</P>
     *
     * <P>If the fitting passed in is not connected as an
     * output of this <code>TeeSplit</code>, then <code>null</code>
     * is returned.</P>
     *
     * @param target the IPipeFitting to connect for output.
     * @return disconnected Pipe
     */
    public IPipeFitting disconnectFitting(IPipeFitting target) {
        return outputs.remove(target) ? target : null;
    }

    /**
     * <P>Write the message to all connected outputs.</P>
     *
     * <P>Returns false if any output returns false,
     * but all outputs are written to regardless.</P>
     *
     * @param message the message to write
     * @return Boolean whether any connected outputs failed
     */
    public boolean write(IPipeMessage message) {
        final boolean[] success = {true};
        ArrayList<IPipeFitting> temp = new ArrayList<>(outputs);
        temp.forEach(output -> {
            if(!output.write(message)) success[0] = false;
        });
        return success[0];
    }
}
