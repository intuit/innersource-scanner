package com.intuit.innersource.reposcanner;

import com.intuit.innersource.reposcanner.repofilepath.InvalidRepositoryFilePathException;
import org.junit.Test;

public class InnerSourceReadinessReportCommandTest extends TestFixtures {

    public InnerSourceReadinessReportCommandTest(
        final CommandTestFixture fixture,
        final String testSuiteName
    ) {
        super(fixture, testSuiteName);
    }

    @Test
    public void givenNullRepoDirectory_whenScanRepo_thenThrowInvalidWorkspaceException() {
        given_github_repo(null);

        when_run_report_command();

        then_readiness_report_matches(InvalidRepositoryFilePathException.class);
    }

    @Test
    public void givenNullScanLogConsumer_whenScanRepo_thenThrowNullPointerException() {
        given_github_repo();
        given_logging_service(null);

        when_run_report_command();

        then_readiness_report_matches(NullPointerException.class);
    }

    @Test
    public void givenEmptyRepo_whenScanRepo_thenAllMetricsNotFound() {
        given_github_repo();

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenRepoWithEmptyDocStructureFiles_whenScanRepo_thenAllMetricsNotFound_andScanLogContainsEmptyFileWarnings() {
        given_github_repo(
            emptyFile("/.github/CODEOWNERS"),
            emptyFile("/.github/PULL_REQUEST_TEMPLATE.md"),
            emptyFile("/README.md"),
            emptyFile("/CONTRIBUTING.md")
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenStandardDocStructurePresent_whenScanRepo_thenAllStructureMetricsFound() {
        given_github_repo(
            fileWithContents("/.github/CODEOWNERS", "codeowners file"),
            fileWithContents("/.github/PULL_REQUEST_TEMPLATE.md", "pr template"),
            fileWithContents("/README.md", "readme"),
            fileWithContents("/CONTRIBUTING.md", "contributing")
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

        then_log_warning_count_is(0);
    }

    @Test
    public void givenFilesInNonStandardLocations_whenScanRepo_thenDocStructureFound_andWarningsInScanLog() {
        given_github_repo(
            fileWithContents("/.github/README.md", "readme"),
            fileWithContents("/CODEOWNERS", "codeowners file"),
            fileWithContents("/docs/PULL_REQUEST_TEMPLATE", "pr template"),
            fileWithContents("/docs/CONTRIBUTING.md", "contributing")
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

        then_log_warning_count_is(0);
    }

    @Test
    public void givenNonStandardCasing_whenScanRepo_thenDocStructureFilesFound_andWarningsInScanLog() {
        given_github_repo(
            fileWithContents("/.GITHUB/CONTRIBUTING.txt", "contributing file"),
            fileWithContents("/.github/codeowners", "contributing file"),
            fileWithContents("/docs/PULL_REQUEST_TEMPLATE", "pr template"),
            fileWithContents("/DOCS/ReAdMe", "readme")
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

        then_log_warning_count_is(0);
    }

    @Test
    public void givenCodeownersFileWithDefaultRule_whenScanRepo_thenCodeownersDefaultRuleFound() {
        given_github_repo(fileWithContents("/.github/CODEOWNERS", "* @mmadson"));

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule....................................................FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenCodeownersFileWithCommentedOutDefaultRule_whenScanRepo_thenCodeownersDefaultRuleNotFound() {
        given_github_repo(fileWithContents("/.github/CODEOWNERS", "# * @mmadson"));

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenCodeownersFileDefaultRuleHasLeadingWhitepsace_whenScanRepo_thenDefaultRuleFound() {
        given_github_repo(
            fileWithContents("/.github/CODEOWNERS", "    * @mmadson @ahernandez1")
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule....................................................FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenMultilineCodeownersFile_whenScanRepo_thenDefaultRuleFound() {
        given_github_repo(
            fileWithContents("/.github/CODEOWNERS", classpath("/codeowners/CODEOWNERS-1"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule....................................................FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTitleOnly_whenScanRepo_thenReadmeTitleFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-1.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTitleCommentHint_whenScanRepo_thenReadmeTitleFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-17.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTitleAndDescription_whenScanRepo_thenReadmeTitleAndDescriptionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-2.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTitleAndListDescription_whenScanRepo_thenReadmeTitleAndDescriptionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-35.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTitleAndLinkDescription_whenScanRepo_thenReadmeTitleAndDescriptionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-36.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTitleCommentHintAndDescriptionFollowingNonStandardTitle_whenScanRepo_thenReadmeTitleAndDescriptionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-18.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTitleCommentHintAndNoDescription_whenScanRepo_thenReadmeTitleFoundAndDescriptionNotFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-19.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTitleCommentHintAndDescriptionCommentHint_whenScanRepo_thenReadmeTitleAndDescriptionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-20.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithBothBadges_whenScanRepo_thenReadmeBuildStatusBadgesFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-3.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithOnlyBuildBadge_whenScanRepo_thenReadmeBuildStatusBadgesNotFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-4.md"))
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
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTwoBadgeCommentHints_whenScanRepo_thenReadmeBuildStatusBadgesFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-12.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithTwoBadgeCommentHintsUsingHintAliases_whenScanRepo_thenReadmeBuildStatusBadgesFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-11.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithBuildStatusBadgeAndCodeCoverageCommentHint_whenScanRepo_thenReadmeBuildStatusBadgesFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-13.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithBuildStatusBadgeAndCodeCoverageCommentHintUsingAlias_whenScanRepo_thenReadmeBuildStatusBadgesFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-14.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithCodeCoverageBadgeAndBuildStatusCommentHint_whenScanRepo_thenReadmeBuildStatusBadgesFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-15.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithCodeCoverageBadgeAndBuildStatusCommentHintUsingAlias_whenScanRepo_thenReadmeBuildStatusBadgesFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-16.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithOnlyCodeCoverageBadge_whenScanRepo_thenReadmeBuildStatusBadgesNotFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-5.md"))
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
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithStandardUsageSection_whenScanRepo_thenReadmeUsageSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-6.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section....................................................FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithEmptyUsageSection_whenScanRepo_thenReadmeUsageSectionNotFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-7.md"))
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
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithBoldUsageSection_whenScanRepo_thenReadmeUsageSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-8.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section....................................................FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithNonStandardUsageSectionAndCommentHint_whenScanRepo_thenReadmeUsageSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-9.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section....................................................FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithNestedUsageSectionContent_whenScanRepo_thenReadmeUsageSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-10.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section....................................................FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithStandardLocalDevelopmentSection_whenScanRepo_thenReadmeLocalDevelopmentSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-21.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithEmptyLocalDevelopmentSection_whenScanRepo_thenReadmeLocalDevelopmentSectionNotFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-22.md"))
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
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithBoldLocalDevelopmentSection_whenScanRepo_thenReadmeLocalDevelopmentSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-23.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithNestedLocalDevelopmentSection_whenScanRepo_thenReadmeLocalDevelopmentSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-24.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithLinkHeadingLocalDevelopmentSection_whenScanRepo_thenReadmeLocalDevelopmentSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-25.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithLocalDevelopmentCommentHint_whenScanRepo_thenReadmeLocalDevelopmentSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-29.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithStandardContributingSection_whenScanRepo_thenReadmeContributingSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-26.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.............................................FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithMixedCaseContributingSection_whenScanRepo_thenReadmeContributingSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-27.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.............................................FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithEmptyContributingSection_whenScanRepo_thenReadmeContributingSectionNotFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-28.md"))
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
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithContributingCommentHint_whenScanRepo_thenReadmeContributingSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-30.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.............................................FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithStandardSupportSection_whenScanRepo_thenReadmeSupportSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-31.md"))
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
            "README.md Support Section..................................................FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithEmptySupportSection_whenScanRepo_thenReadmeSupportSectionNotFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-32.md"))
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
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithSupportSectionCommentHint_whenScanRepo_thenReadmeSupportSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-33.md"))
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
            "README.md Support Section..................................................FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenReadmeWithSupportSectionContainingExtraWhitepsace_whenScanRepo_thenReadmeSupportSectionFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-34.md"))
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
            "README.md Support Section..................................................FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenCompleteReadme_whenScanRepo_thenAllReadmeMetricsFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/README-COMPLETE.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section....................................................FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.............................................FOUND\n" +
            "README.md Support Section..................................................FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenCompleteReadmeUsingCommentHints_whenScanRepo_thenAllReadmeMetricsFound() {
        given_github_repo(
            fileWithContents(
                "/README.md",
                classpath("/readmes/README-COMPLETE-COMMENT-HINTS.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section....................................................FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.............................................FOUND\n" +
            "README.md Support Section..................................................FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenComplexReadme1_whenScanRepo_thenExpectedReadmeMetricsFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/COMPLEX-README-1.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.............................................FOUND\n" +
            "README.md Support Section..................................................FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenComplexReadme2_whenScanRepo_thenExpectedReadmeMetricsFound() {
        given_github_repo(
            fileWithContents("/README.md", classpath("/readmes/COMPLEX-README-2.md"))
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section....................................................FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.............................................FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md........................................................NOT_FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithBeforePrSection_whenScanRepo_thenBeforePrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-1.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithEmptyBeforePrSection_whenScanRepo_thenBeforePrSectionNotFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-2.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithBoldLinkBeforePrSection_whenScanRepo_thenBeforePrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-3.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithBeforePrSectionUsingSynonym_whenScanRepo_thenBeforePrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-4.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithBeforePrSectionCommentHint_whenScanRepo_thenBeforePrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-5.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithDuringPrSection_whenScanRepo_thenDuringPrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-6.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithDuringPrSectionSynonym_whenScanRepo_thenDuringPrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-7.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithDuringPrSectionCommentHint_whenScanRepo_thenDuringPrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-8.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md After PR Section.......................................NOT_FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithAfterPrSection_whenScanRepo_thenAfterPrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-9.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section...........................................FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithAfterPrSectionSynonym_whenScanRepo_thenAfterPrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-10.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section...........................................FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenContributingWithAfterPrSectionCommentHint_whenScanRepo_thenAfterPrSectionFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-11.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md During PR Section......................................NOT_FOUND\n" +
            "CONTRIBUTING.md After PR Section...........................................FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenCompleteContributing_whenScanRepo_thenAllContributingMetricsFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-COMPLETE.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md During PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md After PR Section...........................................FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenCompleteContributingUsingCommentHints_whenScanRepo_thenAllContributingMetricsFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/CONTRIBUTING-COMPLETE-COMMENT-HINTS.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md During PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md After PR Section...........................................FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenComplexContributing_whenScanRepo_thenExpectedMetricsFound() {
        given_github_repo(
            fileWithContents(
                "/CONTRIBUTING.md",
                classpath("/contributings/COMPLEX-CONTRIBUTING-1.md")
            )
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..............................................................NOT_FOUND\n" +
            "README.md Title........................................................NOT_FOUND\n" +
            "README.md Description..................................................NOT_FOUND\n" +
            "README.md Build Status Badges..........................................NOT_FOUND\n" +
            "README.md Usage Section................................................NOT_FOUND\n" +
            "README.md Local Development Section....................................NOT_FOUND\n" +
            "README.md Contributing Section.........................................NOT_FOUND\n" +
            "README.md Support Section..............................................NOT_FOUND\n" +
            "CODEOWNERS.............................................................NOT_FOUND\n" +
            "CODEOWNERS Default Rule................................................NOT_FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md During PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md After PR Section...........................................FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...............................................NOT_FOUND\n"
        );

        then_log_warning_count_is(0);
    }

    @Test
    public void givenMultipleWarningsWithCompleteNonStandardDocuments_whenScanRepo_thenAllMetricsFound_andAllWarningsInScanLog() {
        given_github_repo(
            fileWithContents(
                "/.GITHUB/README.MD",
                classpath("/readmes/README-COMPLETE.md")
            ),
            fileWithContents("/.GITHUB/README.md", "/.GITHUB/README.md"),
            fileWithContents("/.GITHUB/ReAdMe.MD", "/.GITHUB/ReAdMe.MD"),
            fileWithContents("/.GITHUB/readme.md", "/.GITHUB/README.md"),
            fileWithContents("/.GITHUB/README", "/.GITHUB/README"),
            fileWithContents("/.GITHUB/ReAdMe", "/.GITHUB/ReAdMe"),
            fileWithContents("/.GITHUB/readme", "/.GITHUB/README"),
            fileWithContents("/.GITHUB/README.TXT", "/.GITHUB/README.TXT"),
            fileWithContents("/.GITHUB/README.txt", "/.GITHUB/README.txt"),
            fileWithContents("/.GITHUB/ReAdMe.txt", "/.GITHUB/ReAdMe.txt"),
            fileWithContents("/.GITHUB/readme.txt", "/.GITHUB/README.txt"),
            emptyFile("/.GITHUB/README.EMPTY"),
            emptyFile("/.GITHUB/README.empty"),
            emptyFile("/.GITHUB/ReAdMe.empty"),
            emptyFile("/.GITHUB/readme.empty"),
            fileWithContents("/.github/README.MD", "/.github/README.MD"),
            fileWithContents("/.github/README.md", "/.github/README.md"),
            fileWithContents("/.github/ReAdMe.MD", "/.github/ReAdMe.MD"),
            fileWithContents("/.github/readme.md", "/.github/README.md"),
            fileWithContents("/.github/README", "/.github/README"),
            fileWithContents("/.github/ReAdMe", "/.github/ReAdMe"),
            fileWithContents("/.github/readme", "/.github/README"),
            fileWithContents("/.github/README.TXT", "/.github/README.TXT"),
            fileWithContents("/.github/README.txt", "/.github/README.txt"),
            fileWithContents("/.github/ReAdMe.txt", "/.github/ReAdMe.txt"),
            fileWithContents("/.github/readme.txt", "/.github/README.txt"),
            emptyFile("/.github/README.EMPTY"),
            emptyFile("/.github/README.empty"),
            emptyFile("/.github/ReAdMe.empty"),
            emptyFile("/.github/readme.empty"),
            fileWithContents("/README.MD", "/README.MD"),
            fileWithContents("/README.md", "/README.md"),
            fileWithContents("/ReAdMe.MD", "/ReAdMe.MD"),
            fileWithContents("/readme.md", "/README.md"),
            fileWithContents("/README", "/README"),
            fileWithContents("/ReAdMe", "/ReAdMe"),
            fileWithContents("/readme", "/README"),
            fileWithContents("/README.TXT", "/README.TXT"),
            fileWithContents("/README.txt", "/README.txt"),
            fileWithContents("/ReAdMe.txt", "/ReAdMe.txt"),
            fileWithContents("/readme.txt", "/README.txt"),
            emptyFile("/README.EMPTY"),
            emptyFile("/README.empty"),
            emptyFile("/ReAdMe.empty"),
            emptyFile("/readme.empty"),
            fileWithContents("/DOCS/README.MD", "/DOCS/README.MD"),
            fileWithContents("/DOCS/README.md", "/DOCS/README.md"),
            fileWithContents("/DOCS/ReAdMe.MD", "/DOCS/ReAdMe.MD"),
            fileWithContents("/DOCS/readme.md", "/DOCS/README.md"),
            fileWithContents("/DOCS/README", "/DOCS/README"),
            fileWithContents("/DOCS/ReAdMe", "/DOCS/ReAdMe"),
            fileWithContents("/DOCS/readme", "/DOCS/README"),
            fileWithContents("/DOCS/README.TXT", "/DOCS/README.TXT"),
            fileWithContents("/DOCS/README.txt", "/DOCS/README.txt"),
            fileWithContents("/DOCS/ReAdMe.txt", "/DOCS/ReAdMe.txt"),
            fileWithContents("/DOCS/readme.txt", "/DOCS/README.txt"),
            emptyFile("/DOCS/README.EMPTY"),
            emptyFile("/DOCS/README.empty"),
            emptyFile("/DOCS/ReAdMe.empty"),
            emptyFile("/DOCS/readme.empty"),
            fileWithContents("/docs/README.MD", "/docs/README.MD"),
            fileWithContents("/docs/README.md", "/docs/README.md"),
            fileWithContents("/docs/ReAdMe.MD", "/docs/ReAdMe.MD"),
            fileWithContents("/docs/readme.md", "/docs/README.md"),
            fileWithContents("/docs/README", "/docs/README"),
            fileWithContents("/docs/ReAdMe", "/docs/ReAdMe"),
            fileWithContents("/docs/readme", "/docs/README"),
            fileWithContents("/docs/README.TXT", "/docs/README.TXT"),
            fileWithContents("/docs/README.txt", "/docs/README.txt"),
            fileWithContents("/docs/ReAdMe.txt", "/docs/ReAdMe.txt"),
            fileWithContents("/docs/readme.txt", "/docs/README.txt"),
            emptyFile("/docs/README.EMPTY"),
            emptyFile("/docs/README.empty"),
            emptyFile("/docs/ReAdMe.empty"),
            emptyFile("/docs/readme.empty"),
            fileWithContents("/.GITHUB/CODEOWNERS.MD", "* @mmadson"),
            fileWithContents("/.GITHUB/CODEOWNERS.md", "/.GITHUB/CODEOWNERS.md"),
            fileWithContents("/.GITHUB/CODEowners.MD", "/.GITHUB/CODEowners.MD"),
            fileWithContents("/.GITHUB/codeowners.md", "/.GITHUB/CODEOWNERS.md"),
            fileWithContents("/.GITHUB/CODEOWNERS", "/.GITHUB/CODEOWNERS"),
            fileWithContents("/.GITHUB/CODEowners", "/.GITHUB/CODEowners"),
            fileWithContents("/.GITHUB/codeowners", "/.GITHUB/CODEOWNERS"),
            fileWithContents("/.GITHUB/CODEOWNERS.TXT", "/.GITHUB/CODEOWNERS.TXT"),
            fileWithContents("/.GITHUB/CODEOWNERS.txt", "/.GITHUB/CODEOWNERS.txt"),
            fileWithContents("/.GITHUB/CODEowners.txt", "/.GITHUB/CODEowners.txt"),
            fileWithContents("/.GITHUB/codeowners.txt", "/.GITHUB/CODEOWNERS.txt"),
            emptyFile("/.GITHUB/CODEOWNERS.EMPTY"),
            emptyFile("/.GITHUB/CODEOWNERS.empty"),
            emptyFile("/.GITHUB/CODEowners.empty"),
            emptyFile("/.GITHUB/codeowners.empty"),
            fileWithContents("/.github/CODEOWNERS.MD", "/.github/CODEOWNERS.MD"),
            fileWithContents("/.github/CODEOWNERS.md", "/.github/CODEOWNERS.md"),
            fileWithContents("/.github/CODEowners.MD", "/.github/CODEowners.MD"),
            fileWithContents("/.github/codeowners.md", "/.github/CODEOWNERS.md"),
            fileWithContents("/.github/CODEOWNERS", "/.github/CODEOWNERS"),
            fileWithContents("/.github/CODEowners", "/.github/CODEowners"),
            fileWithContents("/.github/codeowners", "/.github/CODEOWNERS"),
            fileWithContents("/.github/CODEOWNERS.TXT", "/.github/CODEOWNERS.TXT"),
            fileWithContents("/.github/CODEOWNERS.txt", "/.github/CODEOWNERS.txt"),
            fileWithContents("/.github/CODEowners.txt", "/.github/CODEowners.txt"),
            fileWithContents("/.github/codeowners.txt", "/.github/CODEOWNERS.txt"),
            emptyFile("/.github/CODEOWNERS.EMPTY"),
            emptyFile("/.github/CODEOWNERS.empty"),
            emptyFile("/.github/CODEowners.empty"),
            emptyFile("/.github/codeowners.empty"),
            fileWithContents("/CODEOWNERS.MD", "/CODEOWNERS.MD"),
            fileWithContents("/CODEOWNERS.md", "/CODEOWNERS.md"),
            fileWithContents("/CODEowners.MD", "/CODEowners.MD"),
            fileWithContents("/codeowners.md", "/CODEOWNERS.md"),
            fileWithContents("/CODEOWNERS", "/CODEOWNERS"),
            fileWithContents("/CODEowners", "/CODEowners"),
            fileWithContents("/codeowners", "/CODEOWNERS"),
            fileWithContents("/CODEOWNERS.TXT", "/CODEOWNERS.TXT"),
            fileWithContents("/CODEOWNERS.txt", "/CODEOWNERS.txt"),
            fileWithContents("/CODEowners.txt", "/CODEowners.txt"),
            fileWithContents("/codeowners.txt", "/CODEOWNERS.txt"),
            emptyFile("/CODEOWNERS.EMPTY"),
            emptyFile("/CODEOWNERS.empty"),
            emptyFile("/CODEowners.empty"),
            emptyFile("/codeowners.empty"),
            fileWithContents("/DOCS/CODEOWNERS.MD", "/DOCS/CODEOWNERS.MD"),
            fileWithContents("/DOCS/CODEOWNERS.md", "/DOCS/CODEOWNERS.md"),
            fileWithContents("/DOCS/CODEowners.MD", "/DOCS/CODEowners.MD"),
            fileWithContents("/DOCS/codeowners.md", "/DOCS/CODEOWNERS.md"),
            fileWithContents("/DOCS/CODEOWNERS", "/DOCS/CODEOWNERS"),
            fileWithContents("/DOCS/CODEowners", "/DOCS/CODEowners"),
            fileWithContents("/DOCS/codeowners", "/DOCS/CODEOWNERS"),
            fileWithContents("/DOCS/CODEOWNERS.TXT", "/DOCS/CODEOWNERS.TXT"),
            fileWithContents("/DOCS/CODEOWNERS.txt", "/DOCS/CODEOWNERS.txt"),
            fileWithContents("/DOCS/CODEowners.txt", "/DOCS/CODEowners.txt"),
            fileWithContents("/DOCS/codeowners.txt", "/DOCS/CODEOWNERS.txt"),
            emptyFile("/DOCS/CODEOWNERS.EMPTY"),
            emptyFile("/DOCS/CODEOWNERS.empty"),
            emptyFile("/DOCS/CODEowners.empty"),
            emptyFile("/DOCS/codeowners.empty"),
            fileWithContents("/docs/CODEOWNERS.MD", "/docs/CODEOWNERS.MD"),
            fileWithContents("/docs/CODEOWNERS.md", "/docs/CODEOWNERS.md"),
            fileWithContents("/docs/CODEowners.MD", "/docs/CODEowners.MD"),
            fileWithContents("/docs/codeowners.md", "/docs/CODEOWNERS.md"),
            fileWithContents("/docs/CODEOWNERS", "/docs/CODEOWNERS"),
            fileWithContents("/docs/CODEowners", "/docs/CODEowners"),
            fileWithContents("/docs/codeowners", "/docs/CODEOWNERS"),
            fileWithContents("/docs/CODEOWNERS.TXT", "/docs/CODEOWNERS.TXT"),
            fileWithContents("/docs/CODEOWNERS.txt", "/docs/CODEOWNERS.txt"),
            fileWithContents("/docs/CODEowners.txt", "/docs/CODEowners.txt"),
            fileWithContents("/docs/codeowners.txt", "/docs/CODEOWNERS.txt"),
            emptyFile("/docs/CODEOWNERS.EMPTY"),
            emptyFile("/docs/CODEOWNERS.empty"),
            emptyFile("/docs/CODEowners.empty"),
            emptyFile("/docs/codeowners.empty"),
            fileWithContents(
                "/.GITHUB/CONTRIBUTING.MD",
                classpath("/contributings/CONTRIBUTING-COMPLETE.md")
            ),
            fileWithContents("/.GITHUB/CONTRIBUTING.md", "/.GITHUB/CONTRIBUTING.md"),
            fileWithContents("/.GITHUB/CONtributing.MD", "/.GITHUB/CONtributing.MD"),
            fileWithContents("/.GITHUB/contributing.md", "/.GITHUB/CONTRIBUTING.md"),
            fileWithContents("/.GITHUB/CONTRIBUTING", "/.GITHUB/CONTRIBUTING"),
            fileWithContents("/.GITHUB/CONtributing", "/.GITHUB/CONtributing"),
            fileWithContents("/.GITHUB/contributing", "/.GITHUB/CONTRIBUTING"),
            fileWithContents("/.GITHUB/CONTRIBUTING.TXT", "/.GITHUB/CONTRIBUTING.TXT"),
            fileWithContents("/.GITHUB/CONTRIBUTING.txt", "/.GITHUB/CONTRIBUTING.txt"),
            fileWithContents("/.GITHUB/CONtributing.txt", "/.GITHUB/CONtributing.txt"),
            fileWithContents("/.GITHUB/contributing.txt", "/.GITHUB/CONTRIBUTING.txt"),
            emptyFile("/.GITHUB/CONTRIBUTING.EMPTY"),
            emptyFile("/.GITHUB/CONTRIBUTING.empty"),
            emptyFile("/.GITHUB/CONtributing.empty"),
            emptyFile("/.GITHUB/contributing.empty"),
            fileWithContents("/.github/CONTRIBUTING.MD", "/.github/CONTRIBUTING.MD"),
            fileWithContents("/.github/CONTRIBUTING.md", "/.github/CONTRIBUTING.md"),
            fileWithContents("/.github/CONtributing.MD", "/.github/CONtributing.MD"),
            fileWithContents("/.github/contributing.md", "/.github/CONTRIBUTING.md"),
            fileWithContents("/.github/CONTRIBUTING", "/.github/CONTRIBUTING"),
            fileWithContents("/.github/CONtributing", "/.github/CONtributing"),
            fileWithContents("/.github/contributing", "/.github/CONTRIBUTING"),
            fileWithContents("/.github/CONTRIBUTING.TXT", "/.github/CONTRIBUTING.TXT"),
            fileWithContents("/.github/CONTRIBUTING.txt", "/.github/CONTRIBUTING.txt"),
            fileWithContents("/.github/CONtributing.txt", "/.github/CONtributing.txt"),
            fileWithContents("/.github/contributing.txt", "/.github/CONTRIBUTING.txt"),
            emptyFile("/.github/CONTRIBUTING.EMPTY"),
            emptyFile("/.github/CONTRIBUTING.empty"),
            emptyFile("/.github/CONtributing.empty"),
            emptyFile("/.github/contributing.empty"),
            fileWithContents("/CONTRIBUTING.MD", "/CONTRIBUTING.MD"),
            fileWithContents("/CONTRIBUTING.md", "/CONTRIBUTING.md"),
            fileWithContents("/CONtributing.MD", "/CONtributing.MD"),
            fileWithContents("/contributing.md", "/CONTRIBUTING.md"),
            fileWithContents("/CONTRIBUTING", "/CONTRIBUTING"),
            fileWithContents("/CONtributing", "/CONtributing"),
            fileWithContents("/contributing", "/CONTRIBUTING"),
            fileWithContents("/CONTRIBUTING.TXT", "/CONTRIBUTING.TXT"),
            fileWithContents("/CONTRIBUTING.txt", "/CONTRIBUTING.txt"),
            fileWithContents("/CONtributing.txt", "/CONtributing.txt"),
            fileWithContents("/contributing.txt", "/CONTRIBUTING.txt"),
            emptyFile("/CONTRIBUTING.EMPTY"),
            emptyFile("/CONTRIBUTING.empty"),
            emptyFile("/CONtributing.empty"),
            emptyFile("/contributing.empty"),
            fileWithContents("/DOCS/CONTRIBUTING.MD", "/DOCS/CONTRIBUTING.MD"),
            fileWithContents("/DOCS/CONTRIBUTING.md", "/DOCS/CONTRIBUTING.md"),
            fileWithContents("/DOCS/CONtributing.MD", "/DOCS/CONtributing.MD"),
            fileWithContents("/DOCS/contributing.md", "/DOCS/CONTRIBUTING.md"),
            fileWithContents("/DOCS/CONTRIBUTING", "/DOCS/CONTRIBUTING"),
            fileWithContents("/DOCS/CONtributing", "/DOCS/CONtributing"),
            fileWithContents("/DOCS/contributing", "/DOCS/CONTRIBUTING"),
            fileWithContents("/DOCS/CONTRIBUTING.TXT", "/DOCS/CONTRIBUTING.TXT"),
            fileWithContents("/DOCS/CONTRIBUTING.txt", "/DOCS/CONTRIBUTING.txt"),
            fileWithContents("/DOCS/CONtributing.txt", "/DOCS/CONtributing.txt"),
            fileWithContents("/DOCS/contributing.txt", "/DOCS/CONTRIBUTING.txt"),
            emptyFile("/DOCS/CONTRIBUTING.EMPTY"),
            emptyFile("/DOCS/CONTRIBUTING.empty"),
            emptyFile("/DOCS/CONtributing.empty"),
            emptyFile("/DOCS/contributing.empty"),
            fileWithContents("/docs/CONTRIBUTING.MD", "/docs/CONTRIBUTING.MD"),
            fileWithContents("/docs/CONTRIBUTING.md", "/docs/CONTRIBUTING.md"),
            fileWithContents("/docs/CONtributing.MD", "/docs/CONtributing.MD"),
            fileWithContents("/docs/contributing.md", "/docs/CONTRIBUTING.md"),
            fileWithContents("/docs/CONTRIBUTING", "/docs/CONTRIBUTING"),
            fileWithContents("/docs/CONtributing", "/docs/CONtributing"),
            fileWithContents("/docs/contributing", "/docs/CONTRIBUTING"),
            fileWithContents("/docs/CONTRIBUTING.TXT", "/docs/CONTRIBUTING.TXT"),
            fileWithContents("/docs/CONTRIBUTING.txt", "/docs/CONTRIBUTING.txt"),
            fileWithContents("/docs/CONtributing.txt", "/docs/CONtributing.txt"),
            fileWithContents("/docs/contributing.txt", "/docs/CONTRIBUTING.txt"),
            emptyFile("/docs/CONTRIBUTING.EMPTY"),
            emptyFile("/docs/CONTRIBUTING.empty"),
            emptyFile("/docs/CONtributing.empty"),
            emptyFile("/docs/contributing.empty"),
            fileWithContents("/.GITHUB/PULL_REQUEST_TEMPLATE.MD", "Describe Your Change"),
            fileWithContents(
                "/.GITHUB/PULL_REQUEST_TEMPLATE.md",
                "/.GITHUB/PULL_REQUEST_TEMPLATE.md"
            ),
            fileWithContents(
                "/.GITHUB/Pull_Request_Template.MD",
                "/.GITHUB/Pull_Request_Template.MD"
            ),
            fileWithContents(
                "/.GITHUB/pull_request_template.md",
                "/.GITHUB/PULL_REQUEST_TEMPLATE.md"
            ),
            fileWithContents(
                "/.GITHUB/PULL_REQUEST_TEMPLATE",
                "/.GITHUB/PULL_REQUEST_TEMPLATE"
            ),
            fileWithContents(
                "/.GITHUB/Pull_Request_Template",
                "/.GITHUB/Pull_Request_Template"
            ),
            fileWithContents(
                "/.GITHUB/pull_request_template",
                "/.GITHUB/PULL_REQUEST_TEMPLATE"
            ),
            fileWithContents(
                "/.GITHUB/PULL_REQUEST_TEMPLATE.TXT",
                "/.GITHUB/PULL_REQUEST_TEMPLATE.TXT"
            ),
            fileWithContents(
                "/.GITHUB/PULL_REQUEST_TEMPLATE.txt",
                "/.GITHUB/PULL_REQUEST_TEMPLATE.txt"
            ),
            fileWithContents(
                "/.GITHUB/Pull_Request_Template.txt",
                "/.GITHUB/Pull_Request_Template.txt"
            ),
            fileWithContents(
                "/.GITHUB/pull_request_template.txt",
                "/.GITHUB/PULL_REQUEST_TEMPLATE.txt"
            ),
            emptyFile("/.GITHUB/PULL_REQUEST_TEMPLATE.EMPTY"),
            emptyFile("/.GITHUB/PULL_REQUEST_TEMPLATE.empty"),
            emptyFile("/.GITHUB/Pull_Request_Template.empty"),
            emptyFile("/.GITHUB/pull_request_template.empty"),
            fileWithContents(
                "/.github/PULL_REQUEST_TEMPLATE.MD",
                "/.github/PULL_REQUEST_TEMPLATE.MD"
            ),
            fileWithContents(
                "/.github/PULL_REQUEST_TEMPLATE.md",
                "/.github/PULL_REQUEST_TEMPLATE.md"
            ),
            fileWithContents(
                "/.github/Pull_Request_Template.MD",
                "/.github/Pull_Request_Template.MD"
            ),
            fileWithContents(
                "/.github/pull_request_template.md",
                "/.github/PULL_REQUEST_TEMPLATE.md"
            ),
            fileWithContents(
                "/.github/PULL_REQUEST_TEMPLATE",
                "/.github/PULL_REQUEST_TEMPLATE"
            ),
            fileWithContents(
                "/.github/Pull_Request_Template",
                "/.github/Pull_Request_Template"
            ),
            fileWithContents(
                "/.github/pull_request_template",
                "/.github/PULL_REQUEST_TEMPLATE"
            ),
            fileWithContents(
                "/.github/PULL_REQUEST_TEMPLATE.TXT",
                "/.github/PULL_REQUEST_TEMPLATE.TXT"
            ),
            fileWithContents(
                "/.github/PULL_REQUEST_TEMPLATE.txt",
                "/.github/PULL_REQUEST_TEMPLATE.txt"
            ),
            fileWithContents(
                "/.github/Pull_Request_Template.txt",
                "/.github/Pull_Request_Template.txt"
            ),
            fileWithContents(
                "/.github/pull_request_template.txt",
                "/.github/PULL_REQUEST_TEMPLATE.txt"
            ),
            emptyFile("/.github/PULL_REQUEST_TEMPLATE.EMPTY"),
            emptyFile("/.github/PULL_REQUEST_TEMPLATE.empty"),
            emptyFile("/.github/Pull_Request_Template.empty"),
            emptyFile("/.github/pull_request_template.empty"),
            fileWithContents("/PULL_REQUEST_TEMPLATE.MD", "/PULL_REQUEST_TEMPLATE.MD"),
            fileWithContents("/PULL_REQUEST_TEMPLATE.md", "/PULL_REQUEST_TEMPLATE.md"),
            fileWithContents("/Pull_Request_Template.MD", "/Pull_Request_Template.MD"),
            fileWithContents("/pull_request_template.md", "/PULL_REQUEST_TEMPLATE.md"),
            fileWithContents("/PULL_REQUEST_TEMPLATE", "/PULL_REQUEST_TEMPLATE"),
            fileWithContents("/Pull_Request_Template", "/Pull_Request_Template"),
            fileWithContents("/pull_request_template", "/PULL_REQUEST_TEMPLATE"),
            fileWithContents("/PULL_REQUEST_TEMPLATE.TXT", "/PULL_REQUEST_TEMPLATE.TXT"),
            fileWithContents("/PULL_REQUEST_TEMPLATE.txt", "/PULL_REQUEST_TEMPLATE.txt"),
            fileWithContents("/Pull_Request_Template.txt", "/Pull_Request_Template.txt"),
            fileWithContents("/pull_request_template.txt", "/PULL_REQUEST_TEMPLATE.txt"),
            emptyFile("/PULL_REQUEST_TEMPLATE.EMPTY"),
            emptyFile("/PULL_REQUEST_TEMPLATE.empty"),
            emptyFile("/Pull_Request_Template.empty"),
            emptyFile("/pull_request_template.empty"),
            fileWithContents(
                "/DOCS/PULL_REQUEST_TEMPLATE.MD",
                "/DOCS/PULL_REQUEST_TEMPLATE.MD"
            ),
            fileWithContents(
                "/DOCS/PULL_REQUEST_TEMPLATE.md",
                "/DOCS/PULL_REQUEST_TEMPLATE.md"
            ),
            fileWithContents(
                "/DOCS/Pull_Request_Template.MD",
                "/DOCS/Pull_Request_Template.MD"
            ),
            fileWithContents(
                "/DOCS/pull_request_template.md",
                "/DOCS/PULL_REQUEST_TEMPLATE.md"
            ),
            fileWithContents(
                "/DOCS/PULL_REQUEST_TEMPLATE",
                "/DOCS/PULL_REQUEST_TEMPLATE"
            ),
            fileWithContents(
                "/DOCS/Pull_Request_Template",
                "/DOCS/Pull_Request_Template"
            ),
            fileWithContents(
                "/DOCS/pull_request_template",
                "/DOCS/PULL_REQUEST_TEMPLATE"
            ),
            fileWithContents(
                "/DOCS/PULL_REQUEST_TEMPLATE.TXT",
                "/DOCS/PULL_REQUEST_TEMPLATE.TXT"
            ),
            fileWithContents(
                "/DOCS/PULL_REQUEST_TEMPLATE.txt",
                "/DOCS/PULL_REQUEST_TEMPLATE.txt"
            ),
            fileWithContents(
                "/DOCS/Pull_Request_Template.txt",
                "/DOCS/Pull_Request_Template.txt"
            ),
            fileWithContents(
                "/DOCS/pull_request_template.txt",
                "/DOCS/PULL_REQUEST_TEMPLATE.txt"
            ),
            emptyFile("/DOCS/PULL_REQUEST_TEMPLATE.EMPTY"),
            emptyFile("/DOCS/PULL_REQUEST_TEMPLATE.empty"),
            emptyFile("/DOCS/Pull_Request_Template.empty"),
            emptyFile("/DOCS/pull_request_template.empty"),
            fileWithContents(
                "/docs/PULL_REQUEST_TEMPLATE.MD",
                "/docs/PULL_REQUEST_TEMPLATE.MD"
            ),
            fileWithContents(
                "/docs/PULL_REQUEST_TEMPLATE.md",
                "/docs/PULL_REQUEST_TEMPLATE.md"
            ),
            fileWithContents(
                "/docs/Pull_Request_Template.MD",
                "/docs/Pull_Request_Template.MD"
            ),
            fileWithContents(
                "/docs/pull_request_template.md",
                "/docs/PULL_REQUEST_TEMPLATE.md"
            ),
            fileWithContents(
                "/docs/PULL_REQUEST_TEMPLATE",
                "/docs/PULL_REQUEST_TEMPLATE"
            ),
            fileWithContents(
                "/docs/Pull_Request_Template",
                "/docs/Pull_Request_Template"
            ),
            fileWithContents(
                "/docs/pull_request_template",
                "/docs/PULL_REQUEST_TEMPLATE"
            ),
            fileWithContents(
                "/docs/PULL_REQUEST_TEMPLATE.TXT",
                "/docs/PULL_REQUEST_TEMPLATE.TXT"
            ),
            fileWithContents(
                "/docs/PULL_REQUEST_TEMPLATE.txt",
                "/docs/PULL_REQUEST_TEMPLATE.txt"
            ),
            fileWithContents(
                "/docs/Pull_Request_Template.txt",
                "/docs/Pull_Request_Template.txt"
            ),
            fileWithContents(
                "/docs/pull_request_template.txt",
                "/docs/PULL_REQUEST_TEMPLATE.txt"
            ),
            emptyFile("/docs/PULL_REQUEST_TEMPLATE.EMPTY"),
            emptyFile("/docs/PULL_REQUEST_TEMPLATE.empty"),
            emptyFile("/docs/Pull_Request_Template.empty"),
            emptyFile("/docs/pull_request_template.empty")
        );

        when_run_report_command();

        then_readiness_report_matches(
            "README.md..................................................................FOUND\n" +
            "README.md Title............................................................FOUND\n" +
            "README.md Description......................................................FOUND\n" +
            "README.md Build Status Badges..............................................FOUND\n" +
            "README.md Usage Section....................................................FOUND\n" +
            "README.md Local Development Section........................................FOUND\n" +
            "README.md Contributing Section.............................................FOUND\n" +
            "README.md Support Section..................................................FOUND\n" +
            "CODEOWNERS.................................................................FOUND\n" +
            "CODEOWNERS Default Rule....................................................FOUND\n" +
            "CONTRIBUTING.md............................................................FOUND\n" +
            "CONTRIBUTING.md Before PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md During PR Section..........................................FOUND\n" +
            "CONTRIBUTING.md After PR Section...........................................FOUND\n" +
            "PULL_REQUEST_TEMPLATE.md...................................................FOUND\n"
        );

        then_log_warning_count_is(0);
    }
}
