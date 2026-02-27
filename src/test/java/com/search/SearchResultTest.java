package com.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchResultTest {

    @Test
    void constructorAndGettersReturnCorrectValues() {
        SearchResult r = new SearchResult("My Title", "https://example.com", "A short snippet.");
        assertEquals("My Title", r.getTitle());
        assertEquals("https://example.com", r.getUrl());
        assertEquals("A short snippet.", r.getSnippet());
    }

    @Test
    void toStringContainsAllFields() {
        SearchResult r = new SearchResult("Title", "https://example.com", "Snippet");
        String s = r.toString();
        assertTrue(s.contains("Title"));
        assertTrue(s.contains("https://example.com"));
        assertTrue(s.contains("Snippet"));
    }
}
