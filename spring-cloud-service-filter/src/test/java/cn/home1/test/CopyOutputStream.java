package cn.home1.test;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

@Slf4j
public class CopyOutputStream extends OutputStream {

    private final OutputStream copy;

    private final PipedInputStream in;

    private final OutputStream original;

    @SneakyThrows
    public CopyOutputStream(final OutputStream original) {
        this.original = original;

        this.in = new PipedInputStream();
        this.copy = new PipedOutputStream(this.in);
    }

    @Override
    public void close() throws IOException {
        // never close original stream
        IOUtils.closeQuietly(this.copy);
    }

    @Override
    public void flush() throws IOException {
        this.original.flush();
        this.copy.flush();
    }

    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.in));
    }

    public boolean waitForLine(final String regex, final int timeoutInSeconds) {
        final DateTime from = DateTime.now();
        final DateTime till = from.plusSeconds(timeoutInSeconds);

        try (final BufferedReader streamOutReader = this.getReader()) {
            DateTime now;
            while ((now = DateTime.now()).isBefore(till)) {
                log.trace("waitForLine find {} in log", regex); // output of this line can cause 'false found'

                final TimeLimiter timeLimiter = new SimpleTimeLimiter();
                try {
                    String line;
                    while ((line = timeLimiter.callWithTimeout(streamOutReader::readLine, 2, SECONDS, false)) != null) {
                        if (line.matches(regex)) {
                            log.info("waitForLine found {} in log", regex);
                            return true;
                        }
                    }
                } catch (final InterruptedException ie) {
                    return false;
                } catch (final UncheckedTimeoutException timeout) {
                } catch (final Exception ex) {
                    log.info("waitForLine error", ex);
                }
            }
        } catch (final IOException ignored) {
        }

        log.info("waitForLine {} not found in log", regex);
        return false;
    }

    @Override
    public void write(final int b) throws IOException {
        this.original.write(b);
        this.copy.write(b);
    }

    @Override
    public void write(byte b[]) throws IOException {
        this.original.write(b);
        this.copy.write(b);
    }

    @Override
    public void write(final byte buf[], final int off, final int len) throws IOException {
        this.original.write(buf, off, len);
        this.copy.write(buf, off, len);
    }
}
