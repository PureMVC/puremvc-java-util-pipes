//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.FilterControlMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

import java.util.function.BiConsumer;

/**
 * <P>Pipe Filter.</P>
 *
 * <P>Filters may modify the contents of messages before writing them to
 * their output pipe fitting. They may also have their parameters and
 * filter function passed to them by control message, as well as having
 * their Bypass/Filter operation mode toggled via control message.</p>
 */
public class Filter extends Pipe {

    protected String mode = FilterControlMessage.FILTER;

    protected BiConsumer<IPipeMessage, Object> filter = (message, params) -> {return;};

    protected Object params = new Object();

    protected String name;

    // Constructor
    public Filter(String name, IPipeFitting output, BiConsumer<IPipeMessage, Object> filter, Object params) {
        super(output);
        this.name = name;
        if(filter != null) this.filter = filter;
        if(params != null) this.params = params;
    }

    // Constructor
    public Filter(String name, IPipeFitting output, BiConsumer<IPipeMessage, Object> filter) {
        this(name, output, filter, null);
    }

    // Constructor
    public Filter(String name, IPipeFitting output){
        this(name, output, null, null);
    }

    // Constructor
    public Filter(String name) {
        this(name, null, null, null);
    }

    /**
     * <P>Handle the incoming message.</P>
     *
     * <P>If message type is normal, filter the message (unless in BYPASS mode)
     * and write the result to the output pipe fitting if the filter
     * operation is successful.</P>
     *
     * <P>The FilterControlMessage.SET_PARAMS message type tells the Filter
     * that the message class is FilterControlMessage, which it
     * casts the message to in order to retrieve the filter parameters
     * object if the message is addressed to this filter.</P>
     *
     * <P>The FilterControlMessage.SET_FILTER message type tells the Filter
     * that the message class is FilterControlMessage, which it
     * casts the message to in order to retrieve the filter function.</P>
     *
     * <P>The FilterControlMessage.BYPASS message type tells the Filter
     * that it should go into Bypass mode operation, passing all normal
     * messages through unfiltered.</P>
     *
     * <P>The FilterControlMessage.FILTER message type tells the Filter
     * that it should go into Filtering mode operation, filtering all
     * normal normal messages before writing out. This is the default
     * mode of operation and so this message type need only be sent to
     * cancel a previous BYPASS message.</P>
     *
     * <P>The Filter only acts on the control message if it is targeted
     * to this named filter instance. Otherwise it writes through to the
     * output.</P>
     *
     * @return Boolean True if the filter process does not throw an error and subsequent operations
     * in the pipeline succede.
     */
    public boolean write(IPipeMessage message) {
        boolean success = true;

        switch (message.getType()) {

            // Filter normal messages
            case Message.NORMAL:
                try {
                    if(mode == FilterControlMessage.FILTER) {
                        applyFilter(message);
                    }
                    success = output.write(message);
                } catch (Exception exception) {
                    return false;
                }
                break;

            // Accept parameters from control message
            case FilterControlMessage.SET_PARAMS:
                if(isTarget(message)) {
                    setParams(((FilterControlMessage)message).getParams());
                } else {
                    success = output.write(message);
                }
                break;

            // Accept filter function from control message
            case FilterControlMessage.SET_FILTER:
                if(isTarget(message)) {
                    setFilter(((FilterControlMessage)message).getFilter());
                } else {
                    success = output.write(message);
                }
                break;

            // Toggle between Filter or Bypass operational modes
            case FilterControlMessage.BYPASS:
            case FilterControlMessage.FILTER:
                if(isTarget(message)) {
                    mode = ((FilterControlMessage)message).getType();
                } else {
                    success = output.write(message);
                }
                break;

            // Write control messages for other fittings through
            default:
                success = output.write(message);
        }
        return success;
    }

    /**
     * <P>Is the message directed at this filter instance?</P>
     *
     * @param message message to be tested for filter instance
     * @return true if the message is directed at this filter instance
     */
    protected boolean isTarget(IPipeMessage message) {
        return ((FilterControlMessage)message).getName() == name;
    }

    /**
     * <P>Set the Filter parameters.</P>
     *
     * <P>This can be an object can contain whatever arbitrary
     * properties and values your filter method requires to
     * operate.</P>
     *
     * @param params the parameters object
     */
    public void setParams(Object params) {
        this.params = params;
    }

    /**
     * <P>Set the Filter function.</P>
     *
     * <P>It must accept two arguments; an IPipeMessage,
     * and a parameter Object, which can contain whatever
     * arbitrary properties and values your filter method
     * requires.</P>
     *
     * @param filter the filter function.
     */
    public void setFilter(BiConsumer<IPipeMessage, Object> filter) {
        this.filter = filter;
    }

    /**
     * <P>Filter the message.</P>
     *
     * @param message message to be passed to the filter
     * @return message
     */
    protected IPipeMessage applyFilter(IPipeMessage message) {
        filter.accept(message, params);
        return message;
    }
}



