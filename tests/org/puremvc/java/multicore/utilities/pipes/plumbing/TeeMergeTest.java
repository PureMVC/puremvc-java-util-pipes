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
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Test the TeeMerge class.
 */
public class TeeMergeTest {

    private class Prop {
        public int value;

        public Prop(int value) {
            this.value = value;
        }
    }

    /**
     * Test connecting an output and several input pipes to a merging tee.
     */
    @Test
    public void testConnectingIOPipes() {
        // create input pipe
        IPipeFitting output1 = new Pipe();

        // create input pipes 1, 2, 3 and 4
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();
        IPipeFitting pipe3 = new Pipe();
        IPipeFitting pipe4 = new Pipe();

        // create splitting tee (args are first two input fittings of tee)
        TeeMerge teeMerge = new TeeMerge(pipe1, pipe2);

        // connect 2 extra inputs for a total of 4
        boolean connectedExtra1 = teeMerge.connectInput(pipe3);
        boolean connectedExtra2 = teeMerge.connectInput(pipe4);

        // connect the single output
        boolean connected = output1.connect(teeMerge);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe1, "Expecting pipe1 is Pipe");
        Assertions.assertNotNull((Pipe)pipe2, "Expecting pipe2 is Pipe");
        Assertions.assertNotNull((Pipe)pipe3, "Expecting pipe3 is Pipe");
        Assertions.assertNotNull((Pipe)pipe4, "Expecting pipe4 is Pipe");
        Assertions.assertNotNull((TeeMerge)teeMerge, "Expecting teeMerge is TeeMerge");
        Assertions.assertTrue(connectedExtra1, "Expecting connected extra input 1");
        Assertions.assertTrue(connectedExtra2, "Expecting connected extra input 2");
    }

    /**
     * Test receiving messages from two pipes using a TeeMerge.
     */
    @Test
    public void testReceiveMessagesFromTwoPipesViaTeeMerge() throws Exception {
        // create a message to send on pipe 1
        Document doc1 = (DocumentBuilderFactory.newInstance()).newDocumentBuilder().parse(new InputSource(new StringReader("<testMessage testAtt='Pipe 1 Message'/>")));
        IPipeMessage pipe1Message = new Message(Message.NORMAL, new Prop(1), doc1, Message.PRIORITY_LOW);

        // create a message to send on pipe 2
        Document doc2 = (DocumentBuilderFactory.newInstance()).newDocumentBuilder().parse(new InputSource(new StringReader("<testMessage testAtt='Pipe 2 Message'/>")));
        IPipeMessage pipe2Message = new Message(Message.NORMAL, new Prop(2), doc2, Message.PRIORITY_HIGH);

        // create pipes 1 and 2
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();

        // create merging tee (args are first two input fittings of tee)
        TeeMerge teeMerge = new TeeMerge(pipe1, pipe2);

        // create listener
        PipeListener listener = new PipeListener(this, this::callBackMethod);

        // connect the listener to the tee and write the messages
        boolean connected = teeMerge.connect(listener);

        // write messages to their respective pipes
        boolean pipe1written = pipe1.write(pipe1Message);
        boolean pipe2written = pipe2.write(pipe2Message);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)pipe1Message, "Expecting pipe1Message is IPipeMessage");
        Assertions.assertNotNull((IPipeMessage)pipe2Message, "Expecting pipe2Message is IPipeMessage");
        Assertions.assertNotNull((Pipe)pipe1, "Expecting pipe1 is Pipe");
        Assertions.assertNotNull((Pipe)pipe2, "Expecting pipe2 is Pipe");
        Assertions.assertNotNull((TeeMerge)teeMerge, "Expecting teeMerge is TeeMerge");
        Assertions.assertNotNull((PipeListener)listener, "Expecting listener is PipeListener");
        Assertions.assertTrue(connected, "Expecting connected listener to merging tee");
        Assertions.assertTrue(pipe1written, "Expecting wrote message to pipe 1");
        Assertions.assertTrue(pipe2written, "Expecting wrote message to pipe 2");

        // test that both messages were received, then test
        // FIFO order by inspecting the messages themselves
        Assertions.assertEquals(messagesReceived.size(), 2, "Expecting received 2 messages");

        // test message 1 assertions
        IPipeMessage message1 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)message1, "Expecting message1 is IPipeMessage");
        Assertions.assertEquals(message1, pipe1Message, "Expecting message1.equals(pipe1Message)");
        Assertions.assertEquals(message1.getType(), Message.NORMAL, "Expecting message1.getType == Message.NORMAL");
        Assertions.assertEquals(((Prop)message1.getHeader()).value, 1, "Expecting message1.getHeader().testProp = 'testval'");
        Assertions.assertTrue(((Document)message1.getBody()).getDocumentElement().getAttribute("testAtt").equals("Pipe 1 Message"), "Expecting ((Document)message1.getBody()).getDocumentElement().getAttribute('testAtt') == 'Pipe 1 Message'");
        Assertions.assertTrue(message1.getPriority() == Message.PRIORITY_LOW, "Expecting message1.getPriority() == Message.PRIORITY_HIGH");

        // test message 2 assertions
        IPipeMessage message2 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)message2, "Expecting message2 is IPipeMessage");
        Assertions.assertEquals(message2, pipe2Message, "Expecting message2.equals(pipe1Message)");
        Assertions.assertEquals(message2.getType(), Message.NORMAL, "Expecting message2.getType == Message.NORMAL");
        Assertions.assertEquals(((Prop)message2.getHeader()).value, 2, "Expecting message2.getHeader().testProp = '2'");
        Assertions.assertTrue(((Document)message2.getBody()).getDocumentElement().getAttribute("testAtt").equals("Pipe 2 Message"), "Expecting ((Document)message2.getBody()).getDocumentElement().getAttribute('testAtt') == 'Pipe 2 Message'");
        Assertions.assertTrue(message2.getPriority() == Message.PRIORITY_HIGH, "Expecting message2.getPriority() == Message.PRIORITY_HIGH");
    }

    /**
     * Test receiving messages from four pipes using a TeeMerge.
     */
    @Test
    public void testReceiveMessagesFromFourPipesViaTeeMerge() {
        // create a message to send on pipe 1
        IPipeMessage pipe1Message = new Message(Message.NORMAL, new Prop(1));
        IPipeMessage pipe2Message = new Message(Message.NORMAL, new Prop(2));
        IPipeMessage pipe3Message = new Message(Message.NORMAL, new Prop(3));
        IPipeMessage pipe4Message = new Message(Message.NORMAL, new Prop(4));

        // create pipes 1, 2, 3 and 4
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();
        IPipeFitting pipe3 = new Pipe();
        IPipeFitting pipe4 = new Pipe();

        // create merging tee
        TeeMerge teeMerge = new TeeMerge(pipe1, pipe2);
        boolean connectedExtraInput3 = teeMerge.connectInput(pipe3);
        boolean connectedExtraInput4 = teeMerge.connectInput(pipe4);

        // create listener
        PipeListener listener = new PipeListener(this, this::callBackMethod);

        // connect the listener to the tee and write the messages
        boolean connected = teeMerge.connect(listener);

        // write messages to their respective pipes
        boolean pipe1written = pipe1.write(pipe1Message);
        boolean pipe2written = pipe2.write(pipe2Message);
        boolean pipe3written = pipe3.write(pipe3Message);
        boolean pipe4written = pipe4.write(pipe4Message);

        // test assertions
        Assertions.assertNotNull((IPipeMessage)pipe1Message, "Expecting pipe1Message is IPipeMessage");
        Assertions.assertNotNull((IPipeMessage)pipe2Message, "Expecting pipe2Message is IPipeMessage");
        Assertions.assertNotNull((IPipeMessage)pipe3Message, "Expecting pipe3Message is IPipeMessage");
        Assertions.assertNotNull((IPipeMessage)pipe4Message, "Expecting pipe4Message is IPipeMessage");
        Assertions.assertNotNull((Pipe)pipe1, "Expecting pipe1 is Pipe");
        Assertions.assertNotNull((Pipe)pipe2, "Expecting pipe2 is Pipe");
        Assertions.assertNotNull((Pipe)pipe3, "Expecting pipe3 is Pipe");
        Assertions.assertNotNull((Pipe)pipe4, "Expecting pipe4 is Pipe");
        Assertions.assertNotNull((TeeMerge)teeMerge, "Expecting teeMerge is TeeMerge");
        Assertions.assertNotNull((PipeListener)listener, "Expecting listener is PipeListener");
        Assertions.assertTrue(connected, "Expecting connected listener to merging tee");
        Assertions.assertTrue(connectedExtraInput3, "Expecting connected extra input pipe3 to merging tee");
        Assertions.assertTrue(connectedExtraInput4, "Expecting connected extra input pipe4 to merging tee");
        Assertions.assertTrue(pipe1written, "Expecting wrote message to pipe1");
        Assertions.assertTrue(pipe2written, "Expecting wrote message to pipe2");
        Assertions.assertTrue(pipe3written, "Expecting wrote message to pipe3");
        Assertions.assertTrue(pipe4written, "Expecting wrote message to pipe4");

        // test that both messages were received, then test
        // FIFO order by inspecting the messages themselves
        Assertions.assertEquals(4, messagesReceived.size(), "Expecting received 4 messages");

        // test message 1 assertions
        IPipeMessage message1 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)message1, "Expecting message1 is IPipeMessage");
        Assertions.assertSame(message1, pipe1Message, "Expecting message1 === pipe1Message");
        Assertions.assertEquals(Message.NORMAL, message1.getType(), "Expecting message1.getType == Message.NORMAL");
        Assertions.assertSame(1, ((Prop)message1.getHeader()).value, "Expecting message1.getHeader().testProp = '1'");

        // test message 2 assertions
        IPipeMessage message2 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)message2, "Expecting message2 is IPipeMessage");
        Assertions.assertSame(message2, pipe2Message, "Expecting message2 === pipe1Message");
        Assertions.assertEquals(Message.NORMAL, message2.getType(), "Expecting message2.getType == Message.NORMAL");
        Assertions.assertSame(2,((Prop)message2.getHeader()).value, "Expecting message2.getHeader().testProp = '2'");

        // test message 3 assertions
        IPipeMessage message3 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)message3, "Expecting message3 is IPipeMessage");
        Assertions.assertSame(message3, pipe3Message, "Expecting message3 === pipe1Message");
        Assertions.assertEquals(Message.NORMAL, message3.getType(), "Expecting message3.getType == Message.NORMAL");
        Assertions.assertSame(3, ((Prop)message3.getHeader()).value, "Expecting message3.getHeader().testProp = '3'");

        // test message 4 assertions
        IPipeMessage message4 = messagesReceived.remove(0);
        Assertions.assertNotNull((IPipeMessage)message4, "Expecting message4 is IPipeMessage");
        Assertions.assertSame(message4, pipe4Message, "Expecting message4 === pipe1Message");
        Assertions.assertEquals(Message.NORMAL, message4.getType(), "Expecting message4.getType == Message.NORMAL");
        Assertions.assertSame(4, ((Prop)message4.getHeader()).value, "Expecting message4.getHeader().testProp = '4'");
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
