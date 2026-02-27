package com.search;

/**
 * Represents a single Google search result with its title, URL and snippet.
 */
public class SearchResult {

    private final String title;
    private final String url;
    private final String snippet;

    public SearchResult(String title, String url, String snippet) {
        this.title = title;
        this.url = url;
        this.snippet = snippet;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSnippet() {
        return snippet;
    }

    @Override
    public String toString() {
        return "SearchResult{title='" + title + "', url='" + url + "', snippet='" + snippet + "'}";
    }
}
