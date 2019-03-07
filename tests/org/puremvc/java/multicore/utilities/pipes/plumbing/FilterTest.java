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
import org.puremvc.java.multicore.utilities.pipes.messages.FilterControlMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;

import java.util.ArrayList;

/**
 * Test the Filter class.
 */
public class FilterTest {

    private class Rectangle {
        public float width;
        public float height;

        public Rectangle(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }

    private class Factor {
        public int factor;
        public Factor(int factor) {
            this.factor = factor;
        }
    }

    /**
     * Test connecting input and output pipes to a filter as well as disconnecting the output.
     */
    @Test
    public void testConnectingAndDisconnectingIOPipes() {
        // create output pipes 1
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();

        // create filter
        Filter filter = new Filter("TestFilter");

        // connect input fitting
        boolean connectedInput = pipe1.connect(filter);

        // connect output fitting
        boolean connectedOutput = filter.connect(pipe2);


        // test assertions
        Assertions.assertNotNull((Pipe)pipe1, "Expecting pipe1 is Pipe");
        Assertions.assertNotNull((Pipe)pipe2, "Expecting pipe2 is Pipe");
        Assertions.assertNotNull((Filter)filter, "Expecting filter is Filter");
        Assertions.assertTrue(connectedInput, "Expecting connected input");
        Assertions.assertTrue(connectedOutput, "Expecting connected output");

        // disconnect pipe 2 from filter
        IPipeFitting disconnectedPipe = filter.disconnect();
        Assertions.assertSame(pipe2, disconnectedPipe, "Expecting disconnected pipe2 from filter");
    }

    /**
     * Test applying filter to a normal message.
     */
    @Test
    public void testFilteringNormalMessage() {
        // create messages to send to the queue
        IPipeMessage message = new Message(Message.NORMAL, new Rectangle(10, 2));

        // create filter, attach an anonymous listener to the filter output to receive the message,
        // pass in an anonymous function an parameter object
        Filter filter = new Filter("scale", new PipeListener(this, this::callBackMethod), (msg, params) -> {
            ((Rectangle)msg.getHeader()).width *= ((Factor)params).factor;
            ((Rectangle)msg.getHeader()).height *= ((Factor)params).factor;
        }, new Factor(10));

        // write messages to the filter
        boolean written = filter.write(message);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)message, "Expecting message is IPipeMessage");
        Assertions.assertNotNull((Filter)filter, "Expecting filter is Filter");
        Assertions.assertTrue(written, "Expecting wrote message to filter");
        Assertions.assertEquals(1, messagesReceived.size(), "Expecting received 1 messages");

