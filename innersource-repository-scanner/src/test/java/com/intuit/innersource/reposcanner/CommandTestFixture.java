package com.intuit.innersource.reposcanner;

import com.intuit.innersource.reposcanner.loggingservice.LoggingService;
import java.util.Map.Entry;
import java.util.function.Supplier;

public interface CommandTestFixture {
    void given_github_repo(Supplier<Entry<String, Object>>... specSuppliers);

    void given_logging_service(LoggingService scanLogConsumer);

    void when_run_report_command();

    void when_run_fixup_command();

    void then_readiness_report_matches(Object expectedScanResults);

    void then_fixed_files_match(Object... expected);

    void then_log_contains_lines(String... expectedScanLogLines);

    void then_log_warning_count_is(int expectedWarningCount);
}
