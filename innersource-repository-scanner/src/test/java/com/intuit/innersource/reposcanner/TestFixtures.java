package com.intuit.innersource.reposcanner;

import com.google.common.collect.Maps;
import com.intuit.innersource.reposcanner.loggingservice.LoggingService;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.Supplier;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
abstract class TestFixtures implements CommandTestFixture {

    @Parameters(name = "{index} - {1}")
    public static Collection<Object[]> fixtures() {
        return Arrays.asList(
            new Object[][] {
                { new LocalFileSystemTestFixture(), "Local Filesystem Tests" },
                { new RemoteGitHubTestFixture(), "Remote GitHub Tests" },
            }
        );
    }

    private CommandTestFixture fixture;

    @Rule
    public TestRule fixtureCleanupRules;

    public TestFixtures(final CommandTestFixture fixture, final String testSuiteName) {
        this.fixture = fixture;
        if (fixture instanceof TestRule) {
            this.fixtureCleanupRules = (TestRule) fixture;
        }
    }

    @Override
    public void given_github_repo(
        final Supplier<Entry<String, Object>>... specSuppliers
    ) {
        fixture.given_github_repo(specSuppliers);
    }

    @Override
    public void given_logging_service(final LoggingService scanLogConsumer) {
        fixture.given_logging_service(scanLogConsumer);
    }

    @Override
    public void when_run_report_command() {
        fixture.when_run_report_command();
    }

    @Override
    public void when_run_fixup_command() {
        fixture.when_run_fixup_command();
    }

    @Override
    public void then_readiness_report_matches(final Object expectedScanResults) {
        fixture.then_readiness_report_matches(expectedScanResults);
    }

    @Override
    public void then_fixed_files_match(final Object... expected) {
        fixture.then_fixed_files_match(expected);
    }

    @Override
    public void then_log_contains_lines(final String... expectedScanLogLines) {
        fixture.then_log_contains_lines(expectedScanLogLines);
    }

    @Override
    public void then_log_warning_count_is(final int expectedWarningCount) {
        fixture.then_log_warning_count_is(expectedWarningCount);
    }

    protected Supplier<Entry<String, Object>> emptyFile(final String filePath) {
        return fileWithContents(filePath, null);
    }

    protected Supplier<Entry<String, Object>> fileWithContents(
        final String repoPath,
        Object fileContents
    ) {
        return () -> Maps.immutableEntry(repoPath, fileContents);
    }

    protected URL classpath(final String classpathLocation) {
        return TestFixtures.class.getResource(classpathLocation);
    }
}
