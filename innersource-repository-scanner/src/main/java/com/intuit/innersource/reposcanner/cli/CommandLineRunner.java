package com.intuit.innersource.reposcanner.cli;

import com.google.common.net.InternetDomainName;
import com.intuit.innersource.reposcanner.commands.fixup.FixupFileTemplates;
import com.intuit.innersource.reposcanner.commands.fixup.InnerSourceReadinessFixupCommand;
import com.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReport;
import com.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReportCommand;
import com.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReportCommand.Builder;
import com.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.intuit.innersource.reposcanner.repofilepath.github.GitHubRepositoryPath;
import com.intuit.innersource.reposcanner.repofilepath.local.LocalRepositoryFilePath;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

/**
 * @author Matt Madson
 * @author Shagun Bose
 * @since 1.0.0
 */
@Command(
    mixinStandardHelpOptions = true,
    sortOptions = false,
    versionProvider = VersionProvider.class
)
public final class CommandLineRunner implements Callable<Integer> {

    @Option(
        names = { "-c", "--command" },
        required = true,
        paramLabel = "COMMAND",
        description = "The command to run. Valid values: ${COMPLETION-CANDIDATES}"
    )
    private CommandCliArgument command;

    @Option(
        names = { "-r", "--repository" },
        required = true,
        paramLabel = "REPOSITORY",
        description = "The root directory of a local git repository or the URL of a remote GitHub repository."
    )
    private URI repository;

    @Option(
        names = { "-s", "--spec" },
        paramLabel = "INNERSOURCE READINESS SPEC",
        description = "The expected InnerSource readiness specification json file.",
        converter = InnerSourceReadinessSpecificationCliArgumentConverter.class
    )
    private InnerSourceReadinessSpecification innerSourceReadinessSpec =
        InnerSourceReadinessSpecification.PUBLIC_GITHUB_DEFAULT;

    @Option(
        names = { "-t", "--templates" },
        paramLabel = "FIXUP FILE TEMPLATES",
        description = "Json file containing file templates to use when running the fixup command.",
        converter = FixupFileTemplateCliArgumentConverter.class
    )
    private FixupFileTemplates fixupFileTemplates =
        FixupFileTemplates.PUBLIC_GITHUB_DEFAULT;

    @Option(
        names = { "-a", "--authtoken" },
        paramLabel = "AUTHTOKEN",
        description = "The GitHub OAuth token to use when accessing remote GitHub repositories. " +
        "If not specified authentication information is expected to be present in the " +
        "environment variables."
    )
    private String authToken;

    @Spec
    private CommandSpec cmdlnArgSpec;

    private CommandLineRunner() {}

    @Override
    public Integer call() throws Exception {
        final RepositoryFilePath repoPath = resolveRepositoryFilePath();

        switch (command) {
            case REPORT:
                final Builder reportCommandBuilder = InnerSourceReadinessReportCommand.create(
                    repoPath
                );
                Optional
                    .ofNullable(innerSourceReadinessSpec)
                    .ifPresent(reportCommandBuilder::specification);
                final InnerSourceReadinessReport report = reportCommandBuilder
                    .build()
                    .call();
                System.out.println(report.toJson());
                break;
            case FIXUP:
                final InnerSourceReadinessFixupCommand.Builder fixupCommandBuilder = InnerSourceReadinessFixupCommand.create(
                    repoPath
                );
                Optional
                    .ofNullable(innerSourceReadinessSpec)
                    .ifPresent(fixupCommandBuilder::specification);
                Optional
                    .ofNullable(fixupFileTemplates)
                    .ifPresent(fixupCommandBuilder::fileTemplates);
                fixupCommandBuilder.build().call();
                break;
        }
        return 0;
    }

    private RepositoryFilePath resolveRepositoryFilePath() throws IOException {
        final RepositoryFilePath result;
        if (isRemoteGitHubRepository()) {
            final URL repoUrl = repository.toURL();

            GitHubBuilder gitHubBuilder;
            if (StringUtils.isBlank(authToken)) {
                gitHubBuilder = GitHubBuilder.fromEnvironment();
            } else {
                gitHubBuilder = new GitHubBuilder().withOAuthToken(authToken);
            }

            if (!isRepoHostedOnPublicGitHub(repoUrl)) {
                gitHubBuilder =
                    gitHubBuilder.withEndpoint(
                        StringUtils.removeEndIgnoreCase(
                            repoUrl.toString(),
                            repoUrl.getPath()
                        ) +
                        "/api/v3"
                    );
            }

            final GitHub githubApi = gitHubBuilder.build();

            result =
                GitHubRepositoryPath.of(
                    githubApi.getRepository(
                        StringUtils.removeStart(repoUrl.getPath(), "/")
                    )
                );
        } else {
            final Path localRepoFilePath = Paths.get(repository.getSchemeSpecificPart());
            if (!Files.isDirectory(localRepoFilePath)) {
                throw new ParameterException(
                    cmdlnArgSpec.commandLine(),
                    "local git repository path is not a directory"
                );
            }
            result = LocalRepositoryFilePath.of(localRepoFilePath);
        }
        return result;
    }

    private boolean isRemoteGitHubRepository() {
        return StringUtils.startsWithIgnoreCase(repository.getScheme(), "http");
    }

    private static boolean isRepoHostedOnPublicGitHub(final URL url) {
        return "github.com".equalsIgnoreCase(
                InternetDomainName.from(url.getHost()).topPrivateDomain().toString()
            );
    }

    public static void main(final String... args) {
        final CommandLine cli = new CommandLine(new CommandLineRunner());
        cli.setCommandName(
            BuildInfoService.getInstance().getProperty("application.name")
        );
        cli.setCaseInsensitiveEnumValuesAllowed(true);
        System.exit(cli.execute(args));
    }
}
