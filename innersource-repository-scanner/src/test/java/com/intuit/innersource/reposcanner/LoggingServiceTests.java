package com.intuit.innersource.reposcanner;

import com.intuit.innersource.reposcanner.loggingservice.console.ConsoleLoggingService;
import com.intuit.innersource.reposcanner.loggingservice.noop.NoopLoggingService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoggingServiceTests {

    private PrintStream preTestStdOut;
    private PrintStream preTestStdErr;
    private ByteArrayOutputStream stdoutStream;
    private ByteArrayOutputStream stderrStream;
    private String logMessage;

    @Before
    public void setUp() {
        preTestStdOut = System.out;
        preTestStdErr = System.err;
        stdoutStream = new ByteArrayOutputStream();
        stderrStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdoutStream));
        System.setErr(new PrintStream(stderrStream));
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(preTestStdOut);
        System.setErr(preTestStdErr);
    }

    @Test
    public void testConsoleLoggingServiceInfoLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_console_at_info_level();

        then_standard_output_receives("INFO: hello world\n");
    }

    @Test
    public void testConsoleLoggingServiceDebugLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_console_at_debug_level();

        then_standard_output_receives("DEBUG: hello world\n");
    }

    @Test
    public void testConsoleLoggingServiceTraceLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_console_at_trace_level();

        then_standard_output_receives("TRACE: hello world\n");
    }

    @Test
    public void testConsoleLoggingServiceWarnLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_console_at_warn_level();

        then_standard_error_receives("WARN: hello world\n");
    }

    @Test
    public void testConsoleLoggingServiceErrorLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_console_at_error_level();

        then_standard_error_receives("ERROR: hello world\n");
    }

    @Test
    public void testNoopLoggingServiceInfoLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_noop_at_info_level();

        then_standard_output_receives(null);
    }

    @Test
    public void testNoopLoggingServiceDebugLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_noop_at_debug_level();

        then_standard_output_receives(null);
    }

    @Test
    public void testNoopLoggingServiceTraceLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_noop_at_trace_level();

        then_standard_output_receives(null);
    }

    @Test
    public void testNoopLoggingServiceWarnLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_noop_at_warn_level();

        then_standard_error_receives(null);
    }

    @Test
    public void testNoopLoggingServiceErrorLevel() throws Exception {
        given_log_message("hello world");

        when_log_to_noop_at_error_level();

        then_standard_error_receives(null);
    }

    private void given_log_message(final String logMessage) {
        this.logMessage = logMessage;
    }

    private void then_standard_output_receives(final String expectedStandardOutput)
        throws Exception {
        if (expectedStandardOutput == null) {
            Assertions.assertThat(stdoutStream.size()).isEqualTo(0);
            return;
        }
        Assertions
            .assertThat(stdoutStream.toString(StandardCharsets.UTF_8.name()))
            .isEqualTo(expectedStandardOutput);
    }

    private void then_standard_error_receives(final String expectedStandardError)
        throws Exception {
        if (expectedStandardError == null) {
            Assertions.assertThat(stderrStream.size()).isEqualTo(0);
            return;
        }
        Assertions
            .assertThat(stderrStream.toString(StandardCharsets.UTF_8.name()))
            .isEqualTo(expectedStandardError);
    }

    private void when_log_to_console_at_info_level() {
        ConsoleLoggingService.INSTANCE.info(logMessage);
    }

    private void when_log_to_console_at_debug_level() {
        ConsoleLoggingService.INSTANCE.debug(logMessage);
    }

    private void when_log_to_console_at_trace_level() {
        ConsoleLoggingService.INSTANCE.trace(logMessage);
    }

    private void when_log_to_console_at_warn_level() {
        ConsoleLoggingService.INSTANCE.warn(logMessage);
    }

    private void when_log_to_console_at_error_level() {
        ConsoleLoggingService.INSTANCE.error(logMessage);
    }

    private void when_log_to_noop_at_info_level() {
        NoopLoggingService.INSTANCE.info(logMessage);
    }

    private void when_log_to_noop_at_debug_level() {
        NoopLoggingService.INSTANCE.debug(logMessage);
    }

    private void when_log_to_noop_at_trace_level() {
        NoopLoggingService.INSTANCE.trace(logMessage);
    }

    private void when_log_to_noop_at_warn_level() {
        NoopLoggingService.INSTANCE.warn(logMessage);
    }

    private void when_log_to_noop_at_error_level() {
        NoopLoggingService.INSTANCE.error(logMessage);
    }
}
