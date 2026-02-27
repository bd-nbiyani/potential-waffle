package com.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches the top search results from Google for a given query.
 * Uses JSoup to parse the HTML response from Google's search page.
 *
 * <p><strong>Note:</strong> This implementation scrapes Google's search results page.
 * This may violate Google's Terms of Service and can be fragile if Google changes its
 * HTML structure. For a stable, ToS-compliant alternative, consider using the
 * <a href="https://developers.google.com/custom-search/v1/overview">Google Custom Search JSON API</a>,
 * which requires an API key and a Programmable Search Engine ID (cx).
 */
public class SearchService {

    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/131.0.0.0 Safari/537.36";
    private static final int MAX_RESULTS = 5;
    private static final int MIN_SNIPPET_LENGTH = 20;

    /**
     * Searches Google for the given query and returns the top results.
     *
     * @param query the search query entered by the user
     * @return list of up to {@value MAX_RESULTS} search results
     * @throws IOException if the network request fails
     */
    public List<SearchResult> search(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String searchUrl = GOOGLE_SEARCH_URL + encodedQuery + "&num=10&hl=en";

        Document doc = Jsoup.connect(searchUrl)
                .userAgent(USER_AGENT)
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .timeout(15_000)
                .get();

        return parseResults(doc);
    }

    /**
     * Parses organic search result cards from the Google results page.
     * Google's HTML structure may change; the selectors below target the
     * standard result blocks (div.g) that have been stable across several
     * Chrome versions.
     */
    List<SearchResult> parseResults(Document doc) {
        List<SearchResult> results = new ArrayList<>();

        // Each organic result is wrapped in a <div class="g"> container
        Elements resultBlocks = doc.select("div.g");

        for (Element block : resultBlocks) {
            if (results.size() >= MAX_RESULTS) {
                break;
            }

            // Title is in the first <h3> inside the block
            Element titleEl = block.selectFirst("h3");
            if (titleEl == null) {
                continue;
            }
            String title = titleEl.text().trim();
            if (title.isEmpty()) {
                continue;
            }

            // URL is in the first <a> ancestor that contains href
            Element linkEl = block.selectFirst("a[href]");
            if (linkEl == null) {
                continue;
            }
            String href = linkEl.attr("href");
            // Google sometimes wraps URLs with /url?q=... — unwrap and decode them
            if (href.startsWith("/url?q=")) {
                href = href.substring(7);
                int ampIdx = href.indexOf('&');
                if (ampIdx != -1) {
                    href = href.substring(0, ampIdx);
                }
                href = URLDecoder.decode(href, StandardCharsets.UTF_8);
            }
            if (!href.startsWith("http")) {
                continue;
            }

            // Snippet is typically in <div class="VwiC3b"> or the first <span> with text
            String snippet = "";
            Element snippetEl = block.selectFirst("div.VwiC3b");
            if (snippetEl != null) {
                snippet = snippetEl.text().trim();
            }
            if (snippet.isEmpty()) {
                // Fallback: first non-empty text span
                for (Element span : block.select("span")) {
                    String text = span.text().trim();
                    if (text.length() > MIN_SNIPPET_LENGTH) {
                        snippet = text;
                        break;
                    }
                }
            }

            results.add(new SearchResult(title, href, snippet));
        }

        return results;
    }
}
