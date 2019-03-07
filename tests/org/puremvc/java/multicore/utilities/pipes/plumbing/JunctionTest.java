//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2019 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Test the Junction class.
 */
public class JunctionTest {

    private class Prop {
        public int prop;

        public Prop(int prop) {
            this.prop = prop;
        }
    }

    /**
     * Test registering an INPUT pipe to a junction.
     * <P>
     * Tests that the INPUT pipe is successfully registered and
     * that the hasPipe and hasInputPipe methods work. Then tests
     * that the pipe can be retrieved by name.</P>
     * <P>
     * Finally, it removes the registered INPUT pipe and tests
     * that all the previous assertions about it's registration
     * and accessability via the Junction are no longer true.</P>
     */
    @Test
    public void testRegisterRetrieveAndRemoveInputPipe() {
        // create pipe connected to this test with a pipelistener
        IPipeFitting pipe = new Pipe();

        // create junction
        Junction junction = new Junction();

        // register the pipe with the junction, giving it a name and direction
        boolean registered = junction.registerPipe("testInputPipe", Junction.INPUT, pipe);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe, "Expecting pipe is Pipe");
        Assertions.assertNotNull((Junction)junction, "Expecting junction is Junction");
        Assertions.assertTrue(registered, "Expecting success registering pipe");

        // assertions about junction methods once input  pipe is registered
        Assertions.assertTrue(junction.hasPipe("testInputPipe"), "Expecting junction has pipe");
        Assertions.assertTrue(junction.hasInputPipe("testInputPipe"), "Expecting junction has pipe registered as an INPUT type");
        Assertions.assertSame(pipe, junction.retrievePipe("testInputPipe"), "Expecting pipe retrieved from junction");

