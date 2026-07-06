package org.stanb.epubrepair.normalize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class SelfCloseElementNormalizerTest {

  private final SelfCloseElementNormalizer normalizer =
      new SelfCloseElementNormalizer();

  @Test
  void selfClosesEverySupportedEmptyElement() {
    String source = """
        <area>
        <base href="book/">
        <br>
        <col span="2">
        <hr>
        <img src="cover.png" alt="">
        <input type="text">
        <link rel="stylesheet" href="book.css">
        <meta name="author" content="Stan">
        <param name="x" value="y">
        """;

    String expected = """
        <area />
        <base href="book/" />
        <br />
        <col span="2" />
        <hr />
        <img src="cover.png" alt="" />
        <input type="text" />
        <link rel="stylesheet" href="book.css" />
        <meta name="author" content="Stan" />
        <param name="x" value="y" />
        """;

    assertEquals(expected, normalizer.normalize(source));
  }

  @Test
  void supportsMultilineTagsAndGreaterThanSignsInsideQuotes() {
    String source = """
        <link
          rel="stylesheet"
          data-test="a > b"
          href="book.css">
        """;

    String expected = """
        <link
          rel="stylesheet"
          data-test="a > b"
          href="book.css" />
        """;

    assertEquals(expected, normalizer.normalize(source));
  }

  @Test
  void leavesAlreadySelfClosedElementsUnchanged() {
    String source = "<img src=\"cover.png\" alt=\"\" />";

    assertEquals(source, normalizer.normalize(source));
  }

  @Test
  void leavesNonEmptyElementsUnchanged() {
    String source = "<p>Text</p><div><span>More</span></div>";

    assertEquals(source, normalizer.normalize(source));
  }

  @Test
  void ignoresCommentsCdataProcessingInstructionsAndRawText() {
    String source = """
        <!-- <img src="comment.png"> -->
        <![CDATA[<br>]]>
        <?repair <hr>?>
        <script>const value = "<img src='script.png'>";</script>
        <style>.x::before { content: "<br>"; }</style>
        """;

    assertEquals(source, normalizer.normalize(source));
  }

  @Test
  void isIdempotent() {
    String source = "<link rel=\"stylesheet\" href=\"book.css\">";

    String firstRun = normalizer.normalize(source);
    String secondRun = normalizer.normalize(firstRun);

    assertEquals(firstRun, secondRun);
  }
}
