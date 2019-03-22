//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.mediator.Mediator;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

/**
 * <P>Junction Mediator.</P>
 *
 * <P>A base class for handling the Pipe Junction in an IPipeAware
 * Core.</P>
 */
public class JunctionMediator extends Mediator {

    /**
     * <P>Accept input pipe notification name constant.</P>
     */
    public static final String ACCEPT_INPUT_PIPE = "acceptInputPipe";

    /**
     * <P>Accept output pipe notification name constant.</P>
     */
    public static final String ACCEPT_OUTPUT_PIPE = "acceptOutputPipe";

    /**
     * <P>Constructor.</P>
     *
     * @param name mediator name
     * @param viewComponent junction
     */
    public JunctionMediator(String name, Junction viewComponent) {
        super(name, viewComponent);
    }

    /**
     * <P>List Notification Interests.</P>
     *
     * <P>Returns the notification interests for this base class.
     * Override in subclass and call <code>super.listNotificationInterests</code>
     * to get this list, then add any sublcass interests to
     * the array before returning.</P>
     */
    public String[] listNotificationInterests() {
        return new String[]{
                JunctionMediator.ACCEPT_INPUT_PIPE,
                JunctionMediator.ACCEPT_OUTPUT_PIPE
        };
    }

    /**
     * <P>Handle Notification.</P>
     *
     * <P>This provides the handling for common junction activities. It
     * accepts input and output pipes in response to <code>IPipeAware</code>
     * interface calls.</P>
     *
     * <P>Override in subclass, and call <code>super.handleNotification</code>
     * if none of the subclass-specific notification names are matched.</P>
     */
    public void handleNotification(INotification notification) {
        switch (notification.getName()) {
            // accept an input pipe
            // register the pipe and if successful
            // set this mediator as its listener
            case JunctionMediator.ACCEPT_INPUT_PIPE:
                String inputPipeName = notification.getType();
                IPipeFitting inputPipe = (IPipeFitting)notification.getBody();
                if(getJunction().registerPipe(inputPipeName, Junction.INPUT, inputPipe)) {
                    getJunction().addPipeListener(inputPipeName, this, this::handlePipeMessage);
                }
                break;

            // accept an output pipe
            case JunctionMediator.ACCEPT_OUTPUT_PIPE:
                String outputPipeName = notification.getType();
                IPipeFitting outputPipe = (IPipeFitting)notification.getBody();
                getJunction().registerPipe(outputPipeName, Junction.OUTPUT, outputPipe);
                break;
        }
    }

    /**
     * <P>Handle incoming pipe messages.</P>
     *
     * <P>Override in subclass and handle messages appropriately for the module.</P>
     *
     * @param message message to be handled
     */
    public void handlePipeMessage(IPipeMessage message) {

    }

    /**
     * <P>The Junction for this Module.</P>
     *
     * @return junction
     */
    protected Junction getJunction() {
        return (Junction)getViewComponent();
    }
}
