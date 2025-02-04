package de.fernunihagen.dbis.anguillasearch;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import java.util.*;
 
/**
 * Main class of the AnguillaSearch project.
 */ 

public final class AnguillaSearch {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnguillaSearch.class);
     
    private AnguillaSearch() {
    } 

    /**
     * Main method.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        LOGGER.info("Starting AnguillaSearch...");

        if (args.length < 1) {
            LOGGER.error("Bitte geben Sie den Pfad zu einer JSON-Datei als Argument an.");
            return;
        } 

        Crawler crawler = new Crawler();

        String jsonFilePath = args[0];

        try { 
            JsonObject jsonFile = Utils.parseJSONFile(jsonFilePath);
            crawler.crawl(jsonFile);
            
            ReverseIndex reverseIndex = new ReverseIndex();
            reverseIndex = crawler.getReverseIndex();
            
            PageRank pageRank = new PageRank(jsonFile);
            pageRank.calculate();

            int crawledPages = crawler.getCrawledPages().size();

            LOGGER.info("Crawling and PageRank calculation completed");
            LOGGER.info("You can now start searching.");
            LOGGER.info("Pages indexed: {}", crawledPages);

            try (Scanner scanner = new Scanner(System.in)) {
                LOGGER.info("Welcome to AnguillaSearch!");

                while (true) { 
                    LOGGER.info("Enter search query (or 'exit' to quit): ");
                    String userQuery = scanner.nextLine();

                    if (userQuery.equalsIgnoreCase("exit")) {
                        LOGGER.info("Exiting. Goodbye!");
                        break;
                    }

                    List<Map.Entry<String, Double>> searchResults = reverseIndex.searchQuery(userQuery, true, jsonFile);

                    if (searchResults.isEmpty()) {
                        LOGGER.error("No results found for your query.");
                    } else {
                        int resultNumber = 1;
                        for (int i = 0; i < searchResults.size(); i++) {
                            Map.Entry<String, Double> result = searchResults.get(i);
                            String url = result.getKey();

                            PageData pageData = crawler.getCrawledPages().stream()
                                    .filter(page -> page.getURL().equals(url))
                                    .findFirst()
                                    .orElse(null);

                            String title = (pageData != null) ? pageData.getTitle() : "No title available";
                            LOGGER.info("Result {}:", resultNumber);
                            LOGGER.info("URL: {}", url);
                            LOGGER.info("Title: \"{}\"", title);
                            LOGGER.info(" ");
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error during crawling or processing the file: " + jsonFilePath, e);
        }

        /*
         * Set the java.awt.headless property to true to prevent awt from opening windows.
         * If the property is not set to true, the program will throw an exception when trying to 
         * generate the graph visualizations in a headless environment.
         */
        System.setProperty("java.awt.headless", "true");
        LOGGER.info("Java awt GraphicsEnvironment headless: {}", java.awt.GraphicsEnvironment.isHeadless());
    }
}

