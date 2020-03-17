//
//  PureMVC Java Multicore Utility - Pipes
//
//  Copyright(c) 2020 Saad Shams <saad.shams@puremvc.org>
//  Your reuse is governed by the Creative Commons Attribution 3.0 License
//

package org.puremvc.java.multicore.utilities.pipes.plumbing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.puremvc.java.multicore.utilities.pipes.interfaces.IPipeFitting;

/**
 * Test the Pipe class.
 */
public class PipeTest {

    /**
     * Test the constructor.
     */
    @Test
    public void testConstructor() {
        IPipeFitting pipe = new Pipe();

        // test assertions
        Assertions.assertNotNull((Pipe)pipe, "Expecting pipe is Pipe");
    }

    /**
     * Test connecting and disconnecting two pipes.
     */
    @Test
    public void testConnectingAndDisconnectingTwoPipes() {
        // create two pipes
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();

        // connect them
        boolean success = pipe1.connect(pipe2);

        // test assertions
        Assertions.assertNotNull((Pipe)pipe1, "Expecting pipe1 is Pipe");
        Assertions.assertNotNull((Pipe)pipe2, "Expecting pipe2 is Pipe");
        Assertions.assertTrue(success, "Expecting connected pipe1 to pipe2");

        // disconnect pipe 2 from pipe 1
        IPipeFitting disconnectedPipe = pipe1.disconnect();
        Assertions.assertTrue(disconnectedPipe == pipe2, "Expecting disconnected pipe2 from pipe1");
    }

    /**
     * Test attempting to connect a pipe to a pipe with an output already connected.
     */
    @Test
    public void testConnectingToAConnectedPipe() {
        // create three pipes
        IPipeFitting pipe1 = new Pipe();
        IPipeFitting pipe2 = new Pipe();
        IPipeFitting pipe3 = new Pipe();

        // connect them
        boolean success = pipe1.connect(pipe2);

        // test assertions
        Assertions.assertTrue(success, "Expecting connected pipe1 to pipe2");
        Assertions.assertFalse(pipe1.connect(pipe3), "expecting can't connect pipe3 to pipe1");
    }

}
