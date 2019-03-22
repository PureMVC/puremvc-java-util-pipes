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
import org.puremvc.java.multicore.utilities.pipes.messages.QueueControlMessage;

import java.util.ArrayList;

/**
 * Test the Queue class.
 */
public class QueueTest {

    private class Prop {
        public int prop;

        public Prop(int prop) {
            this.prop = prop;
        }
    }

    /**
     * Test connecting input and output pipes to a queue.
     */
    @Test
    public void testConnectingIOPipes() {
        // create output pipes 1
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();

        // create queue
        Queue queue = new Queue();

        // connect input fitting
        boolean connectedInput = pipe1.connect(queue);

        // connect output fitting
        boolean connectedOutput = queue.connect(pipe2);
        
        // test assertions
        Assertions.assertNotNull((Pipe)pipe1, "Expecting pipe1 is Pipe");
        Assertions.assertNotNull((Pipe)pipe2, "Expecting pipe2 is Pipe");
        Assertions.assertNotNull((Queue)queue, "Expecting queue is Queue");
        Assertions.assertTrue(connectedInput, "Expecting connected input");
        Assertions.assertTrue(connectedOutput, "Expecting connected output");
    }

    /**
     * Test writing multiple messages to the Queue followed by a Flush message.
     * <P>
     * Creates messages to send to the queue.
     * Creates queue, attaching an anonymous listener to its output.
     * Writes messages to the queue. Tests that no messages have been
     * received yet (they've been enqueued). Sends FLUSH message. Tests
     * that messages were receieved, and in the order sent (FIFO).</P>
     */
    @Test
    public void testWritingMultipleMessagesAndFlush() {
        // create messages to send to the queue
        IPipeMessage message1 = new Message(Message.NORMAL, new Prop(1));
        IPipeMessage message2 = new Message(Message.NORMAL, new Prop(2));
        IPipeMessage message3 = new Message(Message.NORMAL, new Prop(3));

        // create queue control flush message
        IPipeMessage flush = new QueueControlMessage(QueueControlMessage.FLUSH);

        // create queue, attaching an anonymous listener to its output
        Queue queue = new Queue(new PipeListener(this, this::callBackMethod));

        // write messages to the queue
        boolean message1written = queue.write(message1);
        boolean message2written = queue.write(message2);
        boolean message3written = queue.write(message3);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)message1, "Expecting message1 is IPipeMessage");
        Assertions.assertNotNull((IPipeMessage)message2, "Expecting message2 is IPipeMessage");
        Assertions.assertNotNull((IPipeMessage)message3, "Expecting message3 is IPipeMessage");
        Assertions.assertNotNull((IPipeMessage)flush, "Expecting flush is IPipeMessage");
        Assertions.assertNotNull((Queue)queue, "Expecting queue is Queue");

        Assertions.assertTrue(message1written, "Expecting wrote message1 to queue");
        Assertions.assertTrue(message2written, "Expecting wrote message2 to queue");
        Assertions.assertTrue(message3written, "Expecting wrote message3 to queue");

        // test that no messages were received (they've been enqueued)
        Assertions.assertEquals(0, messagesReceived.size(), "Expecting received 0 messages");

        // write flush control message to the queue
        boolean flushWritten = queue.write(flush);

        // test that all messages were received, then test
        // FIFO order by inspecting the messages themselves
        Assertions.assertEquals(3, messagesReceived.size(), "Expecting received 3 messages");

        // test message 1 assertions
        IPipeMessage received1 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received1, "Expecting received1 is IPipeMessage");
        Assertions.assertSame(message1, received1, "Expecting received1 === message1");

        // test message 2 assertions
        IPipeMessage received2 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received2, "Expecting received2 is IPipeMessage");
        Assertions.assertSame(message2, received2, "Expecting received2 === message2");

        // test message 3 assertions
        IPipeMessage received3 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)received3, "Expecting received3 is IPipeMessage");
        Assertions.assertSame(message3, received3, "Expecting received3 === message3");
    }

    /**
     * Test the Sort-by-Priority and FIFO modes.
     * <P>
     * Creates messages to send to the queue, priorities unsorted.
     * Creates queue, attaching an anonymous listener to its output.
     * Sends SORT message to start sort-by-priority order mode.
     * Writes messages to the queue. Sends FLUSH message, tests
     * that messages were receieved in order of priority, not how
     * they were sent.</P>
     * <P>
     * Then sends a FIFO message to switch the queue back to
     * default FIFO behavior, sends messages again, flushes again,
     * tests that the messages were recieved and in the order they
     * were originally sent.</P>
     */
    @Test
    public void testSortByPriorityAndFIFO() {
        // create messages to send to the queue
        IPipeMessage message1 = new Message(Message.NORMAL, null, null, Message.PRIORITY_MED);
        IPipeMessage message2 = new Message(Message.NORMAL, null, null, Message.PRIORITY_LOW);
        IPipeMessage message3 = new Message(Message.NORMAL, null, null, Message.PRIORITY_HIGH);

        // create queue, attaching an anonymous listener to its output
        Queue queue = new Queue(new PipeListener(this, this::callBackMethod));

        // begin sort-by-priority order mode
        boolean sortWritten = queue.write(new QueueControlMessage(QueueControlMessage.SORT));

        // write messages to the queue
        boolean message1written = queue.write(message1);
        boolean message2written = queue.write(message2);
        boolean message3written = queue.write(message3);

        // flush the queue
        boolean flushWritten = queue.write(new QueueControlMessage(QueueControlMessage.FLUSH));

        // test assertions
        Assertions.assertTrue(sortWritten, "Expecting wrote sort message to queue");
        Assertions.assertTrue(message1written, "Expecting wrote message1 to queue");
        Assertions.assertTrue(message2written, "Expecting wrote message2 to queue");
        Assertions.assertTrue(message3written, "Expecting wrote message3 to queue");
        Assertions.assertTrue(flushWritten, "Expected wrote flus message to queue");

        // test that 3 messages were received
        Assertions.assertEquals(3, messagesReceived.size(), "Expecting received 3 messages");

        // get the messages
        IPipeMessage received1 = messagesReceived.remove(0);
        IPipeMessage received2 = messagesReceived.remove(0);
        IPipeMessage received3 = messagesReceived.remove(0);

        // test that the message order is sorted
        Assertions.assertTrue(received1.getPriority() < received2.getPriority(), "Expecting received1 is higher priority than received2");
        Assertions.assertTrue(received2.getPriority() < received3.getPriority(), "Expecting received2 is higher priority than received3");
        Assertions.assertSame(received1, message3, "Expecting received1 === message3");
        Assertions.assertSame(received2, message1, "Expecting received2 === message1");
        Assertions.assertSame(received3, message2, "Expecting received3 === message2");

        // begin FIFO order mode
        boolean fifoWritten = queue.write(new QueueControlMessage(QueueControlMessage.FIFO));

        // write messages to the queue
        boolean message1writtenAgain = queue.write(message1);
        boolean message2writtenAgain = queue.write(message2);
        boolean message3writtenAgain = queue.write(message3);

        // flush the queue
        boolean flushWrittenAgain = queue.write(new QueueControlMessage(QueueControlMessage.FLUSH));

        // test assertions
        Assertions.assertTrue(fifoWritten, "Expecting wrote fifo message to queue");
        Assertions.assertTrue(message1writtenAgain, "Expecting wrote message1 to queue again");
        Assertions.assertTrue(message2writtenAgain, "Expecting wrote message2 to queue again");
        Assertions.assertTrue(message3writtenAgain, "Expecting wrote message3 to queue again");

        // test that 3 messages were received
        Assertions.assertEquals(3, messagesReceived.size(), "Expecting received 3 messages");

        // get the messages
        IPipeMessage received1Again = messagesReceived.remove(0);
        IPipeMessage received2Again = messagesReceived.remove(0);
        IPipeMessage received3Again = messagesReceived.remove(0);

        // test message order is FIFO
        Assertions.assertSame(received1Again, message1, "Expecting received1Again === message1");
        Assertions.assertSame(received2Again, message2, "Expecting received2Again === message2");
        Assertions.assertSame(received3Again, message3, "Expecting received3Again === message3");
        Assertions.assertEquals(Message.PRIORITY_MED, received1Again.getPriority(), "Expecting received1Again is priority med");
        Assertions.assertEquals(Message.PRIORITY_LOW, received2Again.getPriority(), "Expecting received2Again is priority low");
        Assertions.assertEquals(Message.PRIORITY_HIGH, received3Again.getPriority(), "Expecting received3Again is priority high");
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
