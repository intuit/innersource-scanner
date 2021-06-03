package com.github.intuit.innersource.reposcanner.repofilepath.local;

import com.github.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * A {@code LocalRepositoryFilePath} represents a git repository stored on the local filesystem. Typically this is a
 * cloned / checked out repository.
 *
 * @author Matt Madson
 * @since 1.0.0
 */
@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
public abstract class LocalRepositoryFilePath implements RepositoryFilePath {

    LocalRepositoryFilePath() {}

    /**
     * Returns the underlying filesystem path instance.
     *
     * @return the underlying filesystem path instance.
     */
    public abstract Path path();

    /**
     * Creates a new {@code LocalRepositoryFilePath} from the specified local filesystem {@code path}.
     *
     * @throws IllegalArgumentException if {@code path} is null
     */
    public static LocalRepositoryFilePath of(final Path path) {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        return ImmutableLocalRepositoryFilePath.builder().path(path).build();
    }

    @Override
    public String getFileName() {
        return Optional.of(path()).map(Path::getFileName).map(Path::toString).orElse("");
    }

    @Override
    public String getFileNameWithoutExtension() {
        return FilenameUtils.getBaseName(path().getFileName().toString());
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(path());
    }

    @Override
    public boolean exists() {
        return Files.exists(path());
    }

    @Override
    public long size() {
        try {
            return Files.size(path());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public InputStream read() {
        try {
            return Files.newInputStream(path());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<RepositoryFilePath> listAll() {
        try (final Stream<Path> children = Files.list(path())) {
            return children.map(LocalRepositoryFilePath::of).collect(Collectors.toList());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toFilePathString() {
        return path().toAbsolutePath().normalize().toString();
    }

    @Override
    public RepositoryFilePath resolvePath(final String other) {
        return LocalRepositoryFilePath.of(path().resolve(other));
    }

    @Override
    public void touch() {
        try {
            Files.createDirectories(path().getParent());
            if (Files.exists(path())) {
                Files.setLastModifiedTime(path(), FileTime.from(Instant.now()));
            } else {
                Files.createFile(path());
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void createDirectories() {
        try {
            Files.createDirectories(path());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void appendLines(final Iterable<String> lines) {
        try {
            Files.createDirectories(path().getParent());
            Files.write(path(), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
