package com.github.intuit.innersource.reposcanner.commands.report;

import com.github.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Reference;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.IRichSequence;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

final class MarkdownFileNodeVisitor extends NodeVisitor implements MarkdownFileInfo {

    private static final class ContentFinder extends NodeVisitor {

        private final AtomicBoolean foundContent = new AtomicBoolean(false);

        ContentFinder() {
            super();
            super.addActionHandler(new VisitHandler<>(Node.class, this::visitNode));
        }

        public void visitNode(final Node n) {
            if (MarkdownFileNodeVisitor.isContentNode(n)) {
                foundContent.set(true);
                return;
            }
            this.visitChildren(n);
        }

        @Override
        public Visitor<Node> getAction(final Node node) {
            return this::visitNode;
        }

        public boolean foundContent() {
            return foundContent.get();
        }

        public void reset() {
            foundContent.set(false);
        }
    }

    private final List<SectionHeading> headings = Lists.newArrayList();
    private final List<ImageAltText> imageAltTexts = Lists.newArrayList();
    private final List<CommentHint> commentHints = Lists.newArrayList();

    @SuppressWarnings("unchecked")
    MarkdownFileNodeVisitor() {
        super.addActionHandler(
            (VisitHandler) new VisitHandler<>(Heading.class, this::visitHeading)
        );
        super.addActionHandler(
            (VisitHandler) new VisitHandler<>(Reference.class, this::visitReference)
        );
        super.addActionHandler(
            (VisitHandler) new VisitHandler<>(Image.class, this::visitImage)
        );
    }

    @Override
    public List<SectionHeading> getHeadings() {
        return headings;
    }

    @Override
    public List<ImageAltText> getImageAltTexts() {
        return imageAltTexts;
    }

    @Override
    public List<CommentHint> getCommentHints() {
        return commentHints;
    }

    public void visitHeading(final Heading heading) {
        final String headingText = extractText(heading);

        if (Strings.isNullOrEmpty(headingText)) {
            this.visitChildren(heading);
            return;
        }

        if (isCommentHint(heading.getPrevious())) {
            this.visitChildren(heading);
            return;
        }

        final boolean hasSectionContent = sectionHasContent(heading, heading.getLevel());

        this.headings.add(
                new SectionHeading() {
                    @Override
                    public String getHeadingText() {
                        return headingText;
                    }

                    @Override
                    public boolean hasContent() {
                        return hasSectionContent;
                    }
                }
            );
    }

    public void visitImage(final Image image) {
        final String imageAltText = extractText(image);

        if (Strings.isNullOrEmpty(imageAltText)) {
            this.visitChildren(image);
            return;
        }

        if (isCommentHint(image.getPrevious())) {
            this.visitChildren(image);
            return;
        }

        this.imageAltTexts.add(() -> imageAltText);
    }

    public void visitReference(final Reference reference) {
        if (!isCommentHint(reference)) {
            this.visitChildren(reference);
            return;
        }

        final String commentHintText = Optional
            .of(reference)
            .map(Reference::getTitle)
            .map(BasedSequence::toString)
            .get();

        final boolean hintedElementHasDescription = sectionHasContent(
            Optional
                .of(reference)
                .map(Node::getNext)
                .map(Node::getNext)
                .filter(node -> node instanceof Paragraph)
                .orElse(null),
            1
        );

        this.commentHints.add(
                new CommentHint() {
                    @Override
                    public String getHintText() {
                        return commentHintText;
                    }

                    @Override
                    public boolean hintedElementHasDescription() {
                        return hintedElementHasDescription;
                    }
                }
            );
    }

    private static String extractText(final Node node) {
        return Optional
            .ofNullable(new TextCollectingVisitor().collectAndGetText(node))
            .orElse(null);
    }

    private static boolean isCommentHint(final @Nullable Node node) {
        return Optional
            .ofNullable(node)
            .filter(n -> n instanceof Reference)
            .map(ref -> (Reference) ref)
            .map(
                ref ->
                    Tables.immutableCell(ref.getReference(), ref.getUrl(), ref.getTitle())
            )
            .filter(triple -> Objects.nonNull(triple.getRowKey()))
            .filter(triple -> Objects.nonNull(triple.getColumnKey()))
            .filter(triple -> Objects.nonNull(triple.getValue()))
            .filter(
                triple -> StringUtils.equals(triple.getRowKey().toStringOrNull(), "//")
            )
            .filter(
                triple -> StringUtils.equals(triple.getColumnKey().toStringOrNull(), "#")
            )
            .map(Table.Cell::getValue)
            .map(IRichSequence::toStringOrNull)
            .map(StringUtils::isNotBlank)
            .orElse(false);
    }

    private static boolean isContentNode(final Node nodeToCheck) {
        return !isHeadingNode(nodeToCheck) && !isCommentHint(nodeToCheck);
    }

    private static boolean isHeadingNode(final Node nodeToCheck) {
        final boolean hasHeadingParent = StreamSupport
            .stream(
                Traverser
                    .forTree(
                        (SuccessorsFunction<Node>) root ->
                            Optional
                                .ofNullable(root.getParent())
                                .map(Lists::newArrayList)
                                .orElse(Lists.newArrayList())
                    )
                    .depthFirstPreOrder(nodeToCheck)
                    .spliterator(),
                false
            )
            .anyMatch(node -> node instanceof Heading);

        return (nodeToCheck instanceof Heading) || hasHeadingParent;
    }

    private static boolean sectionHasContent(
        final Node sectionRootNode,
        final int sectionRootNodeLevel
    ) {
        if (sectionRootNode == null) {
            return false;
        }
        Node nodeToCheck = sectionRootNode;
        final ContentFinder contentFinder = new ContentFinder();
        do {
            contentFinder.reset();
            contentFinder.visitChildren(nodeToCheck);
            if (contentFinder.foundContent()) {
                return true;
            }
            if (isContentNode(nodeToCheck)) {
                return true;
            }
            if (
                (nodeToCheck != sectionRootNode) &&
                isHeadingNode(nodeToCheck) &&
                (((Heading) nodeToCheck).getLevel() <= sectionRootNodeLevel)
            ) {
                return false;
            }
            nodeToCheck = nodeToCheck.getNext();
        } while (nodeToCheck != null);
        return false;
    }
}
