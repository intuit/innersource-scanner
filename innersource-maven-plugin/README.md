# **InnerSource Maven Plugin**

A standard maven plugin which can locally scan your codebase and generate a report on
weather your project is InnerSource Ready. 

## **Usage**

### Prerequisites

- maven 3.x should be installed to your local development environment

### Installation

Determine the latest release version from maven central.

Define the plugin in your `pom.xml`

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.intuit.innersource</groupId>
            <artifactId>innersource-maven-plugin</artifactId>
            <version>1.0.0</version>
        </plugin>
    </plugins>
</build>
```

If your `pom.xml` is not located in the root directory of your codebase's git repository, you
can specify the relative location of your codebase's repository root with the `repositoryRoot`
property.

For example if your codebase is setup with the following directory structure:

```
projectRoot/
|-- app/
|   |-- src/
|   `-- pom.xml
`-- README.md
```

You can specify that your repository root directory as being one directory up with
the following configuration:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.intuit.innersource</groupId>
            <artifactId>innersource-maven-plugin</artifactId>
            <version>1.0.0</version>
            <configuration>
                <repositoryRoot>..</repositoryRoot>            
            </configuration>
        </plugin>
    </plugins>
</build>
```   

**NOTE:** when using relative paths in the `repositoryRoot` property, the path is always
relative to the directory containing the `pom.xml` file where the innersource-maven-plugin
is defined.

### Running Scan

From the directory containing your `pom.xml` run can either run

`mvn clean test`

OR

`mvn innersource:scan`

**NOTE:** By default the plugin is bound to the `test` maven lifecycle phase

You should see the report output in your maven log.

Once you have satisfied all the InnerSource documentation requirements you may optionally
want to fail the build if any of the documents get removed or altered in such a way
that the documentation no longer satisfies the InnerSource requirements. To do this, simply
set the `failBuild` configuration property to true:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.intuit.innersource</groupId>
            <artifactId>innersource-maven-plugin</artifactId>
            <version>1.0.0</version>
            <configuration>   
                <failBuild>true</failBuild>         
            </configuration>
        </plugin>
    </plugins>
</build>
```   

Now if your repository is not inner source ready your build will
fail with a warning similar to:

```
Failed to execute goal com.github.intuit.innersource.mavenplugin:innersource-maven-plugin:1.0.0:scan (check-innersource-docs) on project maven-test: Project located at "/Users/mmadson/Documents/git/maven-test" is missing standard InnerSource documentation and "failBuild" property was set to true
```