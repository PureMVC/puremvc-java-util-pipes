//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.messages;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

import java.util.function.BiConsumer;

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
    public static final String BASE = Message.BASE + "filter-control/";

    /**
     * Set filter parameters.
     */
    public static final String SET_PARAMS = BASE + "setparams";

    /**
     * Set filter function.
     */
    public static final String SET_FILTER = BASE + "setfilter";

    /**
     * Toggle to filter bypass mode.
     */
    public static final String BYPASS = BASE + "bypass";

    /**
     * Toggle to filtering mode. (default behavior).
     */
    public static final String FILTER = BASE + "filter";

    protected Object params;

    protected BiConsumer<IPipeMessage, Object> filter;

    protected String name;

    // Constructor
    public FilterControlMessage(String type, String name, BiConsumer<IPipeMessage, Object> filter, Object params) {
        super(type);
        setName(name);
        setFilter(filter);
        setParams(params);
    }

    // Constructor
    public FilterControlMessage(String type, String name, BiConsumer<IPipeMessage, Object> filter) {
        this(type, name, filter, null);
    }

    // Constructor
    public FilterControlMessage(String type, String name) {
        this(type, name, null, null);
    }

    /**
     * Get the target filter name.
     *
     * @return pipe name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the target filter name.
     *
     * @param name name of the pipe
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the filter function.
     *
     * @return filter function
     */
    public BiConsumer<IPipeMessage, Object> getFilter() {
        return filter;
    }

    /**
     * Set the filter function.
     *
     * @param filter filter function
     */
    public void setFilter(BiConsumer<IPipeMessage, Object> filter) {
        this.filter = filter;
    }

    /**
     * Get the parameters object.
     *
     * @return parameter object
     */
    public Object getParams() {
        return params;
    }

    /**
     * Set the parameters object.
     *
     * @param params parameter object
     */
    public void setParams(Object params) {
        this.params = params;
    }
}