        // now remove the pipe and be sure that it is no longer there (same assertions should be false)
        junction.removePipe("testInputPipe");
        Assertions.assertFalse(junction.hasPipe("testInputPipe"), "Expecting junction has pipe");
        Assertions.assertFalse(junction.hasInputPipe("testInputPipe"), "Expecting junction has pipe registered as an INPUT type");
        Assertions.assertNull(junction.retrievePipe("testInputPipe"), "Expecting pipe retrieved from junction");
    }

    /**
     * Test registering an OUTPUT pipe to a junction.
     * <P>
     * Tests that the OUTPUT pipe is successfully registered and
     * that the hasPipe and hasOutputPipe methods work. Then tests
     * that the pipe can be retrieved by name.</P>
     * <P>
     * Finally, it removes the registered OUTPUT pipe and tests
     * that all the previous assertions about it's registration
     * and accessability via the Junction are no longer true.</P>
     */
    @Test
    public void testRegisterRetrieveAndRemoveOutputPipe() {
        // create pipe connected to this test with a pipelistener
        IPipeFitting pipe = new Pipe();

        // create junction
        Junction junction = new Junction();

        // register the pipe with the junction, giving it a name and direction
        boolean registered = junction.registerPipe("testOutputPipe", Junction.OUTPUT, pipe);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe, "Expecting pipe is Pipe");
        Assertions.assertNotNull((Junction)junction, "Expecting junction is Junction");
        Assertions.assertTrue(registered, "Expecting success registering pipe");

        // assertions about junction methods once input  pipe is registered
        Assertions.assertTrue(junction.hasPipe("testOutputPipe"), "Expecting junction has pipe");
        Assertions.assertTrue(junction.hasOutputPipe("testOutputPipe"), "Expecting junction has pipe registered as an INPUT type");
        Assertions.assertSame(pipe, junction.retrievePipe("testOutputPipe"), "Expecting pipe retrieved from junction");

        // now remove the pipe and be sure that it is no longer there (same assertions should be false)
        junction.removePipe("testOutputPipe");
        Assertions.assertFalse(junction.hasPipe("testOutputPipe"), "Expecting junction has pipe");
        Assertions.assertFalse(junction.hasOutputPipe("testOutputPipe"), "Expecting junction has pipe registered as an INPUT type");
        Assertions.assertNull(junction.retrievePipe("testOutputPipe"), "Expecting pipe retrieved from junction");
    }

    /**
     * Test adding a PipeListener to an Input Pipe.
     * <P>
     * Registers an INPUT Pipe with a Junction, then tests
     * the Junction's addPipeListener method, connecting
     * the output of the pipe back into to the test. If this
     * is successful, it sends a message down the pipe and
     * checks to see that it was received.</P>
     */
    @Test
    public void testAddingPipeListenerToAnInputPipe() {
        // create pipe
        IPipeFitting pipe = new Pipe();

        // create junction
        Junction junction = new Junction();

        // create test message
        IPipeMessage message = new Message(Message.NORMAL, new Prop(1));

        // register the pipe with the junction, giving it a name and direction
        boolean registered = junction.registerPipe("testInputPipe", Junction.INPUT, pipe);

        // add the pipelistener using the junction method
        boolean listenerAdded = junction.addPipeListener("testInputPipe", this, this::callBackMethod);

        // send the message using our reference to the pipe,
        // it should show up in messageReceived property via the pipeListener
        boolean sent = pipe.write(message);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe, "Expecting pipe is Pipe");
        Assertions.assertNotNull((Junction)junction, "Expecting junction is Junction");
        Assertions.assertTrue(registered, "Expecting registered pipe");
        Assertions.assertTrue(listenerAdded, "Expecting added pipeListener");
        Assertions.assertTrue(sent, "Expecting successful write to pipe");
        Assertions.assertEquals(1, messagesReceived.size(), "Expecting 1 message received");
        Assertions.assertSame(message, messagesReceived.remove(0), "Expecting received message was same instance sent");
    }

    /**
     * Test using sendMessage on an OUTPUT pipe.
     * <P>
     * Creates a Pipe, Junction and Message.
     * Adds the PipeListener to the Pipe.
     * Adds the Pipe to the Junction as an OUTPUT pipe.
     * uses the Junction's sendMessage method to send
     * the Message, then checks that it was received.</P>
     */
    @Test
    public void testSendMessageOnAnOutputPipe() {
        // create pipe
        IPipeFitting pipe = new Pipe();

        // add a PipeListener manually
        boolean listenerAdded = pipe.connect(new PipeListener(this, this::callBackMethod));

        // create junction
        Junction junction = new Junction();

        // create test message
        IPipeMessage message = new Message(Message.NORMAL, new Prop(1));

        // register the pipe with the junction, giving it a name and direction
        boolean registered = junction.registerPipe("testOutputPipe", Junction.OUTPUT, pipe);

        // send the message using the Junction's method
        // it should show up in messageReceived property via the pipeListener
        boolean sent = junction.sendMessage("testOutputPipe", message);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe, "Expecting pipe is Pipe");
        Assertions.assertNotNull((Junction)junction, "Expecting junction is Junction");
        Assertions.assertTrue(registered, "Expecting registered pipe");
        Assertions.assertTrue(listenerAdded, "Expecting added pipeListener");
        Assertions.assertTrue(sent, "Expecting message sent");
        Assertions.assertEquals(1, messagesReceived.size(), "Expecting 1 message received");
        Assertions.assertSame(message, messagesReceived.remove(0), "Expecting received message was same instance");
    }

    /**
     * Array of received messages.
     * <P>
     * Used by <code>callBackMedhod</code> as a place to store
     * the recieved messages.</P>
     */
    private List<IPipeMessage> messagesReceived = new ArrayList<IPipeMessage>();

    /**
     * Callback given to <code>PipeListener</code> for incoming message.
     * <P>
     * Used by <code>testReceiveMessageViaPipeListener</code>
     * to get the output of pipe back into this  test to see
     * that a message passes through the pipe.</P>
     */
    private void callBackMethod(IPipeMessage message) {
        messagesReceived.add(message);
    }
}
