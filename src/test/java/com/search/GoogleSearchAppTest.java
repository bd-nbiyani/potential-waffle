package com.search;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GoogleSearchAppTest {

    /** A SearchService stub that always returns a fixed empty list. */
    static class StubSearchService extends SearchService {
        @Override
        public java.util.List<SearchResult> search(String query) {
            return java.util.Collections.emptyList();
        }
    }

    private GoogleSearchApp createApp() {
        return new GoogleSearchApp(new StubSearchService(), new HtmlGenerator());
    }

    @Test
    void runExitsOnQuitCommand() {
        Scanner in = new Scanner("quit\n");
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw, true);

        createApp().run(in, out);

        assertTrue(sw.toString().contains("Goodbye!"));
    }

    @Test
    void runExitsOnExitCommand() {
        Scanner in = new Scanner("exit\n");
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw, true);

        createApp().run(in, out);

        assertTrue(sw.toString().contains("Goodbye!"));
    }

    @Test
    void runPromptsPrintsWarningForEmptyQuery() {
        // Empty query → warning, then quit
        Scanner in = new Scanner("\nquit\n");
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw, true);

        createApp().run(in, out);

        String output = sw.toString();
        assertTrue(output.contains("non-empty query"), "Should warn about empty input");
    }

    @Test
    void runHandlesSearchAndPrintsResults() {
        // "java" → search (stub returns empty) → quit
        Scanner in = new Scanner("java\nquit\n");
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw, true);

        createApp().run(in, out);

        String output = sw.toString();
        assertTrue(output.contains("Searching Google for: \"java\""),
                "Should echo the search query");
    }
}
