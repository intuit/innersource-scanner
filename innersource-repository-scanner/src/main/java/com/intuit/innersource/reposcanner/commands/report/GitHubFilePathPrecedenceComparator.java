package com.intuit.innersource.reposcanner.commands.report;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

final class GitHubFilePathPrecedenceComparator
    implements Comparator<String>, Serializable {

    public static final GitHubFilePathPrecedenceComparator INSTANCE = new GitHubFilePathPrecedenceComparator();

    private static final long serialVersionUID = 1971989550190498686L;
    private static final int FIRST_BEFORE_SECOND = -1;
    private static final int FIRST_EQUAL_SECOND = 0;
    private static final int FIRST_AFTER_SECOND = 1;

    private GitHubFilePathPrecedenceComparator() {}

    @Override
    public int compare(final String first, final String second) {
        if ((first == null) && (second == null)) {
            return FIRST_EQUAL_SECOND;
        }
        if (first == null) {
            return FIRST_AFTER_SECOND;
        }
        if (second == null) {
            return FIRST_BEFORE_SECOND;
        }

        final String firstBaseName = FilenameUtils.getBaseName(first);
        final String secondBaseName = FilenameUtils.getBaseName(second);

        if (!StringUtils.equalsIgnoreCase(firstBaseName, secondBaseName)) {
            switch (firstBaseName.toLowerCase()) {
                case "readme":
                    return FIRST_BEFORE_SECOND;
                case "contributing":
                    switch (secondBaseName.toLowerCase()) {
                        case "readme":
                            return FIRST_AFTER_SECOND;
                        default:
                            return FIRST_BEFORE_SECOND;
                    }
                case "codeowners":
                    switch (secondBaseName.toLowerCase()) {
                        case "readme":
                        case "contributing":
                            return FIRST_AFTER_SECOND;
                        default:
                            return FIRST_BEFORE_SECOND;
                    }
                case "pull_request_template":
                    switch (secondBaseName.toLowerCase()) {
                        case "readme":
                        case "contributing":
                        case "codeowners":
                            return FIRST_AFTER_SECOND;
                        default:
                            return FIRST_BEFORE_SECOND;
                    }
                default:
                    switch (secondBaseName.toLowerCase()) {
                        case "readme":
                        case "contributing":
                        case "codeowners":
                        case "pull_request_template":
                            return FIRST_AFTER_SECOND;
                        default:
                            return String.CASE_INSENSITIVE_ORDER.compare(
                                firstBaseName,
                                secondBaseName
                            );
                    }
            }
        }

        // base filenames are equal ignoring case

        final String firstParentDirName = Optional
            .ofNullable(Paths.get(first).getParent())
            .map(Path::getFileName)
            .map(Path::toString)
            .orElse("/");

        final String secondParentDirName = Optional
            .ofNullable(Paths.get(second).getParent())
            .map(Path::getFileName)
            .map(Path::toString)
            .orElse("/");

        if (!StringUtils.equalsIgnoreCase(firstParentDirName, secondParentDirName)) {
            switch (firstParentDirName.toLowerCase()) {
                case ".github":
                    return FIRST_BEFORE_SECOND;
                case "docs":
                    return FIRST_AFTER_SECOND;
                case "/":
                    switch (secondParentDirName.toLowerCase()) {
                        case ".github":
                            return FIRST_AFTER_SECOND;
                        default:
                            return FIRST_BEFORE_SECOND;
                    }
                default:
                    switch (secondParentDirName.toLowerCase()) {
                        case ".github":
                        case "/":
                            return FIRST_AFTER_SECOND;
                        case "docs":
                            return FIRST_BEFORE_SECOND;
                        default:
                            return String.CASE_INSENSITIVE_ORDER.compare(
                                firstParentDirName,
                                secondParentDirName
                            );
                    }
            }
        }

        // parent directories are equal ignoring case

        if (!firstParentDirName.equals(secondParentDirName)) {
            return firstParentDirName.compareTo(secondParentDirName);
        }

        // parent directories are equal and the same case, base filenames are equal ignoring case

        final String firstPath = StringUtils.substringBeforeLast(first, firstBaseName);
        final String secondPath = StringUtils.substringBeforeLast(second, secondBaseName);

        if (firstPath.length() != secondPath.length()) {
            return Integer.compare(firstPath.length(), secondPath.length());
        }

        final String firstExtension = FilenameUtils.getExtension(first);
        final String secondExtension = FilenameUtils.getExtension(second);

        if (!StringUtils.equalsIgnoreCase(firstExtension, secondExtension)) {
            switch (firstExtension.toLowerCase()) {
                case "md":
                    return FIRST_BEFORE_SECOND;
                case "":
                    switch (secondExtension.toLowerCase()) {
                        case "md":
                            return FIRST_AFTER_SECOND;
                        default:
                            return FIRST_BEFORE_SECOND;
                    }
                default:
                    switch (secondExtension.toLowerCase()) {
                        case "md":
                        case "":
                            return FIRST_AFTER_SECOND;
                        default:
                            return String.CASE_INSENSITIVE_ORDER.compare(
                                firstExtension,
                                secondExtension
                            );
                    }
            }
        }

        // paths are equal, base file names are equal ignoring case, extensions are equal ignoring case

        if (!firstExtension.equals(secondExtension)) {
            return firstExtension.compareTo(secondExtension);
        }

        return first.compareTo(second);
    }
}
