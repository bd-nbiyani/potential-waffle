package com.search;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Google Search App.
 *
 * <p>Usage: run the jar and type your search query when prompted.
 * The app will fetch the top 5 Google results and open them in
 * your default browser as a nicely formatted HTML page.
 */
public class GoogleSearchApp {

    private final SearchService searchService;
    private final HtmlGenerator htmlGenerator;

    public GoogleSearchApp(SearchService searchService, HtmlGenerator htmlGenerator) {
        this.searchService = searchService;
        this.htmlGenerator = htmlGenerator;
    }

    /**
     * Runs the interactive search loop.
     *
     * @param in  input source (normally System.in)
     * @param out output writer (normally System.out)
     */
    public void run(Scanner in, PrintWriter out) {
        out.println("╔══════════════════════════════════════════╗");
        out.println("║         Google Search – Java App         ║");
        out.println("╚══════════════════════════════════════════╝");
        out.println();

        while (true) {
            out.print("Enter search query (or 'quit' to exit): ");
            out.flush();

            if (!in.hasNextLine()) {
                break;
            }
            String query = in.nextLine().trim();

            if (query.equalsIgnoreCase("quit") || query.equalsIgnoreCase("exit")) {
                out.println("Goodbye!");
                break;
            }
            if (query.isEmpty()) {
                out.println("Please enter a non-empty query.");
                continue;
            }

            out.println("Searching Google for: \"" + query + "\" ...");
            out.flush();

            try {
                List<SearchResult> results = searchService.search(query);

                if (results.isEmpty()) {
                    out.println("No results found. Google may have changed its HTML structure.");
                } else {
                    out.println("Found " + results.size() + " result(s). Opening browser...");
                    // Print a brief console summary as well
                    for (int i = 0; i < results.size(); i++) {
                        SearchResult r = results.get(i);
                        out.println("  " + (i + 1) + ". " + r.getTitle());
                        out.println("     " + r.getUrl());
                    }
                }

                String html = htmlGenerator.generate(query, results);
                openInBrowser(html, out);

            } catch (IOException e) {
                out.println("Error fetching results: " + e.getMessage());
                out.println("Please check your internet connection and try again.");
            }

            out.println();
        }
    }

    /**
     * Writes the HTML content to a temporary file and opens it in the
     * system's default browser.
     */
    private void openInBrowser(String html, PrintWriter out) {
        try {
            File tmpFile = Files.createTempFile("tmp-", ".html").toFile();
            tmpFile.deleteOnExit();

            try (PrintWriter writer = new PrintWriter(tmpFile, StandardCharsets.UTF_8)) {
                writer.print(html);
            }

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(tmpFile.toURI());
            } else {
                // Fallback: print file path so the user can open it manually
                out.println("Browser not supported. Open this file manually:");
                out.println("  " + tmpFile.getAbsolutePath());
            }
        } catch (IOException e) {
            out.println("Could not open browser: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        GoogleSearchApp app = new GoogleSearchApp(new SearchService(), new HtmlGenerator());
        try (Scanner scanner = new Scanner(System.in);
             PrintWriter writer = new PrintWriter(System.out, true)) {
            app.run(scanner, writer);
        }
    }
}
