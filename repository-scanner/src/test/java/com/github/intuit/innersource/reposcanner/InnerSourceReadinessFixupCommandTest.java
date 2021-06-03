package com.github.intuit.innersource.reposcanner;

import com.github.intuit.innersource.reposcanner.repofilepath.InvalidRepositoryFilePathException;
import org.junit.Test;

public class InnerSourceReadinessFixupCommandTest extends TestFixtures {

    public InnerSourceReadinessFixupCommandTest(
        final CommandTestFixture fixture,
        final String testSuiteName
    ) {
        super(fixture, testSuiteName);
    }

    @Test
    public void givenEmptyRepo_whenFixupCommand_thenStructureCreated() {
        given_github_repo();

        when_run_fixup_command();

        then_fixed_files_match(
            fileWithContents(
                "/README.md",
                "\n" +
                "# USAGE\n" +
                "\n" +
                "\n" +
                "# LOCAL DEVELOPMENT\n" +
                "\n" +
                "\n" +
                "# CONTRIBUTING\n" +
                "\n" +
                "\n" +
                "# SUPPORT\n" +
                "\n"
            ),
            fileWithContents(
                "/CONTRIBUTING.md",
                "\n" +
                "# BEFORE PR\n" +
                "\n" +
                "\n" +
                "# DURING PR\n" +
                "\n" +
                "\n" +
                "# AFTER PR\n" +
                "\n"
            ),
            fileWithContents(
                "/.github/CODEOWNERS",
                "# This is a comment.\n" +
                "# Each line is a file pattern followed by one or more owners.\n" +
                "\n" +
                "# These owners will be the default owners for everything in\n" +
                "# the repo. Unless a later match takes precedence,\n" +
                "# @global-owner1 and @global-owner2 will be requested for\n" +
                "# review when someone opens a pull request.\n" +
                "# *       @global-owner1 @global-owner2\n" +
                "\n" +
                "# Order is important; the last matching pattern takes the most\n" +
                "# precedence. When someone opens a pull request that only\n" +
                "# modifies JS files, only @js-owner and not the global\n" +
                "# owner(s) will be requested for a review.\n" +
                "# *.js    @js-owner\n" +
                "\n" +
                "# You can also use email addresses if you prefer. They'll be\n" +
                "# used to look up users just like we do for commit author\n" +
                "# emails.\n" +
                "# *.go docs@example.com\n" +
                "\n" +
                "# In this example, @doctocat owns any files in the build/logs\n" +
                "# directory at the root of the repository and any of its\n" +
                "# subdirectories.\n" +
                "# /build/logs/ @doctocat\n" +
                "\n" +
                "# The `docs/*` pattern will match files like\n" +
                "# `docs/getting-started.md` but not further nested files like\n" +
                "# `docs/build-app/troubleshooting.md`.\n" +
                "# docs/*  docs@example.com\n" +
                "\n" +
                "# In this example, @octocat owns any file in an apps directory\n" +
                "# anywhere in your repository.\n" +
                "# apps/ @octocat\n" +
                "\n" +
                "# In this example, @doctocat owns any file in the `/docs`\n" +
                "# directory in the root of your repository and any of its\n" +
                "# subdirectories.\n" +
                "# /docs/ @doctocat\n"
            ),
            fileWithContents(
                "/.github/PULL_REQUEST_TEMPLATE.md",
                "# Describe Proposed Changes\n"
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...................................................FOUND\n"
        );
    }

    @Test
    public void givenNullRepoPath_whenCreateDocStructure_thenThrowInvalidWorkspaceException() {
        given_github_repo(null);

        when_run_fixup_command();

        then_fixed_files_match(InvalidRepositoryFilePathException.class);
    }

    @Test
    public void givenNullLoggingService_whenCreateDocStructure_thenThrowNullPointerException() {
        given_github_repo();
        given_logging_service(null);

        when_run_fixup_command();

        then_fixed_files_match(NullPointerException.class);
    }

    @Test
    public void givenRepoContainsEmptyDocStructureFiles_whenCreateDocStructure_thenEmitWarningsAndSkipFileCreation() {
        given_github_repo(
            emptyFile("/.github/CODEOWNERS"),
            emptyFile("/.github/PULL_REQUEST_TEMPLATE.md"),
            emptyFile("/README.md"),
            emptyFile("/CONTRIBUTING.md")
        );

        when_run_fixup_command();

        then_fixed_files_match(
            fileWithContents(
                "/README.md",
                "\n" +
                "# USAGE\n" +
                "\n" +
                "\n" +
                "# LOCAL DEVELOPMENT\n" +
                "\n" +
                "\n" +
                "# CONTRIBUTING\n" +
                "\n" +
                "\n" +
                "# SUPPORT\n" +
                "\n"
            ),
            fileWithContents(
                "/CONTRIBUTING.md",
                "\n" +
                "# BEFORE PR\n" +
                "\n" +
                "\n" +
                "# DURING PR\n" +
                "\n" +
                "\n" +
                "# AFTER PR\n" +
                "\n"
            ),
            fileWithContents(
                "/.github/CODEOWNERS",
                "# This is a comment.\n" +
                "# Each line is a file pattern followed by one or more owners.\n" +
                "\n" +
                "# These owners will be the default owners for everything in\n" +
                "# the repo. Unless a later match takes precedence,\n" +
                "# @global-owner1 and @global-owner2 will be requested for\n" +
                "# review when someone opens a pull request.\n" +
                "# *       @global-owner1 @global-owner2\n" +
                "\n" +
                "# Order is important; the last matching pattern takes the most\n" +
                "# precedence. When someone opens a pull request that only\n" +
                "# modifies JS files, only @js-owner and not the global\n" +
                "# owner(s) will be requested for a review.\n" +
                "# *.js    @js-owner\n" +
                "\n" +
                "# You can also use email addresses if you prefer. They'll be\n" +
                "# used to look up users just like we do for commit author\n" +
                "# emails.\n" +
                "# *.go docs@example.com\n" +
                "\n" +
                "# In this example, @doctocat owns any files in the build/logs\n" +
                "# directory at the root of the repository and any of its\n" +
                "# subdirectories.\n" +
                "# /build/logs/ @doctocat\n" +
                "\n" +
                "# The `docs/*` pattern will match files like\n" +
                "# `docs/getting-started.md` but not further nested files like\n" +
                "# `docs/build-app/troubleshooting.md`.\n" +
                "# docs/*  docs@example.com\n" +
                "\n" +
                "# In this example, @octocat owns any file in an apps directory\n" +
                "# anywhere in your repository.\n" +
                "# apps/ @octocat\n" +
                "\n" +
                "# In this example, @doctocat owns any file in the `/docs`\n" +
                "# directory in the root of your repository and any of its\n" +
                "# subdirectories.\n" +
                "# /docs/ @doctocat\n"
            ),
            fileWithContents(
                "/.github/PULL_REQUEST_TEMPLATE.md",
                "# Describe Proposed Changes\n"
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...................................................FOUND\n"
        );
    }

    @Test
    public void givenRepoContainsNonEmptyDocStructureFiles_whenCreateDocStructure_thenEmitWarningsAndSkipFileCreation() {
        given_github_repo(
            fileWithContents("/.github/CODEOWNERS", "codeowners file"),
            fileWithContents("/.github/PULL_REQUEST_TEMPLATE.md", "pr template"),
            fileWithContents("/README.md", "readme"),
            fileWithContents("/CONTRIBUTING.md", "contributing")
        );

        when_run_fixup_command();

        then_fixed_files_match(
            fileWithContents(
                "/README.md",
                "readme\n" +
                "\n" +
                "# USAGE\n" +
                "\n" +
                "\n" +
                "# LOCAL DEVELOPMENT\n" +
                "\n" +
                "\n" +
                "# CONTRIBUTING\n" +
                "\n" +
                "\n" +
                "# SUPPORT\n" +
                "\n"
            ),
            fileWithContents(
                "/CONTRIBUTING.md",
                "contributing\n" +
                "\n" +
                "# BEFORE PR\n" +
                "\n" +
                "\n" +
                "# DURING PR\n" +
                "\n" +
                "\n" +
                "# AFTER PR\n" +
                "\n"
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...................................................FOUND\n"
        );
    }

    @Test
    public void givenRepoContainsSomeDocStructureFiles_whenCreateDocStructure_thenCreateMissingFiles() {
        given_github_repo(
            fileWithContents("/.github/CODEOWNERS", "codeowners file"),
            fileWithContents("/.github/PULL_REQUEST_TEMPLATE.md", "pr template"),
            fileWithContents("/CONTRIBUTING.md", "contributing")
        );

        when_run_fixup_command();

        then_fixed_files_match(
            fileWithContents(
                "/README.md",
                "\n" +
                "# USAGE\n" +
                "\n" +
                "\n" +
                "# LOCAL DEVELOPMENT\n" +
                "\n" +
                "\n" +
                "# CONTRIBUTING\n" +
                "\n" +
                "\n" +
                "# SUPPORT\n" +
                "\n"
            ),
            fileWithContents(
                "/CONTRIBUTING.md",
                "contributing\n" +
                "\n" +
                "# BEFORE PR\n" +
                "\n" +
                "\n" +
                "# DURING PR\n" +
                "\n" +
                "\n" +
                "# AFTER PR\n" +
                "\n"
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...................................................FOUND\n"
        );
    }
}
