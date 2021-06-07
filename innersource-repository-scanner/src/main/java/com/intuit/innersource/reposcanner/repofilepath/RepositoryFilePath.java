package com.intuit.innersource.reposcanner.repofilepath;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * A {@code RepositoryFilePath} represents a path to a file in some git repository where the root of the path represents
 * the root of the git repository. The repository can either be a local filesystem repository, or a remotely hosted git
 * repository.
 *
 * @author Matt Madson
 * @author Shagun Bose
 * @since 1.0.0
 */
public interface RepositoryFilePath {
    /**
     * Returns the full filename including the file extension if one is present.
     *
     * @return the full filename with extension.
     */
    String getFileName();

    /**
     * Returns the name of the file without extensions. For example: {@code test.md --> test}
     *
     * @return file name without extension.
     */
    String getFileNameWithoutExtension();

    /**
     * Returns true if the file represented by {@code this} path is a directory, false if it is a flat file.
     *
     * @return true if {@code this} file is a directory, false otherwise.
     */
    boolean isDirectory();

    /**
     * Returns true if the file exists in the underlying git repository, false if the path does not yet exist.
     *
     * @return true if the file exists, false otherwise.
     */
    boolean exists();

    /**
     * Returns the size of the file in bytes.
     *
     * @return size of the file in bytes.
     */
    long size();

    /**
     * Returns the contents of the file
     *
     * @return Input stream with the bytes of this file
     * @throws UncheckedIOException if the file cannot be read
     */
    InputStream read() throws UncheckedIOException;

    /**
     * Lists all the files in a directory.
     *
     * @return A list of Repository File Path(s) which correspond to either files or directories
     * @throws UncheckedIOException if the directory cannot be scanned
     */
    List<RepositoryFilePath> listAll() throws UncheckedIOException;

    /**
     * Returns the string representation of the file path, rooted at the workspaces' root.
     *
     * @return string representation of the file path.
     */
    String toFilePathString();

    /**
     * Resolve {@code this} repo file path against some {@code other} relative file path.
     *
     * @param other a file whose path is relative to {@code this} repo file path.
     * @return a new repo file path representing the path to {@code other} relative to the git repo workspace root.
     */
    RepositoryFilePath resolvePath(String other);

    /**
     * Attempts to create {@code this} repo file path as an empty file if it does not exist.
     */
    void touch();

    /**
     * Will append {@code lines} to {@code this} file if it exists and is not a directory.
     *
     * @param lines The lines to append to {@code this} file.
     * @throws UncheckedIOException If {@code this} is a directory or does not exist, or a transient error occurred
     *                              while writing lines to the file.
     */
    void appendLines(Iterable<String> lines) throws UncheckedIOException;

    /**
     * Creates {@code this} directory and any missing parent directories.
     *
     * @throws UncheckedIOException If {@code this} is not a directory or it's parent is a flat file.
     */
    void createDirectories() throws UncheckedIOException;
}
