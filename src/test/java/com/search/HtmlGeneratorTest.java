package com.search;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HtmlGeneratorTest {

    private final HtmlGenerator generator = new HtmlGenerator();

    @Test
    void generateProducesValidHtmlWithResults() {
        List<SearchResult> results = Arrays.asList(
                new SearchResult("Java Programming", "https://java.com", "Learn Java."),
                new SearchResult("OpenJDK", "https://openjdk.org", "Open source JDK.")
        );

        String html = generator.generate("java", results);

        assertTrue(html.contains("<!DOCTYPE html>"), "Should start with DOCTYPE");
        assertTrue(html.contains("<title>Search Results: java</title>"));
        assertTrue(html.contains("Java Programming"));
        assertTrue(html.contains("https://java.com"));
        assertTrue(html.contains("Learn Java."));
        assertTrue(html.contains("OpenJDK"));
        assertTrue(html.contains("Top 2 results"));
    }

    @Test
    void generateShowsNoResultsMessageWhenListEmpty() {
        String html = generator.generate("unknown xyz", Collections.emptyList());

        assertTrue(html.contains("No results found"));
        assertFalse(html.contains("class=\"results-grid\""), "results-grid div should not appear when list is empty");
    }

    @Test
    void generateEscapesSpecialCharactersInQuery() {
        String html = generator.generate("<script>alert('xss')</script>", Collections.emptyList());

        assertFalse(html.contains("<script>"), "Raw <script> tag must not appear in output");
        assertTrue(html.contains("&lt;script&gt;"), "Script tag should be HTML-escaped");
    }

    @Test
    void generateEscapesSpecialCharactersInResultFields() {
        List<SearchResult> results = Collections.singletonList(
                new SearchResult("Title <b>bold</b>", "https://example.com", "Snippet & more \"text\"")
        );

        String html = generator.generate("test", results);

        assertFalse(html.contains("<b>bold</b>"), "Unescaped HTML in title must not appear");
        assertTrue(html.contains("&lt;b&gt;bold&lt;/b&gt;"));
        assertTrue(html.contains("&amp;"));
        assertTrue(html.contains("&quot;"));
    }

    @Test
    void escapeHtmlHandlesNullInput() {
        assertEquals("", HtmlGenerator.escapeHtml(null));
    }

    @Test
    void escapeHtmlHandlesAllSpecialCharacters() {
        String input  = "& < > \" '";
        String output = HtmlGenerator.escapeHtml(input);
        assertEquals("&amp; &lt; &gt; &quot; &#x27;", output);
    }

    @Test
    void generateResultCountSingularVsPlural() {
        List<SearchResult> one = Collections.singletonList(
                new SearchResult("Only One", "https://example.com", ""));
        String html = generator.generate("q", one);
        assertTrue(html.contains("Top 1 result<"), "Should say 'result' not 'results' for a single item");

        List<SearchResult> two = Arrays.asList(
                new SearchResult("First", "https://example.com", ""),
                new SearchResult("Second", "https://example.org", ""));
        String html2 = generator.generate("q", two);
        assertTrue(html2.contains("Top 2 results<"));
    }

    @Test
    void generateHandlesUrlsWithQueryParameters() {
        List<SearchResult> results = Collections.singletonList(
                new SearchResult("Query Param Test", "https://example.com?a=1&b=2", "Has query params")
        );

        String html = generator.generate("query", results);

        assertTrue(
                html.contains("href=\"https://example.com?a=1&amp;b=2\""),
                "Href should contain URL with query parameters and '&' escaped as '&amp;'"
        );
    }
}
