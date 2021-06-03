package com.github.intuit.innersource.reposcanner;

import com.github.intuit.innersource.reposcanner.repofilepath.github.GitHubRepositoryPath;
import com.github.intuit.innersource.reposcanner.repofilepath.local.LocalRepositoryFilePath;
import com.google.common.collect.TreeTraverser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHContentUpdateResponse;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class RemoteGitHubTestFixture extends LocalFileSystemTestFixture {

    private GHRepository remoteRepo;

    @Override
    protected void before() throws Throwable {
        super.before();
        remoteRepo = null;
    }

    @Override
    public void given_github_repo(
        final Supplier<Entry<String, Object>>... specSuppliers
    ) {
        super.given_github_repo(specSuppliers);
        if (specSuppliers == null) {
            remoteRepo = null;
            return;
        }

        try {
            this.remoteRepo = Mockito.mock(GHRepository.class);

            final Map<Path, GHContent> mockFiles = new TreeTraverser<Path>() {
                @Override
                public Iterable<Path> children(final Path root) {
                    try {
                        return Files.isDirectory(root)
                            ? Files.list(root).collect(Collectors.toList())
                            : Lists.newArrayList();
                    } catch (final IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
                .breadthFirstTraversal(repoRoot)
                .toList()
                .stream()
                .collect(Collectors.toMap(Function.identity(), this::mockRemoteFile));

            Mockito
                .when(remoteRepo.getFileContent(Mockito.any()))
                .thenAnswer(
                    (Answer<GHContent>) invocation -> {
                        final Path requestedFile = repoRoot.resolve(
                            invocation.getArgument(0, String.class)
                        );
                        GHContent result = mockFiles.get(requestedFile);
                        if (result == null) {
                            LocalRepositoryFilePath.of(requestedFile).touch();
                            result = mockRemoteFile(requestedFile);
                            mockFiles.put(requestedFile, result);
                        }
                        return result;
                    }
                );
            Mockito
                .when(remoteRepo.getDirectoryContent(Mockito.eq("/")))
                .thenAnswer(
                    (Answer<List<GHContent>>) invocation ->
                        Files
                            .list(repoRoot)
                            .map(f -> mockFiles.getOrDefault(f, mockRemoteFile(f)))
                            .collect(Collectors.toList())
                );
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private GHContent mockRemoteFile(final Path p) throws UncheckedIOException {
        try {
            final GHContent remoteFile = Mockito.mock(GHContent.class);
            Mockito
                .when(remoteFile.update(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(
                    (Answer<GHContentUpdateResponse>) invocation -> {
                        final String updatedContent = invocation.getArgument(
                            0,
                            String.class
                        );
                        try {
                            Files.createDirectories(p.getParent());
                            Files.write(
                                p,
                                Lists.newArrayList(updatedContent),
                                StandardCharsets.UTF_8,
                                StandardOpenOption.TRUNCATE_EXISTING
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return Mockito.mock(GHContentUpdateResponse.class);
                    }
                );
            Mockito
                .when(remoteFile.getName())
                .thenReturn(
                    Optional
                        .ofNullable(p)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .orElse("")
                );
            Mockito.when(remoteFile.getSize()).thenAnswer(invocation -> Files.size(p));
            Mockito.when(remoteFile.isDirectory()).thenReturn(Files.isDirectory(p));
            Mockito
                .when(remoteFile.read())
                .thenAnswer(invocation -> Files.newInputStream(p));
            Mockito
                .when(remoteFile.getPath())
                .thenReturn(StringUtils.removeStart(p.toString(), "/"));
            final PagedIterable<GHContent> directoryContent = Mockito.mock(
                PagedIterable.class
            );
            Mockito
                .when(directoryContent.toList())
                .thenAnswer(
                    invocation ->
                        Files
                            .list(p)
                            .map(this::mockRemoteFile)
                            .collect(Collectors.toList())
                );
            Mockito.when(remoteFile.listDirectoryContent()).thenReturn(directoryContent);
            return remoteFile;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void when_run_report_command() {
        try {
            readinessReport =
                reportCommandBuilderProvider
                    .apply(
                        Optional
                            .ofNullable(remoteRepo)
                            .map(GitHubRepositoryPath::of)
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
                            .ofNullable(remoteRepo)
                            .map(GitHubRepositoryPath::of)
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
}
