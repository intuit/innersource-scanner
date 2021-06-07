package com.intuit.innersource.reposcanner.repofilepath.github;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.common.base.Throwables;
import com.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.intuit.innersource.reposcanner.repofilepath.github.ImmutableGitHubRepositoryPath;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;

/**
 * A remote GitHub repository file path representing a repository hosted either by the Public GitHub servers or an
 * Enterprise GitHub server.
 *
 * @author Matt Madson
 * @author Shagun Bose
 * @since 1.0.0
 */
@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
public abstract class GitHubRepositoryPath implements RepositoryFilePath {

    GitHubRepositoryPath() {}

    public abstract GHRepository repository();

    /**
     * Creates a new {@code GitHubRepositoryPath} instance from the supplied {@code repository} GitHub api instance.
     *
     * @throws IllegalArgumentException if {@code path} is null
     */
    public static GitHubRepositoryPath of(final GHRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        return ImmutableGitHubRepositoryPath.builder().repository(repository).build();
    }

    @Override
    public String getFileName() {
        return "";
    }

    @Override
    public String getFileNameWithoutExtension() {
        return FilenameUtils.getBaseName(getFileName());
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public long size() {
        return 0L;
    }

    @Override
    public InputStream read() throws UncheckedIOException {
        throw new UncheckedIOException(
            new IOException("This method is not supported for directories")
        );
    }

    @Override
    public List<RepositoryFilePath> listAll() throws UncheckedIOException {
        try {
            return repository()
                .getDirectoryContent(toFilePathString())
                .stream()
                .map(GitHubRepositoryFilePath::of)
                .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toFilePathString() {
        return "/";
    }

    @Override
    public RepositoryFilePath resolvePath(final String childPath) {
        try {
            return GitHubRepositoryFilePath.of(
                repository()
                    .getFileContent(
                        FilenameUtils.normalize(
                            Paths.get(toFilePathString()).resolve(childPath).toString()
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
                    repository(),
                    Paths.get(toFilePathString()).resolve(childPath)
                );
            }
            throw new UncheckedIOException(fileContentException);
        }
    }

    @Override
    public void touch() {
        // root is assumed to already exist, nothing to do
    }

    @Override
    public void appendLines(final Iterable<String> lines) throws UncheckedIOException {
        throw new UncheckedIOException(
            new IOException("repository root is not a file, unable to append lines")
        );
    }

    @Override
    public void createDirectories() {
        // directories are automatically created once a file is written to, if no file
        // write operations occur, directories won't be created
    }
}