        // test filtered message assertions
        IPipeMessage received = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received, "Expecting received is IPipeMessage");
        Assertions.assertSame(message, received, "Expecting received === message");
        Assertions.assertTrue(((Rectangle)received.getHeader()).width == 100, "Expecting received.getHeader().width == 100");
        Assertions.assertTrue(((Rectangle)received.getHeader()).height == 20, "Expecting received.getHeader().height == 20");
    }

    /**
     * Test setting filter to bypass mode, writing, then setting back to filter mode and writing.
     */
    @Test
    public void testBypassAndFilterModeToggle() {
        // create messages to send to the queue
        IPipeMessage message = new Message(Message.NORMAL, new Rectangle(10, 2));

        // create filter, attach an anonymous listener to the filter output to receive the message,
        // pass in an anonymous function an parameter object
        Filter filter = new Filter("scale", new PipeListener(this, this::callBackMethod), (msg, params) -> {
            ((Rectangle)msg.getHeader()).width *= ((Factor)params).factor;
            ((Rectangle)msg.getHeader()).height *= ((Factor)params).factor;
        }, new Factor(10));

        // create bypass control message
        FilterControlMessage bypassMessage = new FilterControlMessage(FilterControlMessage.BYPASS, "scale");

        // write bypass control message to the filter
        boolean bypassWritten = filter.write(bypassMessage);

        // write normal message to the filter
        boolean written1 = filter.write(message);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)message, "Expecting message is IPipeMessage");
        Assertions.assertNotNull((Filter)filter, "Expecting filter is Filter");
        Assertions.assertTrue(bypassWritten, "Expecting bypass message to filter");
        Assertions.assertTrue(written1, "Expecting wrote normal message to filter");
        Assertions.assertEquals(1, messagesReceived.size(), "Expecting received 1 messages");

        // test filtered message assertions (no change to message)
        IPipeMessage received1 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received1, "Expecting received1 is iPipeMessage");
        Assertions.assertSame(message, received1, "Expecting received1 === message");
        Assertions.assertTrue(((Rectangle)received1.getHeader()).width == 10, "Expecting received1.getHeader().width == 10");
        Assertions.assertTrue(((Rectangle)received1.getHeader()).height == 2, "Expecting received1.getHeader().height == 2");

        // create filter control message
        FilterControlMessage filterMessage = new FilterControlMessage(FilterControlMessage.FILTER, "scale");

        // write bypass control message to the filter
        boolean filterWritten = filter.write(filterMessage);

        // write normal message to the filter again
        boolean written2 = filter.write(message);

        // test assertions
        Assertions.assertTrue(filterWritten, "Expecting wrote filter message to filter");
        Assertions.assertTrue(written2, "Expecting wrote normal message to filter");
        Assertions.assertEquals(1, messagesReceived.size(), "Expecting received 1 messages");

        // test filtered message assertions (message filtered)
        IPipeMessage received2 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received2, "Expecting received2 is IPipeMessage");
        Assertions.assertSame(message, received2, "Expecting received2 === message");
        Assertions.assertTrue(((Rectangle)received2.getHeader()).width == 100, "Expecting received2.getHeader().width == 100");
        Assertions.assertTrue(((Rectangle)received2.getHeader()).height == 20, "Expecting received2.getHeader().height == 20");
    }

    /**
     * Test setting filter parameters by sending control message.
     */
    @Test
    public void testSetParamsByControlMessage() {
        // create messages to send to the queue
        IPipeMessage message = new Message(Message.NORMAL, new Rectangle(10, 2));

        // create filter, attach an anonymous listener to the filter output to receive the message,
        // pass in an anonymous function an parameter object
        Filter filter = new Filter("scale", new PipeListener(this, this::callBackMethod), (msg, params) -> {
            ((Rectangle)msg.getHeader()).width *= ((Factor)params).factor;
            ((Rectangle)msg.getHeader()).height *= ((Factor)params).factor;
        }, new Factor(10));

        // create setParams control message
        FilterControlMessage setParamsMessage = new FilterControlMessage(FilterControlMessage.SET_PARAMS, "scale", null, new Factor(5));

        // write filter control message to the filter
        boolean setParamsWritten = filter.write(setParamsMessage);

        // write normal message to the filter
        boolean written = filter.write(message);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)message, "Expecting message is IPipeMessage");
        Assertions.assertNotNull((Filter)filter, "Expecting filter is Filter");
        Assertions.assertTrue(setParamsWritten, "Expecting wrote set_params message to filter");
        Assertions.assertTrue( written, "Expecting wrote normal message to filter" );
        Assertions.assertEquals(1, messagesReceived.size(), "Expecting received 1 messages");

        // test filtered message assertions (message filtered with overridden parameters)
        IPipeMessage received = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received, "Expecting received is IPipeMessage");
        Assertions.assertSame(message, received, "Expecting received === message");
        Assertions.assertTrue(((Rectangle)received.getHeader()).width == 50, "Expecting received.getHeader().width == 50");
        Assertions.assertTrue(((Rectangle)received.getHeader()).height == 10, "Expecting received.getHeader().height == 10");
    }

    /**
     * Test setting filter function by sending control message.
     */
    @Test
    public void testSetFilterByControlMessage() {
        // create messages to send to the queue
        IPipeMessage message = new Message(Message.NORMAL, new Rectangle(10, 2));

        // create filter, attach an anonymous listener to the filter output to receive the message,
        // pass in an anonymous function and an anonymous parameter object
        Filter filter = new Filter("scale", new PipeListener(this, this::callBackMethod), (msg, params) -> {
            ((Rectangle)msg.getHeader()).width *= ((Factor)params).factor;
            ((Rectangle)msg.getHeader()).height *= ((Factor)params).factor;
        }, new Factor(10));

        // create setFilter control message
        FilterControlMessage setFilterMessage = new FilterControlMessage(FilterControlMessage.SET_FILTER, "scale", (msg, params) -> {
            ((Rectangle)msg.getHeader()).width /= ((Factor)params).factor;
            ((Rectangle)msg.getHeader()).height /= ((Factor)params).factor;
        });

        // write filter control message to the filter
        boolean setFilterWritten = filter.write(setFilterMessage);

        // write normal message to the filter
        boolean written = filter.write(message);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)message, "Expecting message is IPipeMessage");
        Assertions.assertNotNull((Filter)filter, "Expecting filter is Filter");
        Assertions.assertTrue(setFilterWritten, "Expecting wrote message to filter");
        Assertions.assertTrue(written, "Expecting wrote normal message to filter");
        Assertions.assertEquals(1, messagesReceived.size(), "Expecting received 1 messages");

        // test filtered message assertions (message filtered with overridden filter function)
        IPipeMessage received = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received, "Expecting received is IPipeMessage");
        Assertions.assertSame(message, received, "Expecting received === message");
        Assertions.assertTrue(((Rectangle)received.getHeader()).width == 1, "Expecting received.getHeader().width == 1");
        Assertions.assertTrue(((Rectangle)received.getHeader()).height == 0.2F, "Expecting received.getHeader().height == .2");
    }

    private class Bozo {
        public int level;
        public String name;

        public Bozo(int level, String name) {
            this.level = level;
            this.name = name;
        }
    }

    private class Threshold {
        int threshold;
        public Threshold(int threshold) {
            this.threshold = threshold;
        }
    }

    /**
     * Test using a filter function to stop propagation of a message.
     * <P>
     * The way to stop propagation of a message from within a filter
     * is to throw an error from the filter function. This test creates
     * two NORMAL messages, each with header objects that contain
     * a <code>bozoLevel</code> property. One has this property set to
     * 10, the other to 3.</P>
     * <P>
     * Creates a Filter, named 'bozoFilter' with an anonymous pipe listener
     * feeding the output back into this test. The filter funciton is an
     * anonymous function that throws an error if the message's bozoLevel
     * property is greater than the filter parameter <code>bozoThreshold</code>.
     * the anonymous filter parameters object has a <code>bozoThreshold</code>
     * value of 5.</P>
     * <P>
     * The messages are written to the filter and it is shown that the
     * message with the <code>bozoLevel</code> of 10 is not written, while
     * the message with the <code>bozoLevel</code> of 3 is.</P>
     */
    @Test
    public void testUseFilterToStopAMessage() {
        // create messages to send to the queue
        IPipeMessage message1 = new Message(Message.NORMAL, new Bozo(10, "Dastardly Dan"));
        IPipeMessage message2 = new Message(Message.NORMAL, new Bozo(3, "Dudely Doright"));

        // create filter, attach an anonymous listener to the filter output to receive the message,
        // pass in an anonymous function and an anonymous parameter object
        Filter filter = new Filter("bozoFilter", new PipeListener(this, this::callBackMethod), (msg, params) -> {
            if(((Bozo)msg.getHeader()).level > ((Threshold)params).threshold) throw new RuntimeException("bozoFiltered");
        }, new Threshold(5));

        // write normal message to the filter
        boolean written1 = filter.write(message1);
        boolean written2 = filter.write(message2);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)message1, "Expecting message is IPipeMessage");
        Assertions.assertNotNull((IPipeMessage)message2, "Expecting message is IPipeMessage");
        Assertions.assertNotNull((Filter)filter, "Expecting filter is Filter");
        Assertions.assertFalse(written1, "Expecting failed to write bad message");
        Assertions.assertTrue(written2, "Expecting wrote good message");
        Assertions.assertEquals(1, messagesReceived.size(), "Expecting received 1 messages");

        // test filtered message assertions (message with good auth token passed
        IPipeMessage received = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received, "Expecting received is IPipeMessage");
        Assertions.assertSame(message2, received, "Expecting received === message2");
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
