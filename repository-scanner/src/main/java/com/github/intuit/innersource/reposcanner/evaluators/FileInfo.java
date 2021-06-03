package com.github.intuit.innersource.reposcanner.evaluators;

import com.github.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import java.util.List;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
public interface FileInfo {
    RepositoryFilePath path();

    List<String> getLines();

    MarkdownFileInfo asMarkdownFileInfo();

    boolean exists();

    boolean isDirectory();

    List<FileInfo> listAll();

    long size();
}
