package com.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    private final SearchService service = new SearchService();

    /**
     * Minimal synthetic HTML that mimics Google's organic result structure
     * (div.g with h3 title, a[href], and div.VwiC3b snippet).
     */
    private static final String MOCK_HTML =
            "<html><body>" +
            "<div class=\"g\">" +
            "  <a href=\"https://example.com/page1\"><h3>Result One</h3></a>" +
            "  <div class=\"VwiC3b\">Snippet for result one.</div>" +
            "</div>" +
            "<div class=\"g\">" +
            "  <a href=\"https://example.org/page2\"><h3>Result Two</h3></a>" +
            "  <div class=\"VwiC3b\">Snippet for result two.</div>" +
            "</div>" +
            "<div class=\"g\">" +
            "  <a href=\"https://foo.com\"><h3>Result Three</h3></a>" +
            "</div>" +
            "</body></html>";

    @Test
    void parseResultsExtractsTitleUrlAndSnippet() {
        Document doc = Jsoup.parse(MOCK_HTML);
        List<SearchResult> results = service.parseResults(doc);

        assertEquals(3, results.size());

        SearchResult first = results.get(0);
        assertEquals("Result One", first.getTitle());
        assertEquals("https://example.com/page1", first.getUrl());
        assertEquals("Snippet for result one.", first.getSnippet());
    }

    @Test
    void parseResultsReturnsAtMostFiveResults() {
        // Build HTML with 8 result blocks
        StringBuilder html = new StringBuilder("<html><body>");
        for (int i = 1; i <= 8; i++) {
            html.append("<div class=\"g\">")
                .append("<a href=\"https://example.com/").append(i).append("\">")
                .append("<h3>Result ").append(i).append("</h3></a>")
                .append("<div class=\"VwiC3b\">Snippet ").append(i).append("</div>")
                .append("</div>");
        }
        html.append("</body></html>");

        Document doc = Jsoup.parse(html.toString());
        List<SearchResult> results = service.parseResults(doc);

        assertEquals(5, results.size(), "Should return at most 5 results");
    }

    @Test
    void parseResultsSkipsBlocksWithoutTitle() {
        String html =
                "<html><body>" +
                "<div class=\"g\"><a href=\"https://example.com\"></a></div>" +
                "<div class=\"g\"><a href=\"https://valid.com\"><h3>Valid</h3></a></div>" +
                "</body></html>";

        Document doc = Jsoup.parse(html);
        List<SearchResult> results = service.parseResults(doc);

        assertEquals(1, results.size());
        assertEquals("Valid", results.get(0).getTitle());
    }

    @Test
    void parseResultsSkipsBlocksWithNonHttpLinks() {
        String html =
                "<html><body>" +
                "<div class=\"g\"><a href=\"/relative/path\"><h3>Relative Link</h3></a></div>" +
                "<div class=\"g\"><a href=\"https://good.com\"><h3>Absolute Link</h3></a></div>" +
                "</body></html>";

        Document doc = Jsoup.parse(html);
        List<SearchResult> results = service.parseResults(doc);

        assertEquals(1, results.size());
        assertEquals("Absolute Link", results.get(0).getTitle());
    }

    @Test
    void parseResultsUnwrapsGoogleRedirectUrls() {
        String html =
                "<html><body>" +
                "<div class=\"g\">" +
                "  <a href=\"/url?q=https://target.com/page&amp;sa=U\"><h3>Redirect</h3></a>" +
                "</div>" +
                "</body></html>";

        Document doc = Jsoup.parse(html);
        List<SearchResult> results = service.parseResults(doc);

        // /url?q= prefix should be stripped
        assertEquals(1, results.size());
        assertEquals("https://target.com/page", results.get(0).getUrl());
    }

    @Test
    void parseResultsReturnsEmptyListForEmptyDocument() {
        Document doc = Jsoup.parse("<html><body></body></html>");
        List<SearchResult> results = service.parseResults(doc);
        assertTrue(results.isEmpty());
    }
}
