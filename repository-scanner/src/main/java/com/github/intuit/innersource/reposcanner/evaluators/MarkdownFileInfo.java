package com.github.intuit.innersource.reposcanner.evaluators;

import java.util.List;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
public interface MarkdownFileInfo {
    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    interface SectionHeading {
        String getHeadingText();

        boolean hasContent();
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    interface ImageAltText {
        String getImageAltText();
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    interface CommentHint {
        String getHintText();

        boolean hintedElementHasDescription();
    }

    List<SectionHeading> getHeadings();

    List<ImageAltText> getImageAltTexts();

    List<CommentHint> getCommentHints();
}
