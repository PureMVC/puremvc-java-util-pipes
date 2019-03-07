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

/**
 * Test the TeeSplit class.
 */
public class TeeSplitTest {

    /**
     * Test connecting and disconnecting I/O Pipes.
     *
     * <P>
     * Connect an input and several output pipes to a splitting tee.
     * Then disconnect all outputs in LIFO order by calling disconnect
     * repeatedly.</P>
     */
    @Test
    public void testConnectingAndDisconnectingIOPipes() {
        // create input pipe
        IPipeFitting input1 = new Pipe();

        // create output pipes 1, 2, 3 and 4
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();
        IPipeFitting pipe3 = new Pipe();
        IPipeFitting pipe4 = new Pipe();

        // create splitting tee (args are first two output fittings of tee)
        TeeSplit teeSplit = new TeeSplit(pipe1, pipe2);

        // connect 2 extra outputs for a total of 4
        boolean connectedExtra1 = teeSplit.connect(pipe3);
        boolean connectedExtra2 = teeSplit.connect(pipe4);

        // connect the single input
        boolean inputConnected = input1.connect(teeSplit);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe1, "Expecting pipe1 is Pipe");
        Assertions.assertNotNull((Pipe)pipe2, "Expecting pipe2 is Pipe");
        Assertions.assertNotNull((Pipe)pipe3, "Expecting pipe3 is Pipe");
        Assertions.assertNotNull((Pipe)pipe4, "Expecting pipe4 is Pipe");
        Assertions.assertNotNull((TeeSplit)teeSplit, "Expecting teeSplit is TeeSplit");
        Assertions.assertTrue(connectedExtra1, "Expecting connected pipe 3");
        Assertions.assertTrue(connectedExtra2, "Expecting connected pipe 4");

        // test LIFO order of output disconnection
        Assertions.assertEquals(teeSplit.disconnect(), pipe4, "Expecting disconnected pipe4");
        Assertions.assertEquals(teeSplit.disconnect(), pipe3, "Expecting disconnected pipe3");
        Assertions.assertEquals(teeSplit.disconnect(), pipe2, "Expecting disconnected pipe2");
        Assertions.assertEquals(teeSplit.disconnect(), pipe1, "Expecting disconnected pipe1");
    }

    /**
     * Test disconnectFitting method.
     *
     * <P>
     * Connect several output pipes to a splitting tee.
     * Then disconnect specific outputs, making sure that once
     * a fitting is disconnected using disconnectFitting, that
     * it isn't returned when disconnectFitting is called again.
     * Finally, make sure that the when a message is sent to
     * the tee that the correct number of output messages is
     * written.
     * </P>
     */
    @Test
    public void testDisconnectFitting() {
        messagesReceived = new ArrayList<>();

        // create input pipe
        IPipeFitting input1 = new Pipe();

        // create output pipes 1, 2, 3 and 4
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();
        IPipeFitting pipe3 = new Pipe();
        IPipeFitting pipe4 = new Pipe();

        // setup pipelisteners
        pipe1.connect(new PipeListener(this, this::callBackMethod));
        pipe2.connect(new PipeListener(this, this::callBackMethod));
        pipe3.connect(new PipeListener(this, this::callBackMethod));
        pipe4.connect(new PipeListener(this, this::callBackMethod));

        // create splitting tee
        TeeSplit teeSplit = new TeeSplit();

        // add outputs
        teeSplit.connect(pipe1);
        teeSplit.connect(pipe2);
        teeSplit.connect(pipe3);
        teeSplit.connect(pipe4);

        // test assertions
        Assertions.assertEquals(teeSplit.disconnectFitting(pipe4), pipe4, "Expecting teeSplit.disconnectFitting(pipe4) === pipe4");
        Assertions.assertNull(teeSplit.disconnectFitting(pipe4), "Expecting teeSplit.disconnectFitting(pipe4) === pipe4");

        // Write a message to the tee
        teeSplit.write(new Message(Message.NORMAL));

        // test assertions
        Assertions.assertSame(3,messagesReceived.size(), "Expecting messagesReceived.size() == 3");
    }

    /**
     * Test receiving messages from two pipes using a TeeMerge.
     */
    @Test
    public void testReceiveMessagesFromTwoTeeSplitOutputs() {
        messagesReceived = new ArrayList<>();

        // create a message to send on pipe 1
        IPipeMessage message = new Message(Message.NORMAL);

        // create output pipes 1 and 2
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();

        // create and connect anonymous listeners
        boolean connected1 = pipe1.connect(new PipeListener(this, this::callBackMethod));
        boolean connected2 = pipe2.connect(new PipeListener(this, this::callBackMethod));

        // create splitting tee (args are first two output fittings of tee)
        TeeSplit teeSplit = new TeeSplit(pipe1, pipe2);

        // write messages to their respective pipes
        boolean written = teeSplit.write(message);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)message, "Expecting message is IPipeMessage");
        Assertions.assertNotNull((Pipe)pipe1, "Expecting pipe1 is Pipe");
        Assertions.assertNotNull((Pipe)pipe2, "Expecting pipe2 is Pipe");
        Assertions.assertNotNull((TeeSplit)teeSplit, "Expecting teeSplit is TeeSplit");
        Assertions.assertTrue(connected1, "Expecting connected anonymous listener to pipe1");
        Assertions.assertTrue(connected2, "Expecting connected anonymous listener to pipe2");
        Assertions.assertTrue(written, "Expecting wrote single message to tee");

        // test that both messages were received, then test
        // FIFO order by inspecting the messages themselves
        Assertions.assertSame(2, messagesReceived.size(), "Expecting received 2 messages");

        // test message 1 assertions
        IPipeMessage message1 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)message1, "Expecting message1 is IPipeMessage");
        Assertions.assertEquals(message, message1, "Expecting message1 === message");

        // test message 2 assertions
        IPipeMessage message2 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)message2, "Expecting message2 is IPipeMessage");
        Assertions.assertEquals(message, message2, "Expecting message2 === message");
    }

    /**
     * Array of received messages.
     * <P>
     * Used by <code>callBackMedhod</code> as a place to store
     * the recieved messages.</P>
     */
    private ArrayList<IPipeMessage> messagesReceived = new ArrayList<>();

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
