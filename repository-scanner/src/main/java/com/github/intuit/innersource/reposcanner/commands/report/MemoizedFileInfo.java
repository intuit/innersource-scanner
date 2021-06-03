package com.github.intuit.innersource.reposcanner.commands.report;

import com.github.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.github.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo;
import com.github.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Parameter;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Immutable(builder = false)
@Style(visibility = ImplementationVisibility.PACKAGE)
abstract class MemoizedFileInfo implements FileInfo {

    @Override
    @Parameter
    public abstract RepositoryFilePath path();

    public static MemoizedFileInfo of(final RepositoryFilePath path) {
        return ImmutableMemoizedFileInfo.of(path);
    }

    @Override
    @Lazy
    public List<String> getLines() {
        try {
            return IOUtils.readLines(path().read(), StandardCharsets.UTF_8.name());
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    @Lazy
    public MarkdownFileInfo asMarkdownFileInfo() {
        try (
            final BufferedReader br = new BufferedReader(
                new InputStreamReader(path().read(), StandardCharsets.UTF_8.name())
            )
        ) {
            final MarkdownFileNodeVisitor nodeVisitor = new MarkdownFileNodeVisitor();
            nodeVisitor.visit(
                Parser.builder(new MutableDataSet()).build().parseReader(br)
            );
            return nodeVisitor;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    @Lazy
    public boolean exists() {
        return path().exists();
    }

    @Override
    @Lazy
    public boolean isDirectory() {
        return path().isDirectory();
    }

    @Override
    @Lazy
    public List<FileInfo> listAll() {
        return path()
            .listAll()
            .stream()
            .map(MemoizedFileInfo::of)
            .collect(Collectors.toList());
    }

    @Override
    @Lazy
    public long size() {
        return path().size();
    }
}
