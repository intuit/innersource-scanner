package com.github.intuit.innersource.reposcanner;

import com.github.intuit.innersource.reposcanner.repofilepath.local.LocalRepositoryFilePath;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.commons.io.IOUtils;

public class LocalFileSystemTestFixture extends BaseTestFixture {

    protected FileSystem fileSystem;
    protected Path repoRoot;

    @Override
    protected void before() throws Throwable {
        super.before();
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        repoRoot = fileSystem.getPath("/");
    }

    @Override
    protected void after() {
        super.after();
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    public void given_github_repo(
        final Supplier<Entry<String, Object>>... specSuppliers
    ) {
        if (specSuppliers == null) {
            repoRoot = null;
            return;
        }
        for (final Supplier<Entry<String, Object>> specSupplier : specSuppliers) {
            final Entry<String, Object> fileCreationSpec = specSupplier.get();
            final Path filePath = repoRoot.resolve(fileCreationSpec.getKey());

            if (fileCreationSpec.getValue() == null) {
                touch(filePath);
            } else if (fileCreationSpec.getValue() instanceof String) {
                write(filePath, (String) fileCreationSpec.getValue());
            } else {
                final URL classpathUrl = (URL) fileCreationSpec.getValue();

                try (final InputStream is = classpathUrl.openStream()) {
                    write(filePath, is);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }

    @Override
    public void when_run_report_command() {
        try {
            readinessReport =
                reportCommandBuilderProvider
                    .apply(
                        Optional
                            .ofNullable(repoRoot)
                            .map(LocalRepositoryFilePath::of)
                            .orElse(null)
                    )
                    .loggingService(loggingService)
                    .specification(SPECIFICATION)
                    .build()
                    .call();
        } catch (final Exception e) {
            this.thrownException = e;
        }
    }

    @Override
    public void when_run_fixup_command() {
        try {
            fixedFiles =
                fixupCommandBuilderProvider
                    .apply(
                        Optional
                            .ofNullable(repoRoot)
                            .map(LocalRepositoryFilePath::of)
                            .orElse(null)
                    )
                    .specification(SPECIFICATION)
                    .fileTemplates(fixupEmptyFileTemplates)
                    .loggingService(this.loggingService)
                    .build()
                    .call();
        } catch (final Exception e) {
            this.thrownException = e;
        }
    }

    private void write(final Path pathToWriteTo, final String stringToWrite) {
        try {
            final Path parentDirectory = pathToWriteTo.getParent();

            if ((parentDirectory != null) && !Files.exists(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }

            Files.write(
                pathToWriteTo,
                ImmutableList.of(stringToWrite),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE
            );
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void write(final Path pathToWriteTo, final InputStream contentStream) {
        try {
            final Path parentDirectory = pathToWriteTo.getParent();

            if ((parentDirectory != null) && !Files.exists(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }

            try (
                final OutputStream outputStream = Files.newOutputStream(
                    pathToWriteTo,
                    StandardOpenOption.CREATE
                )
            ) {
                IOUtils.copy(contentStream, outputStream);
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void touch(final Path pathToTouch) {
        try {
            final Path parentDirectory = pathToTouch.getParent();

            if ((parentDirectory != null) && !Files.exists(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }

            Files.createFile(pathToTouch);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
