/**
 * Command API to fixup a git repo to be InnerSource Ready.
 *
 * <h1>Usage:</h1>
 * <pre>
 * List&lt;RepositoryFilePath&gt; fixedFiles =
 *   InnerSourceReadinessFixupCommand.create(
 *     LocalRepositoryFilePath.of(Paths.get("/your/local/git/repo/root"))
 *   )
 *   .specification(specification)
 *   .fileTemplates(
 *     FixupFileTemplates.from(
 *       ImmutableMap.of(
 *         "/README.md", // should match file to find path in spec
 *         "README contents, when README is not found"
 *       )
 *     )
 *   )
 *   .build()
 *   .call();
 * </pre>
 *
 * @see com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification
 * @see com.intuit.innersource.reposcanner.repofilepath.local.LocalRepositoryFilePath
 * @see com.intuit.innersource.reposcanner.repofilepath.github.GitHubRepositoryPath
 */
package com.intuit.innersource.reposcanner.commands.fixup;
