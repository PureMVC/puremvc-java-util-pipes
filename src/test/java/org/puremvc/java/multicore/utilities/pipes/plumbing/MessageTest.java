//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2020 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeMessage;
import org.puremvc.java.multicore.utilities.pipes.messages.Message;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * Test the Message class.
 */
public class MessageTest {

    private class Prop {
        public String value;

        public Prop(String value) {
            this.value = value;
        }
    }

    /**
     * Tests the constructor parameters and getters.
     */
    @Test
    public void testConstructorAndGetters() throws Exception {
        // create a message with complete constructor args
        Document doc = (DocumentBuilderFactory.newInstance()).newDocumentBuilder().parse(new InputSource(new StringReader("<testMessage testAtt='Hello'/>")));
        IPipeMessage message = new Message(Message.NORMAL, new Prop("testval"), doc, Message.PRIORITY_HIGH);

        // test assertions
        Assertions.assertNotNull((Message)message, "Expecting message is Message");
        Assertions.assertEquals(Message.NORMAL, message.getType(), "Expecting message.getType() == Message.NORMAL");
        Assertions.assertEquals("testval", ((Prop)message.getHeader()).value, "Expecting messageReceived.getHeader().testProp = 'testval'");
        Assertions.assertEquals("Hello", ((Document)message.getBody()).getDocumentElement().getAttribute("testAtt"), "Expecting ((Document)messageReceived.getBody()).getDocumentElement().getAttribute('testAtt') == 'Hello' ");
        Assertions.assertEquals(Message.PRIORITY_HIGH, message.getPriority(), "Expecing message.getPriority() == Message.PRIORITY_HIGH");
    }

    /**
     * Tests message default priority.
     */
    @Test
    public void testDefaultPriority() {
        // Create a message with minimum constructor args
        IPipeMessage message = new Message(Message.NORMAL);

        // test assertions
        Assertions.assertEquals(Message.PRIORITY_MED, message.getPriority(), "Expecting message.getPriority() == Message.PRIORITY_MED");
    }

    /**
     * Tests the setters and getters.
     */
    @Test
    public void testSettersAndGetters() throws Exception {
        // create a message with minimum constructor args
        IPipeMessage message = new Message(Message.NORMAL);

        // Set remainder via setters
        message.setHeader(new Prop("testval"));
        message.setBody((DocumentBuilderFactory.newInstance()).newDocumentBuilder().parse(new InputSource(new StringReader("<testMessage testAtt='Hello'/>"))));
        message.setPriority(Message.PRIORITY_LOW);

        // test assertions
        Assertions.assertNotNull((Message)message, "Expecting message is Message");
        Assertions.assertEquals(Message.NORMAL, message.getType(), "Expecting message.getType() == Message.NORMAL");
        Assertions.assertEquals("testval", ((Prop)message.getHeader()).value, "Expecting messageReceived.getHeader().testProp = 'testval'");
        Assertions.assertEquals("Hello", ((Document)message.getBody()).getDocumentElement().getAttribute("testAtt"), "Expecting ((Document)messageReceived.getBody()).getDocumentElement().getAttribute('testAtt') == 'Hello' ");
        Assertions.assertEquals(Message.PRIORITY_LOW, message.getPriority(), "Expecing message.getPriority() == Message.PRIORITY_HIGH");
    }
}
