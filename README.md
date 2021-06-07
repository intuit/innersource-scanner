
[//]: # (Will add build status and code coverage badges following release)

<!-- Project Logo and Title -->
<p align="center">
  <a href="#">
    <img src=".github/assets/images/innersource-logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">InnerSource Repository Scanner</h3>
</p>

A java api and command line tool for scanning, reporting and fixing a git repository's InnerSource
Readiness based on a supplied specification which defines the files and file contents
necessary for a repository to be considered ready for InnerSource contribution.

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#features">Features</a></li>
    <li><a href="#motivation">Motivation</a></li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li>
          <a href="#command-line-interface-usage">CLI</a>
          <ul>
            <li><a href="#download-the-runnable-jar-cli">Download Runnable Jar</a></li>
            <li><a href="#use-cli-to-generate-innersource-readiness-report">Generate Readiness Report</a></li>
            <li><a href="#use-cli-to-fixup-repositories">Fixup Repository</a></li>
          </ul>
        </li>
        <li>
          <a href="#java-api-usage">Java API</a>
          <ul>
            <li><a href="#install-dependencies">Install Dependencies</a></li>
            <li><a href="#use-java-api-to-generate-innersource-readiness-report">Generate Readiness Report</a></li>
            <li><a href="#use-java-api-to-fixup-repositories">Fixup Repository</a></li>
          </ul>
        </li>
      </ul>
    </li>
    <li>
      <a href="#configuration">Configuration</a>
      <ul>
        <li>
          <a href="#customize-innersource-readiness-specification">Customize Readiness Specification</a>
          <ul>
            <li><a href="#customize-specification-using-cli">Using CLI</a></li>
            <li><a href="#customize-specification-using-java-api">Using Java API</a></li>
          </ul>
        </li>
        <li>
          <a href="#customize-fixup-file-templates">Customize Fixup File Templates</a>
          <ul>
            <li><a href="#customize-fixup-file-templates-using-cli">Using CLI</a></li>
            <li><a href="#customize-fixup-file-templates-using-java-api">Using Java API</a></li>
          </ul>
        </li>
        <li>
          <a href="#customize-logging-service">Customize Log Output</a>
          <ul>
            <li><a href="#customize-logging-using-cli">Using CLI</a></li>
            <li><a href="#customize-logging-using-java-api">Using Java API</a></li>
          </ul>
        </li>
      </ul>
    </li>
    <li>
      <a href="#local-development">Local Development</a>
      <ul>
        <li><a href="#build">Build</a></li>
        <li><a href="#mutation-testing">Mutation Testing</a></li>
      </ul>
    </li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#support">Support</a></li>
  </ol>
</details>

## **Features**

- Determine if a local git repository is InnerSource ready
- Determine if a remote GitHub hosted git repository is InnerSource ready
- Determine if a remote Enterprise GitHub hosted git repository is InnerSource ready
- Customize using declarative json what documentation is required for a repository to be considered InnerSourceReady
- Automatically fixup your local repositories to make them InnerSource ready
- Automatically fixup your remote GitHub hosted repositories to make them InnerSource ready
- Automatically fixup your remote Enterprise GitHub hosted repositories to make them InnerSource ready

## **Motivation**
                 
- Your medium to large sized organization has decided to adopt [InnerSource](https://innersourcecommons.org/) 
  development practices.
- In order for contributors to feel confident contributing to a repository, the repository
  should demonstrate a minimum level of InnerSource readiness; such as having
  a CONTRIBUTING.md describing the expectations of the maintainers, or having a README.md
  with Usage and local development instructions. 
- You and your organization have defined what it means for a repository to be InnerSource ready in terms of
  the expected repository documentation.
- Your organization would like a report on the InnerSource Readiness of its most depended upon repositories.
- You would like to automatically fixup repositories that are not InnerSource ready,
  so that maintainers know which documents are required and what the most important sections 
  in the documents are for them to fill out to help external contributors.
  
## **Usage**

The two main operations supported by this library are scanning a repository for InnerSource
readiness and fixing up the InnerSource readiness of a repository. First decide if you would like to use the standalone 
command line interface (CLI) or the Java API. The primary difference between the two interfaces is that you can use 
multithreading in the Java API to concurrently scan repositories, whereas the CLI requires multiple processes.

- [Learn more about using the CLI](#command-line-interface-usage)
- [Learn more about using the Java API](#java-api-usage)

### Command Line Interface Usage

- [Download the CLI Runnable JAR](#download-the-runnable-jar-cli)
- [Check if your local or remote git repository is InnerSource Ready using the CLI](#use-cli-to-generate-innersource-readiness-report)
- [Fixup your local or remote git repository so that it is as close to InnerSource Ready as possible using the CLI](#use-cli-to-fixup-repositories)

#### Download the Runnable Jar CLI

- Download the latest release version of the runnable jar CLI from the Downloads page.

**NOTE:** Java 1.8+ must be installed in order to execute the runnable jar and follow the examples below.

#### Use CLI to Generate InnerSource Readiness Report

If your repository is checked out locally:

`java -jar innersource.jar -c REPORT -r /path/to/checked/out/repo`

If your repository is hosted on GitHub (public or enterprise):

`java -jar innersource.jar -c REPORT -r https://github.yourorg.com/repo-org-name/repo-name -a yourGithubAccessToken`

After running the report command, you should see a json report output similar to the following:

```text
{
  "isRepositoryInnerSourceReady": false,
  "specificationEvaluated": {...}, // the input specification that was used to generate this report
  "fileRequirementReports": [
    {
      "fileRequirementEvaluated": {...}, // which file requirement defined in the spec does this report relate to
      "optionEvaluated": { // if the file requirement can be satisfied multiple ways, which option is this report evaluating
        "fileToFind": {
          "expectedFilePath": "/README.md"
        },
        "fileChecks": {...}
      },
      "filesEvaluated": [ // what files were found in the repo search directories and evaluated for the defined file checks
        "/README.md"
      ], 
      "filesSatisfyingFileChecks": [], // which files met all of the defined file checks
      "fileChecksReports": [ // breakdown of which file check passed or failed for each scanned file
        {
          "fileEvaluated": "/README.md",
          "fileChecksEvaluated": {...}, // what file checks were defined in the spec that we evaluated as part of this report
          "fileCheckReports": [
            {
              "fileCheckEvaluated": {
                "requirement": "FILE_EXISTS"
              },
              "isFileCheckSatisfied": true
            },
            {
              "fileCheckEvaluated": {
                "requirement": "FILE_NOT_EMPTY"
              },
              "isFileCheckSatisfied": true
            },
            {
              "fileCheckEvaluated": {
                "requirement": "MARKDOWN_FILE_HAS_HEADING",
                "heading": "USAGE",
                "synonyms": [],
                "matchCase": false,
                "matchIfSectionEmpty": true
              },
              "isFileCheckSatisfied": false // true if /README.md had a usage heading, false otherwise
            },
            {
              "fileCheckEvaluated": {
                "requirement": "MARKDOWN_FILE_HAS_HEADING",
                "heading": "LOCAL DEVELOPMENT",
                "synonyms": [
                  "LOCAL DEV"
                ],
                "matchCase": false,
                "matchIfSectionEmpty": true
              },
              "isFileCheckSatisfied": false
            }
          ],
          "isFileChecksSatisfied": false
        }
      ],
      "isFileRequirementSatisfied": false
    },
    {
      "fileRequirementEvaluated": {...},
      "optionEvaluated": { // since Pull Request Template can either be a file or a directory, this report assessed whether we found the file variant
        "fileToFind": {
          "expectedFilePath": "/.github/PULL_REQUEST_TEMPLATE.md"
        },
        "fileChecks": {
          "checks": [
            {
              "requirement": "FILE_EXISTS"
            },
            {
              "requirement": "FILE_NOT_EMPTY"
            }
          ]
        }
      },
      "filesEvaluated": [
        "/.github/PULL_REQUEST_TEMPLATE.md"
      ],
      "filesSatisfyingFileChecks": [
        "/.github/PULL_REQUEST_TEMPLATE.md"
      ],
      "fileChecksReports": [
        {
          "fileEvaluated": "/.github/PULL_REQUEST_TEMPLATE.md",
          "fileChecksEvaluated": {
            "checks": [
              {
                "requirement": "FILE_EXISTS"
              },
              {
                "requirement": "FILE_NOT_EMPTY"
              }
            ]
          },
          "fileCheckReports": [
            {
              "fileCheckEvaluated": {
                "requirement": "FILE_EXISTS"
              },
              "isFileCheckSatisfied": true
            },
            {
              "fileCheckEvaluated": {
                "requirement": "FILE_NOT_EMPTY"
              },
              "isFileCheckSatisfied": true
            }
          ],
          "isFileChecksSatisfied": true
        }
      ],
      "isFileRequirementSatisfied": true
    },
    {
      "fileRequirementEvaluated": {...},
      "optionEvaluated": {
        "fileToFind": {
          "expectedFilePath": "/.github/PULL_REQUEST_TEMPLATE/"
        },
        "fileChecks": {
          "checks": [
            {
              "requirement": "DIRECTORY_EXISTS"
            },
            {
              "requirement": "DIRECTORY_NOT_EMPTY"
            }
          ]
        }
      },
      "filesEvaluated": [
        "/.github/PULL_REQUEST_TEMPLATE.md"
      ],
      "filesSatisfyingFileChecks": [],
      "fileChecksReports": [
        {
          "fileEvaluated": "/.github/PULL_REQUEST_TEMPLATE.md",
          "fileChecksEvaluated": {...},
          "fileCheckReports": [
            {
              "fileCheckEvaluated": {
                "requirement": "DIRECTORY_EXISTS"
              },
              "isFileCheckSatisfied": false
            },
            {
              "fileCheckEvaluated": {
                "requirement": "DIRECTORY_NOT_EMPTY"
              },
              "isFileCheckSatisfied": false
            }
          ],
          "isFileChecksSatisfied": false
        }
      ],
      "isFileRequirementSatisfied": false
    }
  ]
}
```

The top level property `isRepositoryInnerSourceReady` will be true if the scanned repository is 
considered InnerSource Ready, and false otherwise. There are quite a few additional details
included in the report, so you can zoom in on exactly what was missing from the repository.
 
<a id="default-innersource-readiness-spec"></a>
The [Default InnerSource Readiness Specification](.github/assets/examples/public_github_default.spec.json)
states that a repository is considered InnerSource Ready if the following files are present,
and not empty. (NOTE: The files do not actually have to appear in the exact locations as shown;
so long as the file is located in either the repo root `/`, the GitHub metadata directory `/.github`
or the docs directory `/docs`, the file requirement will be satisfied)
Some files have additional requirements such as the README requiring a Title 
heading, or the issue template expecting yaml front matter. The exact requirements for each
file is explained below and in the json spec linked above.

```text
/repo
|-- /.github
|   |-- /ISSUE_TEMPLATE
|   |   `-- someIssueTemplate.md
|   |-- /PULL_REQUEST_TEMPLATE
|   |   `-- somePullRequestTemplate.md
|   `-- CODEOWNERS
|-- CONTRIBUTING.md
|-- CODE_OF_CONDUCT.md
|-- LICENSE
|-- SUPPORT.md
`-- README.md
```

##### README.md Requirements

- A file with the base filename `README` (case-insensitive) exists in either the repository 
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github`.
- The `README` file contains a title heading `#` that is not named `title`.
- The `README`'s title heading is followed by a paragraph text description.

##### CONTRIBUTING.md Requirements

- A file with the base filename `CONTRIBUTING` (case-insensitive) exists in either the repository
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github`.
- The `CONTRIBUTING` file is not empty.

##### CODE_OF_CONDUCT.md Requirements

- A file with the base filename `CODE_OF_CONDUCT` (case-insensitive) exists in either the repository
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github`.
- The `CODE_OF_CONDUCT` file is not empty.

##### LICENSE Requirements

- A file with the base filename `LICENSE` (case-insensitive) exists in either the repository
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github`.
- The `LICENSE` file is not empty.

##### SUPPORT.md Requirements  

- A file with the base filename `SUPPORT` (case-insensitive) exists in either the repository
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github`.
- The `SUPPORT` file is not empty.

##### CODEOWNERS Requirements

- A file with the base filename `CODEOWNERS` (case-insensitive) exists in either the repository
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github`.
- The `CODEOWNERS` file is not empty.
- The `CODEOWNERS` file contains a default match all rule, which is an uncommented line starting with 
  an `*` followed by a GitHub username or email address.

##### ISSUE_TEMPLATE Requirements

- A directory with the filename `ISSUE_TEMPLATE` (case-insensitive) exists in either the repository
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github`.
- The `ISSUE_TEMPLATE` directory contains at least one file which has a Yaml Front Matter 
  section containing the properties `name` and `about`

##### PULL_REQUEST_TEMPLATE Requirements

- A file with the base filename `PULL_REQUEST_TEMPLATE` (case-insensitive) exists in either the repository
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github` 
  OR a directory with the filename `PULL_REQUEST_TEMPLATE` (case-insensitive) exists in either the repository
  root directory `/`, the docs directory `/docs` or the GitHub metadata directory `/.github`
- The `PULL_REQUEST_TEMPLATE` file or directory is not empty.

The default InnerSource Readiness specification can be customized or overridden. See the
[Customize InnerSource Readiness Specification Configuration](#customize-innersource-readiness-specification).

#### Use CLI to Fixup Repositories

If your repository is checked out locally:

`java -jar innersource.jar -c FIXUP -r /path/to/checked/out/repo`

If your repository is hosted on GitHub (public or enterprise):

`java -jar innersource.jar -c FIXUP -r https://github.yourorg.com/repo-org-name/repo-name -a yourGithubAccessToken`
  
<a id="default-fixup-templates"></a>
The [Default Fixup Templates](.github/assets/examples/public_github_default.templates.json) 
for the `FIXUP` command will create the following files with predefined
content if the file does not exist or is empty:

- `/README.md` 
- `/CONTRIBUTING.md` 
- `/CODE_OF_CONDUCT.md` 
- `/SUPPORT.md` 
- `/.github/CODEOWNERS`

The default Fixup File Templates can be customized or overridden. See the
[Customize Fixup File Templates Configuration](#customize-fixup-file-templates).

### Java API Usage

- [Install Dependencies](#install-dependencies)
- [Check if your local or remote git repository is InnerSource Ready using the Java API](#use-java-api-to-generate-innersource-readiness-report)
- [Fixup your local or remote git repository so that it is as close to InnerSource Ready as possible using the Java API](#use-java-api-to-fixup-repositories)

#### Install Dependencies

See [CHANGELOG.md](./CHANGELOG.md) for the latest version.

```xml
<dependency>
    <groupId>com.intuit.innersource</groupId>
    <artifactId>innersource-repository-scanner</artifactId>
    <version>0.0.1</version>
</dependency>
```

#### Use Java API to Generate InnerSource Readiness Report 

If your repository is checked out locally:

```java
InnerSourceReadinessReport report = 
  InnerSourceReadinessReportCommand.create(
    LocalRepositoryFilePath.of(Paths.get("/your/local/git/repo/root"))
  )
  .build()
  .call();        
```

If your repository is hosted on GitHub (public or enterprise):
 
Add the following optional dependency to your classpath:

```xml
<!-- https://mvnrepository.com/artifact/org.kohsuke/github-api -->
<dependency>
    <groupId>org.kohsuke</groupId>
    <artifactId>github-api</artifactId>
    <version>1.122</version>
</dependency>
```

Create an instance of `GitHubRepositoryPath` by configuring a `GHRepository` 
instance using your GitHub server and credentials:

```java
GitHub gh = new GitHubBuilder()
    .withOAuthToken("yourGithubAccessToken")
    .withEndpoint("https://github.yourorg.com/api/v3")
    .build();

GHRepository repository = gh.getRepository("repo-org-name/repo-name");

InnerSourceReadinessReport report =
  InnerSourceReadinessReportCommand.create(
    GitHubRepositoryPath.of(repository)
  )
  .build()
  .call();     
```

After running the report command you can inspect the `InnerSourceReadinessReport` object to find out
if your repository is InnerSource Ready or determine which required files were or were not present.
 
```java
boolean isReadyAccordingToSpec = report.isRepositoryInnerSourceReady();
```

By default, the `InnerSourceReadinessReportCommand` uses the `InnerSourceReadinessSpecification.PUBLIC_GITHUB_DEFAULT`
specification which considers repositories InnerSource Ready only if they have a 
[certain set of files, and the files satisfy a set of predefined file checks](#default-innersource-readiness-spec).

The default specification can be customized or overridden. See the
[Customize InnerSource Readiness Specification Configuration](#customize-innersource-readiness-specification). 

Did the scanned repository contain a README file containing all the expected headers?

```java
FileRequirement readmeFileRequirementPassedToSpec = ...;

boolean foundReadme = report.isFileRequirementSatisfied(
      readmeFileRequirementPassedToSpec
);
```

Did any of the scanned README files contain a USAGE heading?

```java
FileCheck usageHeadingFileCheckPassedToSpec = ...;

boolean foundReadmeWithUsageHeading = report.getOnlyFileRequirementReportFor(
      readmeFileRequirementPassedToSpec
    )
    .get() // unwrap the optional
    .getFileChecksReports() // if multiple README files were found, each will be assessed for the specified file checks
    .stream()
    .anyMatch(checksReport -> 
        checksReport.isFileCheckSatisfied(usageHeadingFileCheckPassedToSpec)
    );
```

See the javadoc for additional details that you can obtain from the report object.

#### Use Java API to Fixup Repositories

```java
List<RepositoryFilePath> fixedFiles =
  InnerSourceReadinessFixupCommand.create(
    LocalRepositoryFilePath.of(Paths.get("/your/local/git/repo/root"))
  )
  .build()
  .call();        
```

By default, the `InnerSourceReadinessFixupCommand` uses the `FixupFileTemplates.PUBLIC_GITHUB_DEFAULT`
set of templates which [will create a set of default files with predefined content](#default-fixup-templates).

The Fixup File Templates can be customized or additional templates defined. See the
[Customize Fixup File Templates Configuration](#customize-fixup-file-templates).

Any files that are created or modified are returned as a result of running
this command.

## **Configuration**

- [Customize InnerSource Readiness Specification](#customize-innersource-readiness-specification)
- [Customize Fixup File Templates](#customize-fixup-file-templates)
- [Customize Log Output](#customize-logging-service)

### **Customize InnerSource Readiness Specification**

- [Using CLI](#customize-specification-using-cli)
- [Using Java API](#customize-specification-using-java-api)

#### **Customize Specification Using CLI**

Create a new json file. Filename isn't important but for our example
we will use `innersource.spec.json`.

```json
{
  "specName": "NameOfYourSpecification",
  "repositoryRequirements": {
    "directoriesToSearch": {
      "directoryPaths": [
        "/",
        "/docs",
        "/.github"
      ]
    },
    "requiredFiles": [
      {
        "requiredFileOptions": [
          {
            "fileToFind": {
              "expectedFilePath": "/README.md"
            },
            "fileChecks": {
              "checks": [
                {
                  "requirement": "FILE_EXISTS"
                },
                {
                  "requirement": "FILE_NOT_EMPTY"
                },
                {
                  "requirement": "MARKDOWN_FILE_HAS_HEADING",
                  "heading": "Usage",
                  "synonyms": [],
                  "matchCase": false,
                  "matchIfSectionEmpty": true
                },
                {
                  "requirement": "MARKDOWN_FILE_HAS_HEADING",
                  "heading": "Local Development",
                  "synonyms": [
                      "Local Dev"
                  ],
                  "matchCase": false,
                  "matchIfSectionEmpty": true
                }                
              ]
            }
          }
        ]
      },
      {
        "requiredFileOptions": [
          {
            "fileToFind": {
              "expectedFilePath": "/.github/PULL_REQUEST_TEMPLATE.md"
            },
            "fileChecks": {
              "checks": [
                {
                  "requirement": "FILE_EXISTS"
                },
                {
                  "requirement": "FILE_NOT_EMPTY"
                }                
              ]
            }
          },
          {
            "fileToFind": {
              "expectedFilePath": "/.github/PULL_REQUEST_TEMPLATE/"
            },
            "fileChecks": {
              "checks": [
                {
                  "requirement": "DIRECTORY_EXISTS"
                },
                {
                  "requirement": "DIRECTORY_NOT_EMPTY"
                }                
              ]
            }
          }          
        ]
      }
    ]
  }
}
```                                                                    

This JSON specification states that for a repository to be considered InnerSource ready, 
there must be a README markdown file containing a USAGE section heading, and a Local Development section
heading (alternately titled Local Dev) as well as a non-empty PULL_REQUEST_TEMPLATE
file, or a non-empty PULL_REQUEST_TEMPLATE directory either located in the repository root 
directory or the `/.github` or `/docs` directories.

**NOTE**: You can use your JSON spec via the Java API's `InnerSourceReadinessSpecification.fromJson()`
static factory method.

Supply this JSON file to either the `REPORT` or `FIXUP` command with the `-s`, `--spec`
parameter:

`java -jar innersource.jar -c REPORT -s innersource.spec.json -r https://github.yourorg.com/repo-org-name/repo-name -a yourGithubAccessToken`

`java -jar innersource.jar -c FIXUP -s innersource.spec.json -r https://github.yourorg.com/repo-org-name/repo-name -a yourGithubAccessToken`
 
List of Built-in File Check Requirements

| Requirement Name | Description | Parameter | Type | Required | 
| ---------------- | ----------- | :---------: | :----: | :--------: |
| PATH_MATCHES_EXPECTED | If path of the file exactly matches the path specified in the file to find specification | - | - | - |
| DIRECTORY_EXISTS | If base filename of file to find path is found in one of the directories to search and the file is a directory | - | - | - |
| DIRECTORY_NOT_EMPTY | If base filename of file to find path is found in one of the directories to search and the file is a directory and the directory contains at least one file | - | - | - |
| DIRECTORY_CONTAINS_FILE_SATISFYING | If base filename of file to find path is found in one of the directories to search and the file is a directory and the directory contains at least one file which satisfies all of the file checks specified in the fileChecks parameter | fileChecks | fileChecks object | Yes |
| FILE_EXISTS | If base filename of file to find path is found in one of the directories to search and the file is a flat file (not a directory) | - | - | - |
| FILE_NOT_EMPTY | If base filename of file to find path is found in one of the directories to search and the file is a flat file of size > 0 | - | - | - |
| FILE_HAS_LINE_MATCHING | If base filename of file to find path is found in one of the directories to search and file contains a line that matches the specified regexPattern | regexPattern | String (java regex pattern) | Yes |
| FILE_HAS_YAML_FRONT_MATTER_PROPERTIES | If base filename of file to find path is found in one of the directories to search and file contains a yaml front matter section containing all of the properties whose names are defined in the propertyNames parameter | propertyNames | array of string | Yes |
| MARKDOWN_FILE_HAS_TITLE_HEADING | If base filename of file to find path is found in one of the directories to search and file contains a markdown heading element whose text matches the regex specified in the titleRegexPattern parameter | titleRegexPattern | string (java regex pattern) | Yes |
| MARKDOWN_FILE_HAS_DESCRIPTION_AFTER_TITLE | If base filename of file to find path is found in one of the directories to search and file contains a markdown title heading followed by a markdown paragraph element containing some amount of text | - | - | - |
| MARKDOWN_FILE_HAS_HEADING | If base filename of file to find path is found in one of the directories to search and file contains a markdown heading whose text content matches the heading parameter | heading | string | Yes |
| | Alternate heading names that will satisfy this file check | synonyms | array of string | No |
| | If true heading text needs to match heading parameter or synonym exactly, otherwise a case-insensitive match will be performed | matchCase | boolean | No |
| | If true the presence of the heading is enough to satisfy the file check, if false, some amount of text content must appear following the heading before the next heading at the same level is encountered | matchIfSectionEmpty | boolean | No |
| MARKDOWN_FILE_HAS_IMAGE | If base filename of file to find path is found in one of the directories to search and file contains a markdown image element whose alt text matches the altText parameter | altText | string | Yes |
| | Alternate altTexts that will satisfy this file check | altTextSynonyms | array of string | No |
| | If true alt text needs to match altText parameter or altTextSynonym exactly, otherwise a case-insensitive match will be performed | matchCase | boolean | No |

#### **Customize Specification Using Java API**

```java
InnerSourceReadinessSpecification specification = 
  InnerSourceReadinessSpecification.create(
    "NameOfYourSpecification",
    RepositoryRequirements.create(
        DirectoriesToSearch.create(
            "/", // root is git workspace root   
            "/docs",  
            "/.github"
        ),
        FileRequirement.create(
          FileToFind.create("/README.md"),
          FileChecks.create(
            FileCheck.fileExists(),
            FileCheck.fileNotEmpty(),
            FileCheck.markdownFileWithHeading(
                "USAGE", // heading text to find
                Sets.newHashSet(), // acceptable synonyms
                false, // case sensitive
                true // satisfied if section is empty?
            ),
            FileCheck.markdownFileWithHeading(
                "Local Development",
                Sets.newHashSet("Local Dev"),
                false,
                true
            )        
          )
        ),
        FileRequirement.oneOf(
          FileRequirementOption.create(
            FileToFind.create("/.github/PULL_REQUEST_TEMPLATE.md"),
            FileChecks.create(
                FileCheck.fileExists(), 
                FileCheck.fileNotEmpty()
            )
          ),
          FileRequirementOption.create(
            FileToFind.create("/.github/PULL_REQUEST_TEMPLATE/"),
            FileChecks.create(
                FileCheck.directoryExists(),
                FileCheck.directoryNotEmpty()
            )
          )
        )        
    )
);
```

The specification above states that for a repository to be considered InnerSource
ready, there should be a non-empty README file containing 2 headings
`USAGE` and `Local Development` (Local Dev is an acceptable synonym) as well as
a non-empty PULL_REQUEST_TEMPLATE file or non-empty PULL_REQUEST_TEMPLATE directory,
located either in the repository root directory, the /docs directory or the /.github
directory.

Once created, you can supply this custom `specification` to either of the
command builders:

Create a Report Command with a custom InnerSource Readiness Specification:

```java
InnerSourceReadinessReport report =
        InnerSourceReadinessReportCommand.create(
            LocalRepositoryFilePath.of(Paths.get("/your/local/git/repo/root"))
        )
        .specification(specification)
        .build()
        .call();        
```

Create a Fixup Command with a custom InnerSource Readiness Specification:

```java
List<RepositoryFilePath> fixedFiles =
  InnerSourceReadinessFixupCommand.create(
    LocalRepositoryFilePath.of(Paths.get("/your/local/git/repo/root"))
  )
  .specification(specification)
  .build()
  .call();        
```

### **Customize Fixup File Templates**

- [Using CLI](#customize-fixup-file-templates-using-cli)
- [Using Java API](#customize-fixup-file-templates-using-java-api)
 
#### **Customize Fixup File Templates Using CLI**

Create a json file (we'll use the filename `templates.spec.json`)
containing empty file templates to use when running the fixup command. These file templates
will be used to create new files for any files required by the specification but not
found in the target repository.

```json
{
  "emptyFileTemplates": {
    "/README.md": "Default README file contents."
  }
}
```

**NOTE**: The key for the empty file template should exactly match the expectedFilePath
string value defined in the specification json.

Supply this JSON file to the `FIXUP` command with the `-t`, `--templates`
parameter:                                                                   

If your repository is checked out locally:

`java -jar innersource.jar -c FIXUP -r /path/to/checked/out/repo -t templates.spec.json`

If your repository is hosted on GitHub (public or enterprise):

`java -jar innersource.jar -c FIXUP -r https://github.yourorg.com/repo-org-name/repo-name -a yourGithubAccessToken -t templates.spec.json`

#### **Customize Fixup File Templates Using Java API**

```java
List<RepositoryFilePath> fixedFiles =
  InnerSourceReadinessFixupCommand.create(
    LocalRepositoryFilePath.of(Paths.get("/your/local/git/repo/root"))
  )
  .specification(specification)
  .fileTemplates(
    FixupFileTemplates.from(
      ImmutableMap.of(
        "/README.md", // should match path in spec 
        "README contents, when README is not found"
      )
    )
  )
  .build()
  .call();        
```

### **Customize Logging Service**

- [Using CLI](#customize-logging-using-cli)
- [Using Java API](#customize-logging-using-java-api)

#### **Customize Logging Using CLI**

There is no way to customize the logging service using the Command Line Interface.
If you need to have the log output redirected or suppressed, please consult your 
terminal's documentation for redirecting the standard output and standard error streams
to either a file or to a null location, such as `/dev/null`.

#### **Customize Logging Using Java API**

The Report and Fixup command builders can also be supplied with alternate `LoggingService` implementations.
The default will write logs to the standard output and standard error stream. In production, you will probably 
want to supply a different consumer to write to a log file. The library comes with a Slf4j api implementation, 
however you may supply your own instance of the LoggingService interface as well.

Suppress logging during execution of the Fixup Command:

```java
List<RepositoryFilePath> fixedFiles =
  InnerSourceReadinessFixupCommand.create(
    LocalRepositoryFilePath.of(Paths.get("/your/local/git/repo/root"))
  )
  .loggingService(NoopLoggingService.INSTANCE)
  .build()
  .call();        
```

Configure logging to the SLF4J API during execution of the Report Command:

```java
InnerSourceReadinessReport report =
        InnerSourceReadinessReportCommand.create(
            LocalRepositoryFilePath.of(Paths.get("/your/local/git/repo/root"))
        )
        .loggingService(Slf4jLoggingService.INSTANCE)
        .build()
        .call();        
```

## **Local Development**

### Build

To build and run the unit tests:

```bash
mvn clean verify
```             

### Mutation Testing

In addition to unit tests, this library also performs mutation testing using
[PITest](https://pitest.org/). After running the `verify` goal, inspect the generated report
at `/target/pit-reports/<date>/index.html`. Mutation testing provides an indication of how effective your unit testing
by modifying your source code and attempting to re-run your test suite. If your tests do not fail, it implies that the
mutated source code is not sufficiently tested by your unit test suite. Use the mutation coverage report to guide your
unit testing efforts.

## **Contributing**

Contributions are welcome, please see the [CONTRIBUTING.md](./CONTRIBUTING.md) for details.

## **Support**

- Create an Issue describing your question or concern
