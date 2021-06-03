package com.github.intuit.innersource.reposcanner.repofilepath.github;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.github.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHFileNotFoundException;

@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
abstract class GitHubRepositoryFilePath implements RepositoryFilePath {

    public abstract GHContent content();

    public static GitHubRepositoryFilePath of(final GHContent content) {
        return ImmutableGitHubRepositoryFilePath.builder().content(content).build();
    }

    @Override
    public String getFileName() {
        return content().getName();
    }

    @Override
    public String getFileNameWithoutExtension() {
        return FilenameUtils.getBaseName(getFileName());
    }

    @Override
    public boolean isDirectory() {
        return content().isDirectory();
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public long size() {
        return content().getSize();
    }

    @Override
    public InputStream read() throws UncheckedIOException {
        try {
            return content().read();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<RepositoryFilePath> listAll() throws UncheckedIOException {
        if (!isDirectory()) {
            throw new UncheckedIOException(
                new IOException("This method is not supported for files")
            );
        }
        try {
            return content()
                .listDirectoryContent()
                .toList()
                .stream()
                .map(GitHubRepositoryFilePath::of)
                .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toFilePathString() {
        return '/' + StringUtils.removeStart(content().getPath(), "/");
    }

    @Override
    public RepositoryFilePath resolvePath(final String other) {
        try {
            return GitHubRepositoryFilePath.of(
                content()
                    .getOwner()
                    .getFileContent(
                        FilenameUtils.normalize(
                            Paths.get(toFilePathString()).resolve(other).toString()
                        )
                    )
            );
        } catch (final IOException fileContentException) {
            if (
                Throwables
                    .getCausalChain(fileContentException)
                    .stream()
                    .anyMatch(
                        cause ->
                            (cause instanceof GHFileNotFoundException) ||
                            (cause instanceof MismatchedInputException)
                    )
            ) {
                return GitHubRepositoryStagedFilePath.of(
                    content().getOwner(),
                    Paths.get(toFilePathString()).resolve(other)
                );
            }
            throw new UncheckedIOException(fileContentException);
        }
    }

    @Override
    public void touch() {
        // file path instances are created from existing GHContent, we assume file exists and also
        // that touch would be a noop.
    }

    @Override
    public void appendLines(final Iterable<String> lines) {
        try {
            final String currentContent = IOUtils.toString(
                read(),
                StandardCharsets.UTF_8
            );
            final String updatedContent = currentContent + Joiner.on("\n").join(lines);
            content().update(updatedContent, "modified from InnerSource Readiness Fixup");
            content().refresh();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void createDirectories() {}
}
