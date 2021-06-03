package com.github.intuit.innersource.reposcanner.repofilepath.github;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.github.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHRepository;

@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
abstract class GitHubRepositoryStagedFilePath implements RepositoryFilePath {

    public abstract GHRepository repository();

    public abstract Path stagedFilePath();

    /**
     * @throws IllegalArgumentException if {@code path} is null
     */
    public static GitHubRepositoryStagedFilePath of(
        final GHRepository repository,
        final Path stagedFilePath
    ) {
        return ImmutableGitHubRepositoryStagedFilePath
            .builder()
            .repository(repository)
            .stagedFilePath(stagedFilePath)
            .build();
    }

    @Override
    public String getFileName() {
        return stagedFilePath().getFileName().toString();
    }

    @Override
    public String getFileNameWithoutExtension() {
        return FilenameUtils.getBaseName(getFileName());
    }

    @Override
    public boolean isDirectory() {
        try {
            return GitHubRepositoryFilePath
                .of(repository().getFileContent(toFilePathString()))
                .isDirectory();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean exists() {
        try {
            return GitHubRepositoryFilePath
                .of(repository().getFileContent(toFilePathString()))
                .exists();
        } catch (final IOException e) {
            if (
                Throwables
                    .getCausalChain(e)
                    .stream()
                    .anyMatch(cause -> cause instanceof GHFileNotFoundException)
            ) {
                return false;
            }
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public long size() {
        try {
            return GitHubRepositoryFilePath
                .of(repository().getFileContent(toFilePathString()))
                .size();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public InputStream read() throws UncheckedIOException {
        try {
            return GitHubRepositoryFilePath
                .of(repository().getFileContent(toFilePathString()))
                .read();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<RepositoryFilePath> listAll() throws UncheckedIOException {
        try {
            return GitHubRepositoryFilePath
                .of(repository().getFileContent(toFilePathString()))
                .listAll();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toFilePathString() {
        return stagedFilePath().toString();
    }

    @Override
    public RepositoryFilePath resolvePath(final String other) {
        try {
            return GitHubRepositoryFilePath.of(
                repository()
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
                    repository(),
                    Paths.get(toFilePathString()).resolve(other)
                );
            }
            throw new UncheckedIOException(fileContentException);
        }
    }

    @Override
    public void touch() {
        if (exists()) {
            return;
        }
        Optional
            .ofNullable(repository())
            .map(GHRepository::createContent)
            .map(ghContentBuilder -> ghContentBuilder.content(""))
            .map(
                ghContentBuilder ->
                    ghContentBuilder.path(
                        StringUtils.removeStart(toFilePathString(), "/")
                    )
            )
            .map(
                ghContentBuilder ->
                    ghContentBuilder.message("creating from InnerSource Readiness Fixup")
            )
            .ifPresent(
                ghContentBuilder -> {
                    try {
                        ghContentBuilder.commit();
                    } catch (final IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            );
    }

    @Override
    public void appendLines(final Iterable<String> lines) {
        try {
            GitHubRepositoryFilePath
                .of(repository().getFileContent(toFilePathString()))
                .appendLines(lines);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void createDirectories() {}
}
