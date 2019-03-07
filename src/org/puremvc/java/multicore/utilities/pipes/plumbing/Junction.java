//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Pipe Junction.
 *
 * <P>
 * Manages Pipes for a Module.
 *
 * <P>
 * When you register a Pipe with a Junction, it is
 * declared as being an INPUT pipe or an OUTPUT pipe.</P>
 *
 * <P>
 * You can retrieve or remove a registered Pipe by name,
 * check to see if a Pipe with a given name exists,or if
 * it exists AND is an INPUT or an OUTPUT Pipe.</P>
 *
 * <P>
 * You can send an <code>IPipeMessage</code> on a named INPUT Pipe
 * or add a <code>PipeListener</code> to registered INPUT Pipe.</P>
 */
public class Junction {

    /**
     *  INPUT Pipe Type
     */
    public static final String INPUT = "input";

    /**
     *  OUTPUT Pipe Type
     */
    public static final String OUTPUT = "output";

    /**
     *  The names of the INPUT pipes
     */
    protected List<String> inputPipes = new ArrayList<>();

    /**
     *  The names of the OUTPUT pipes
     */
    protected List<String> outputPipes = new ArrayList<>();

    /**
     * The map of pipe names to their pipes
     */
    protected ConcurrentHashMap<String, IPipeFitting> pipesMap = new ConcurrentHashMap<>();

    /**
     * The map of pipe names to their types
     */
    protected ConcurrentHashMap<String, String> pipeTypesMap = new ConcurrentHashMap<>();

    // Constructor.
    public Junction() {

    }

    /**
     * Register a pipe with the junction.
     * <P>
     * Pipes are registered by unique name and type,
     * which must be either <code>Junction.INPUT</code>
     * or <code>Junction.OUTPUT</code>.</P>
     * <P>
     * NOTE: You cannot have an INPUT pipe and an OUTPUT
     * pipe registered with the same name. All pipe names
     * must be unique regardless of type.</P>
     *
     * @param name name of the pipe
     * @param type type of the pipe
     * @param pipe pipe fitting
     * @return Boolean true if successfully registered. false if another pipe exists by that name.
     */
    public boolean registerPipe(String name, String type, IPipeFitting pipe) {
        boolean success = true;
        if(pipesMap.get(name) == null) {
            pipesMap.put(name, pipe);
            pipeTypesMap.put(name, type);
            switch (type) {
                case Junction.INPUT:
                    inputPipes.add(name);
                    break;
                case Junction.OUTPUT:
                    outputPipes.add(name);
                    break;
                default:
                    success = false;
            }
        } else {
            success = false;
        }
        return success;
    }

    /**
     * Does this junction have a pipe by this name?
     *
     * @param name the pipe to check for
     * @return whether as pipe is registered with that name.
     */
    public boolean hasPipe(String name) {
        return pipesMap.get(name) != null;
    }

    /**
     * Does this junction have an INPUT pipe by this name?
     *
     * @param name the pipe to check for
     * @return whether an INPUT pipe is registered with that name.
     */
    public boolean hasInputPipe(String name) {
        return hasPipe(name) && pipeTypesMap.get(name) == INPUT;
    }

    /**
     * Does this junction have an OUTPUT pipe by this name?
     *
     * @param name the pipe to check for
     * @return whether an OUTPUT pipe is registered with that name.
     */
    public boolean hasOutputPipe(String name) {
        return hasPipe(name) && pipeTypesMap.get(name) == OUTPUT;
    }

    /**
     * Remove the pipe with this name if it is registered.
     * <P>
     * NOTE: You cannot have an INPUT pipe and an OUTPUT
     * pipe registered with the same name. All pipe names
     * must be unique regardless of type.</P>
     *
     * @param name the pipe to remove
     */
    public void removePipe(String name) {
        if(hasPipe(name)) {
            String type = pipeTypesMap.get(name);
            switch (type) {
                case INPUT:
                    inputPipes.remove(name);
                    break;
                case OUTPUT:
                    outputPipes.remove(name);
                    break;
            }
            pipesMap.remove(name);
            pipeTypesMap.remove(name);
        }
    }

    /**
     * Retrieve the named pipe.
     *
     * @param name the pipe to retrieve
     * @return IPipeFitting the pipe registered by the given name if it exists
     */
    public IPipeFitting retrievePipe(String name) {
        return pipesMap.get(name);
    }

    /**
     * Add a PipeListener to an INPUT pipe.
     * <P>
     * NOTE: there can only be one PipeListener per pipe,
     * and the listener function must accept an IPipeMessage
     * as its sole argument.</P>
     *
     * @param inputPipeName the INPUT pipe to add a PipeListener to
     * @param context the calling context or 'this' object
     * @param listener the function on the context to call
     * @return true if listener connection was successful
     */
    public boolean addPipeListener(String inputPipeName, Object context, Consumer<IPipeMessage> listener) {
        boolean success = false;
        if(hasInputPipe(inputPipeName)) {
            IPipeFitting pipe = pipesMap.get(inputPipeName);
            success = pipe.connect(new PipeListener(context, listener));
        }
        return success;
    }

    /**
     * Send a message on an OUTPUT pipe.
     *
     * @param outputPipeName the OUTPUT pipe to send the message on
     * @param message the IPipeMessage to send
     * @return true if message was successfully written to the named pipe
     */
    public boolean sendMessage(String outputPipeName, IPipeMessage message) {
        boolean success = false;
        if(hasOutputPipe(outputPipeName)){
            IPipeFitting pipe = pipesMap.get(outputPipeName);
            success = pipe.write(message);
        }
        return success;
    }
}
