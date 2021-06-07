/**
 * RepositoryFilePath implementation which operates on a remote GitHub repository.
 *
 * <h1>Usage:</h1>
 * <p>First ensure the optional GitHub api dependency is on your classpath:
 * <pre>
 * &lt;dependency&gt;
 *    &lt;groupId&gt;org.kohsuke&lt;/groupId&gt;
 *    &lt;artifactId&gt;github-api&lt;/artifactId&gt;
 *    &lt;version&gt;LATEST&lt;/version&gt;
 * &lt;/dependency&gt;
 * </pre>
 * <p>Next construct the github api and repository instance in order to instantiate the
 * GitHubRepositoryPath.
 * <pre>
 * GitHub gh = new GitHubBuilder()
 *     .withOAuthToken("yourGithubAccessToken")
 *     .withEndpoint("https://github.yourorg.com/api/v3")
 *     .build();
 *
 * GHRepository repository = gh.getRepository("repo-org-name/repo-name");
 *
 * InnerSourceReadinessReport report =
 *   InnerSourceReadinessReportCommand.create(
 *     GitHubRepositoryPath.of(repository)
 *   )
 *   .specification(specification)
 *   .build()
 *   .call();
 * </pre>
 */
package com.intuit.innersource.reposcanner.repofilepath.github;
