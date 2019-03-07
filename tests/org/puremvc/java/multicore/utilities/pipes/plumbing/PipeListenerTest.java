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

/**
 * Test the PipeListener class.
 */
public class PipeListenerTest {

    private class Prop {
        public String value;

        public Prop(String value) {
            this.value = value;
        }
    }

    /**
     * Test connecting a pipe listener to a pipe.
     */
    @Test
    public void testConnectingToAPipe() {
        // create pipe and listener
        IPipeFitting pipe = new Pipe();
        PipeListener listener = new PipeListener(this, this::callBackMethod);

        // connect the listener to the pipe
        boolean success = pipe.connect(listener);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe, "Expecting pipe is Pipe");
        Assertions.assertTrue(success, "Expecting successfully connected listener to pipe");
    }

    /**
     * Test receiving a message from a pipe using a PipeListener.
     */
    @Test
    public void testReceiveMessageViaPipeListener() throws Exception {
        // create a message
        Document doc = (DocumentBuilderFactory.newInstance()).newDocumentBuilder().parse(new InputSource(new StringReader("<testMessage testAtt='Hello'/>")));
        IPipeMessage messageToSend = new Message(Message.NORMAL, new Prop("testval"), doc, Message.PRIORITY_HIGH);

        // create pipe and listener
        IPipeFitting pipe = new Pipe();
        PipeListener listener = new PipeListener(this, this::callBackMethod);

        // connect the listener to the pipe and write the message
        boolean connected = pipe.connect(listener);
        boolean written = pipe.write(messageToSend);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe, "Expecting pipe is Pipe");
        Assertions.assertTrue(connected, "Expecting connected listener to pipe");
        Assertions.assertTrue(written, "Expecting wrote message to pipe");
        Assertions.assertNotNull((Message)messageReceived, "Expecting messageReceived is Message");
        Assertions.assertTrue(messageReceived.getType() == Message.NORMAL, "Expecting messageReceived.getType() == Message.NORMAL");
        Assertions.assertTrue(((Prop)messageReceived.getHeader()).value == "testval", "Expecting messageReceived.getHeader().testProp = 'testval'" );
        Assertions.assertTrue(((Document)messageReceived.getBody()).getDocumentElement().getAttribute("testAtt").equals("Hello"), "Expecting ((Document)messageReceived.getBody()).getDocumentElement().getAttribute('testAtt') == 'Hello'");
        Assertions.assertTrue(messageReceived.getPriority() == Message.PRIORITY_HIGH, "Expecting messageReceived.getPriority() == Message.PRIORITY_HIGH");
    }

    /**
     * Recipient of message.
     * <P>
     * Used by <code>callBackMedhod</code> as a place to store
     * the recieved message.</P>
     */
    private IPipeMessage messageReceived;

    /**
     * Callback given to <code>PipeListener</code> for incoming message.
     * <P>
     * Used by <code>testReceiveMessageViaPipeListener</code>
     * to get the output of pipe back into this  test to see
     * that a message passes through the pipe.</P>
     */
    private void callBackMethod(IPipeMessage message) {
        messageReceived = message;
    }
}
