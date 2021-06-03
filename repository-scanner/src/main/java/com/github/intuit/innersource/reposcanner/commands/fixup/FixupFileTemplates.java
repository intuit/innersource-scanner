package com.github.intuit.innersource.reposcanner.commands.fixup;

import com.github.intuit.innersource.reposcanner.jsonservice.JsonService;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileToFind;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * A class that represents a set of file templates to use when creating missing required files using the {@link
 * InnerSourceReadinessFixupCommand}.
 * <br><br>
 * <p>Can be constructed either from a json string or from a {@link Map}.
 * <br><br>
 * <h2><a id="jsonusage">Creating from JSON:</a></h2>
 *
 * <p>Json must satisfy the <a href="https://raw.githubusercontent.com/intuit/innersource/main/scanner/src/main/resources/fixupFileTemplates.schema.json">fixupFileTemplates
 * json schema</a>.
 *
 * <pre>
 * {
 *     "emptyFileTemplates": {
 *         "/README.md": "Readme Template to use when Readme isn't found",
 *         "/CONTRIBUTING.md": "Contributing Template to use when Contributing isn't found"
 *     }
 * }
 * </pre>
 * <p>Instantiate by passing the json string to the {@link #fromJson(String)} static factory method.
 * <br><br>
 * <h2><a id="mapusage">Creating from a {@link Map}:</a></h2>
 *
 * <p>Using Java 9 Map.of static factory method:
 * <pre>
 * FixupFileTemplates.from(
 *    Map.of(
 *      "/README.md", "Readme template to use when Readme isn't found",
 *      "/CONTRIBUTING.md", "Contributing template to use when Contributing isn't found"
 *    )
 * )
 * </pre>
 *
 * <p><strong>NOTE:</strong> The map keys and emptyFileTemplates json object property keys must be {@code String}s that
 * exactly match the expectedFilePathFromRepoRoot {@code String} passed to the {@link FileToFind#create(String)} of the
 * {@link InnerSourceReadinessSpecification}.
 *
 * @author Matt Madson
 * @see InnerSourceReadinessFixupCommand
 * @see FileToFind#create(String)
 * @see <a href="https://raw.githubusercontent.com/intuit/innersource/main/scanner/src/main/resources/fixupFileTemplates.schema.json">FixupFileTemplates
 * json schema</a>
 * @see Map
 * @since 1.0.0
 */
@Gson.TypeAdapters
@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
public abstract class FixupFileTemplates {

    /**
     * A set of file templates intended to be used with repositories hosted on an enterprise github instance where the
     * InnerSourceReadinessSpecification expects to find a <em>README.md</em>,
     * <em>CONTRIBUTING.md</em>, <em>SUPPORT.md</em> and <em>CODEOWNERS</em> file. These file
     * templates correspond with the {@link InnerSourceReadinessSpecification#ENTERPRISE_GITHUB_DEFAULT} specification.
     */
    public static final FixupFileTemplates ENTERPRISE_GITHUB_DEFAULT = FixupFileTemplates.from(
        ImmutableMap.of(
            "/README.md",
            "# Title\n" +
            "\n" +
            "A brief description of what problem your application or service solves.\n" +
            "\n",
            "/CONTRIBUTING.md",
            "# Contributing\n" +
            "\n" +
            "## Before Submitting a PR\n" +
            "\n" +
            "Outline expectations of contributor before they submit a pull request," +
            " for example creating an issue and discussing feature with maintainers.\n" +
            "\n" +
            "## Pull Request Process\n" +
            "\n" +
            "Describe what the expectations of the maintainers are in order to have" +
            " a PR merged, also set expectations for contributors for how quickly a PR" +
            " should be reviewed.\n" +
            "\n" +
            "## After Submitting a PR\n" +
            "\n" +
            "What expectations do the maintainers have of the submitted code, should" +
            " contributors be expected to support their features? If so for how long?\n" +
            "\n",
            "/SUPPORT.md",
            "# Support Resources\n" +
            "\n" +
            "Reduce your question to a [minimal reproducible example][]. This may\n" +
            "help you identify the issue yourself, and will make it much easier for\n" +
            "others to help you.\n" +
            "\n" +
            "Finally, use one of the following resources for help with your code:\n" +
            "\n" +
            "* Link to internal chat application, e.g., Slack, Microsoft Teams, etc.\n" +
            "\n" +
            "* Mailing list {{YOUR MAILING LIST EMAIL}}\n" +
            "\n" +
            "\n" +
            "[minimal reproducible example]: https://stackoverflow.com/help/minimal-reproducible-example\n",
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
            "# /docs/ @doctocat"
        )
    );

    /**
     * A set of file templates intended to be used with repositories hosted on the public GitHub servers where the
     * InnerSourceReadinessSpecification expects to find a <em>README.md</em>,
     * <em>CONTRIBUTING.md</em>, <em>CODE_OF_CONDUCT.md</em>, <em>SUPPORT.md</em> and <em>CODEOWNERS</em> file.
     * These file templates correspond with the {@link InnerSourceReadinessSpecification#PUBLIC_GITHUB_DEFAULT}
     * specification.
     */
    public static final FixupFileTemplates PUBLIC_GITHUB_DEFAULT = FixupFileTemplates.from(
        ImmutableMap.of(
            "/README.md",
            "# Title\n" +
            "\n" +
            "A brief description of what problem your application or service solves.\n" +
            "\n",
            "/CONTRIBUTING.md",
            "# Contributing\n" +
            "\n" +
            "## Before Submitting a PR\n" +
            "\n" +
            "Outline expectations of contributor before they submit a pull request," +
            " for example creating an issue and discussing feature with maintainers.\n" +
            "\n" +
            "## Pull Request Process\n" +
            "\n" +
            "Describe what the expectations of the maintainers are in order to have" +
            " a PR merged, also set expectations for contributors for how quickly a PR" +
            " should be reviewed.\n" +
            "\n" +
            "## After Submitting a PR\n" +
            "\n" +
            "What expectations do the maintainers have of the submitted code, should" +
            " contributors be expected to support their features? If so for how long?\n" +
            "\n",
            "/CODE_OF_CONDUCT.md",
            "# Code of Conduct\n" +
            "\n" +
            "## Our Pledge\n" +
            "\n" +
            "In the interest of fostering an open and welcoming environment, we as\n" +
            "contributors and maintainers pledge to making participation in our project and\n" +
            "our community a harassment-free experience for everyone, regardless of age, body\n" +
            "size, disability, ethnicity, gender identity and expression, level of experience,\n" +
            "education, socio-economic status, nationality, personal appearance, race,\n" +
            "religion, or sexual identity and orientation.\n" +
            "\n" +
            "## Our Standards\n" +
            "\n" +
            "Examples of behavior that contributes to creating a positive environment\n" +
            "include:\n" +
            "\n" +
            "* Using welcoming and inclusive language\n" +
            "* Being respectful of differing viewpoints and experiences\n" +
            "* Gracefully accepting constructive criticism\n" +
            "* Focusing on what is best for the community\n" +
            "* Showing empathy towards other community members\n" +
            "\n" +
            "Examples of unacceptable behavior by participants include:\n" +
            "\n" +
            "* The use of sexualized language or imagery and unwelcome sexual attention or\n" +
            "  advances\n" +
            "* Trolling, insulting/derogatory comments, and personal or political attacks\n" +
            "* Public or private harassment\n" +
            "* Publishing others' private information, such as a physical or electronic\n" +
            "  address, without explicit permission\n" +
            "* Other conduct which could reasonably be considered inappropriate in a\n" +
            "  professional setting\n" +
            "\n" +
            "## Our Responsibilities\n" +
            "\n" +
            "Project maintainers are responsible for clarifying the standards of acceptable\n" +
            "behavior and are expected to take appropriate and fair corrective action in\n" +
            "response to any instances of unacceptable behavior.\n" +
            "\n" +
            "Project maintainers have the right and responsibility to remove, edit, or\n" +
            "reject comments, commits, code, wiki edits, issues, and other contributions\n" +
            "that are not aligned to this Code of Conduct, or to ban temporarily or\n" +
            "permanently any contributor for other behaviors that they deem inappropriate,\n" +
            "threatening, offensive, or harmful.\n" +
            "\n" +
            "## Scope\n" +
            "\n" +
            "This Code of Conduct applies both within project spaces and in public spaces\n" +
            "when an individual is representing the project or its community. Examples of\n" +
            "representing a project or community include using an official project e-mail\n" +
            "address, posting via an official social media account, or acting as an appointed\n" +
            "representative at an online or offline event. Representation of a project may be\n" +
            "further defined and clarified by project maintainers.\n" +
            "\n" +
            "## Enforcement\n" +
            "\n" +
            "Instances of abusive, harassing, or otherwise unacceptable behavior may be\n" +
            "reported by contacting the project team at {{ email }}. All\n" +
            "complaints will be reviewed and investigated and will result in a response that\n" +
            "is deemed necessary and appropriate to the circumstances. The project team is\n" +
            "obligated to maintain confidentiality with regard to the reporter of an incident.\n" +
            "Further details of specific enforcement policies may be posted separately.\n" +
            "\n" +
            "Project maintainers who do not follow or enforce the Code of Conduct in good\n" +
            "faith may face temporary or permanent repercussions as determined by other\n" +
            "members of the project's leadership.\n" +
            "\n" +
            "## Attribution\n" +
            "\n" +
            "This Code of Conduct is adapted from the [Contributor Covenant][homepage], version 1.4,\n" +
            "available at https://www.contributor-covenant.org/version/1/4/code-of-conduct.html\n" +
            "\n" +
            "[homepage]: https://www.contributor-covenant.org\n",
            "/SUPPORT.md",
            "# Support Resources\n" +
            "\n" +
            "Please do not create GitHub issues for questions related to your own code.\n" +
            "GitHub issues are for issues related to this project and its code.\n" +
            "\n" +
            "Search with Google using \"{{YOUR APPLICATION NAME}}\" and some keywords, an exception\n" +
            "message, etc. It can be helpful to add `site:stackoverflow.com` to\n" +
            "search Stack Overflow for answers.\n" +
            "\n" +
            "Reduce your question to a [minimal reproducible example][]. This may\n" +
            "help you identify the issue yourself, and will make it much easier for\n" +
            "others to help you.\n" +
            "\n" +
            "Finally, use one of the following resources for help with your code:\n" +
            "\n" +
            "* The `#get-help` channel on our [Discord chat][] can be used to chat\n" +
            "  with and get help from other users in the community.\n" +
            "\n" +
            "* The mailing list {{YOUR MAILING LIST EMAIL}} can be used for larger issues or\n" +
            "  long term discussion with the community.\n" +
            "  \n" +
            "* Ask on [Stack Overflow][]. Be sure to use the advice above to search\n" +
            "  and narrow down your issue first.\n" +
            "\n" +
            "[Discord chat]: {{YOUR APPLICATION DISCORD URL}}\n" +
            "[minimal reproducible example]: https://stackoverflow.com/help/minimal-reproducible-example\n" +
            "[Stack Overflow]: https://stackoverflow.com/",
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
            "# /docs/ @doctocat"
        )
    );

    FixupFileTemplates() {}

    /**
     * Returns an immutable {@code FixupFileTemplates} instance from a json object containing the mappings from expected
     * file paths to file contents. See <a href="jsonusage">Creating from a Json Object</a> for details.
     *
     * @param json The file templates to use when fixing up a repository using the {@link
     *             InnerSourceReadinessFixupCommand} specified as a json object conforming to the <a
     *             href="https://raw.githubusercontent.com/intuit/innersource/main/scanner/src/main/resources/fixupFileTemplates.schema.json">FixupFileTemplates
     *             json schema</a>.
     * @return a new {@code FixupFileTemplates} instance representing the mappings from {@link
     * InnerSourceReadinessSpecification.FileToFind} to the the template file contents that should be used when creating
     * the file should the file not be found during execution of the {@link InnerSourceReadinessFixupCommand}.
     */
    public static FixupFileTemplates fromJson(final String json) {
        final Set<String> validationErrors;
        try {
            validationErrors =
                JsonService
                    .getInstance()
                    .validateJsonAgainstClasspathSchema(
                        json,
                        "/fixupFileTemplates.schema.json"
                    );
        } catch (final IOException cause) {
            throw new InvalidFixupFileTemplatesException(cause);
        }
        if (!validationErrors.isEmpty()) {
            throw new InvalidFixupFileTemplatesException(validationErrors);
        }
        return JsonService.getInstance().fromJson(json, FixupFileTemplates.class);
    }

    /**
     * Returns an immutable {@code FixupFileTemplates} instance from a {@link Map} containing the mappings from expected
     * file paths to file contents. See <a href="mapusage">Creating from a Map</a> for details.
     *
     * @param emptyFileTemplates The file templates to use when fixing up a repository using the {@link
     *                           InnerSourceReadinessFixupCommand} specified as a {@link Map} object.
     * @return a new {@code FixupFileTemplates} instance representing the mappings from {@link
     * InnerSourceReadinessSpecification.FileToFind} to the the template file contents that should be used when creating
     * the file should the file not be found during execution of the {@link InnerSourceReadinessFixupCommand}.
     */
    public static FixupFileTemplates from(final Map<String, String> emptyFileTemplates) {
        return ImmutableFixupFileTemplates
            .builder()
            .emptyFileTemplates(emptyFileTemplates)
            .build();
    }

    /**
     * Returns the file template mappings from expectedFilePath to template file contents to use when populating missing
     * or empty required files during execution of the {@link InnerSourceReadinessFixupCommand}.
     *
     * @return the file template mappings from expectedFilePath to template file contents
     */
    public abstract Map<String, String> emptyFileTemplates();

    /**
     * Serializes {@code this} instance into its JSON representation and returns the result as a {@link String}.
     *
     * @return the json representation of this instance.
     */
    public String toJson() {
        return JsonService.getInstance().toJson(this);
    }
}
