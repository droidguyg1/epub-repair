package org.stanb.epubrepair.normalize;

import java.util.Set;

/** Self-closes known XHTML empty elements before XML parsing. */
public final class SelfCloseElementNormalizer implements PreParseNormalizer {

  private static final Set<String> EMPTY_ELEMENTS = Set.of(
      "area", "base", "br", "col", "hr",
      "img", "input", "link", "meta", "param");
  public static final String ID = "self-close-element";

  @Override
  public String id() {
    return ID;
  }

  @Override
  public String normalize(String source) {
    StringBuilder result = new StringBuilder(source.length());
    int position = 0;

    while (position < source.length()) {
      if (source.startsWith("<!--", position)) {
        position = copyThrough(source, result, position, "-->");
      } else if (source.startsWith("<![CDATA[", position)) {
        position = copyThrough(source, result, position, "]]>");
      } else if (source.startsWith("<?", position)) {
        position = copyThrough(source, result, position, "?>");
      } else if (source.charAt(position) == '<') {
        Tag tag = readTag(source, position);

        if (tag == null) {
          result.append(source.charAt(position++));
        } else if (isRawTextElement(tag)) {
          result.append(source, position, tag.end());
          position = copyRawTextElement(source, result, tag);
        } else {
          appendNormalizedTag(source, result, tag);
          position = tag.end();
        }
      } else {
        result.append(source.charAt(position++));
      }
    }

    return result.toString();
  }

  private int copyThrough(
      String source,
      StringBuilder result,
      int start,
      String terminator) {

    int terminatorStart = source.indexOf(terminator, start);
    int end = terminatorStart < 0
        ? source.length()
        : terminatorStart + terminator.length();

    result.append(source, start, end);
    return end;
  }

  private Tag readTag(String source, int start) {
    int position = start + 1;

    if (position >= source.length()) {
      return null;
    }

    char first = source.charAt(position);

    if (first == '!' || first == '?') {
      int end = findTagEnd(source, position + 1);
      return end < 0 ? null : new Tag("", start, end + 1, false, false);
    }

    boolean closing = first == '/';

    if (closing) {
      position++;
    }

    int nameStart = position;

    while (position < source.length()
        && isNameCharacter(source.charAt(position))) {
      position++;
    }

    if (position == nameStart) {
      return null;
    }

    String name = source.substring(nameStart, position);
    int tagEnd = findTagEnd(source, position);

    if (tagEnd < 0) {
      return null;
    }

    int slashPosition = tagEnd - 1;

    while (slashPosition > position
        && Character.isWhitespace(source.charAt(slashPosition))) {
      slashPosition--;
    }

    boolean selfClosed = source.charAt(slashPosition) == '/';
    return new Tag(name, start, tagEnd + 1, closing, selfClosed);
  }

  private int findTagEnd(String source, int start) {
    char quote = 0;

    for (int position = start; position < source.length(); position++) {
      char current = source.charAt(position);

      if (quote != 0) {
        if (current == quote) {
          quote = 0;
        }
      } else if (current == '"' || current == '\'') {
        quote = current;
      } else if (current == '>') {
        return position;
      }
    }

    return -1;
  }

  private boolean isNameCharacter(char character) {
    return Character.isLetterOrDigit(character)
        || character == ':'
        || character == '-'
        || character == '_'
        || character == '.';
  }

  private boolean isRawTextElement(Tag tag) {
    return !tag.closing()
        && !tag.selfClosed()
        && (tag.name().equals("script") || tag.name().equals("style"));
  }

  private int copyRawTextElement(
      String source,
      StringBuilder result,
      Tag openingTag) {

    String closingTag = "</" + openingTag.name();
    int closingStart = source.indexOf(closingTag, openingTag.end());

    if (closingStart < 0) {
      result.append(source, openingTag.end(), source.length());
      return source.length();
    }

    result.append(source, openingTag.end(), closingStart);
    Tag closing = readTag(source, closingStart);

    if (closing == null) {
      result.append(source, closingStart, source.length());
      return source.length();
    }

    result.append(source, closingStart, closing.end());
    return closing.end();
  }

  private void appendNormalizedTag(
      String source,
      StringBuilder result,
      Tag tag) {

    if (!tag.closing()
        && !tag.selfClosed()
        && EMPTY_ELEMENTS.contains(tag.name())) {

      int closingBracket = tag.end() - 1;
      int insertionPoint = closingBracket;

      while (insertionPoint > tag.start()
          && Character.isWhitespace(source.charAt(insertionPoint - 1))) {
        insertionPoint--;
      }

      result.append(source, tag.start(), closingBracket);

      if (closingBracket > tag.start()
          && Character.isWhitespace(source.charAt(closingBracket - 1))) {
        result.append("/>");
      } else {
        result.append(" />");
      }
    } else {
      result.append(source, tag.start(), tag.end());
    }
  }

  private record Tag(
      String name,
      int start,
      int end,
      boolean closing,
      boolean selfClosed) {
  }
}
