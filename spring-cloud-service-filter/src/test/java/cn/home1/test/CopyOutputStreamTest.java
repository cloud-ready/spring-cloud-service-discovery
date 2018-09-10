package cn.home1.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CopyOutputStreamTest {

    private static final PrintStream originalSystemOut = System.out;
    private CopyOutputStream copyOutputStream;
    private ExecutorService executorService;

    @Before
    public void setUp() {
        this.executorService = Executors.newSingleThreadExecutor();

        this.copyOutputStream = new CopyOutputStream(originalSystemOut);
        System.setOut(new PrintStream(copyOutputStream));
    }

    @After
    public void cleanUp() {
        System.setOut(originalSystemOut);
        this.executorService.shutdown();
    }

    @Test
    public void testNotFound() {
        assertFalse(this.copyOutputStream.waitForLine(".+You will not find this.+", 6));
    }

    @Test
    public void testFound() {
        this.executorService.execute(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            System.out.println("2018-07-20T19:27:08,937 INFO c.h.c.n.e.s.EurekaServer [main] Started EurekaServer in 10.379 seconds (JVM running for 11.552)");
        });

        assertTrue(copyOutputStream.waitForLine(".+Started EurekaServer.+", 8));
    }
}
