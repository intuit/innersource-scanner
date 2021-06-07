package com.intuit.innersource.mavenplugin;

import com.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReport;
import com.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReportCommand;
import com.intuit.innersource.reposcanner.loggingservice.LoggingService;
import com.intuit.innersource.reposcanner.repofilepath.local.LocalRepositoryFilePath;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "scan", inheritByDefault = false, defaultPhase = LifecyclePhase.TEST)
public class InnerSourceScanMojo extends AbstractMojo {

    /**
     * The file path to the project's repository root directory, where the README.md file should reside. Defaults to
     * directory of the pom.xml file this plugin was defined.
     */
    @Parameter(name = "repositoryRoot", defaultValue = ".")
    public File repositoryRoot;

    /**
     * True if the build should fail when standard InnerSource documentation is missing or incomplete, false otherwise.
     */
    @Parameter(name = "failBuild", defaultValue = "false")
    public boolean failBuild;

    public void execute() throws MojoExecutionException, MojoFailureException {
        final InnerSourceReadinessReport resultMetrics;
        try {
            final MavenLoggingService mavenLoggingService = new MavenLoggingService(getLog());
            resultMetrics = InnerSourceReadinessReportCommand
                  .create(
                        LocalRepositoryFilePath.of(repositoryRoot.toPath())
                  )
                  .loggingService(mavenLoggingService)
                  .build()
                  .call();
            mavenLoggingService.info(resultMetrics.toJson());
        } catch (final Exception cause) {
            throw new MojoExecutionException("failed to scan repositoryRoot for InnerSource documentation", cause);
        }
        if (failBuild && !resultMetrics.isRepositoryInnerSourceReady()) {
            throw new MojoFailureException(
                  String.format(
                        "Project located at \"%s\" is missing standard InnerSource documentation and "
                        + "\"failBuild\" property was set to true",
                        repositoryRoot.getAbsolutePath()
                  )
            );
        }
    }

    private static final class MavenLoggingService implements LoggingService {

        private final Log mavenLogger;

        private MavenLoggingService(final Log mavenLogger) {
            this.mavenLogger = mavenLogger;
        }

        @Override
        public void info(final String message) {
            mavenLogger.info(message);
        }

        @Override
        public void debug(final String message) {
            mavenLogger.debug(message);
        }

        @Override
        public void trace(final String message) {
            mavenLogger.debug(message);
        }

        @Override
        public void warn(final String warning) {
            mavenLogger.warn(warning);
        }

        @Override
        public void error(final String error) {
            mavenLogger.error(error);
        }
    }
}
