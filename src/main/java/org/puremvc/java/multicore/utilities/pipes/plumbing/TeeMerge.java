//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2020 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;

/**
 * <P>Merging Pipe Tee.
 *
 * <P>Writes the messages from multiple input pipelines into
 * a single output pipe fitting.</P>
 */
public class TeeMerge extends Pipe {

    /**
     * <P>Constructor.</P>
     */
    public TeeMerge() {

    }

    /**
     * <P>Constructor.</P>
     *
     * @param input input pipe
     */
    public TeeMerge(IPipeFitting input) {
        this(input, null);
    }

    /**
     * <P>Constructor.</P>
     *
     * <P>Create the TeeMerge and the two optional constructor inputs.
     * This is the most common configuration, though you can connect
     * as many inputs as necessary by calling <code>connectInput</code>
     * repeatedly.</P>
     *
     * <P>Connect the single output fitting normally by calling the
     * <code>connect</code> method, as you would with any other IPipeFitting.</P>
     *
     * @param input1 pipe 1
     * @param input2 pipe 2
     */
    public TeeMerge(IPipeFitting input1, IPipeFitting input2) {
        if(input1 != null) connectInput(input1);
        if(input2 != null) connectInput(input2);
    }

    /**
     * <P>Connect an input IPipeFitting.</P>
     *
     * <P>NOTE: You can connect as many inputs as you want
     * by calling this method repeatedly.</P>
     *
     * @param input the IPipeFitting to connect for input.
     * @return true if pipe connection was successful.
     */
    public boolean connectInput(IPipeFitting input) {
        return input.connect(this);
    }

}
