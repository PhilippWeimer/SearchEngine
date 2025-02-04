package de.fernunihagen.dbis.anguillasearch;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test; 
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;

/**  
 * Unit tests for the crawler.
 */

class CrawlerTests {
    static List<JsonObject> testJSONs = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(AnguillaSearch.class);

    @BeforeAll
    static void setUp() throws IOException {
        // Load the metadata from the JSON file
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy1-f126d0d3.json"));
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy2-c79b0581.json"));
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy3-7fdaa098.json"));
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy4-a31d2f0d.json"));
        // Big net testJSONs.add(Utils.parseJSONFile("intranet/cheesy5-d861877d.json"));
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy6-54ae2b2e.json"));
    }

    @Test
    void crawlAllWebsitesInProvidedNetwork() {
        for (JsonObject testJSON : testJSONs) {
            
            Crawler crawler = new Crawler();
            int numPagesCrawled = 0;

            try { 
                numPagesCrawled = crawler.getVisitedWebsites(testJSON);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Error while crawling the file: " + testJSON, e);
            }
            
            assertEquals(testJSON.get("Num-Websites").getAsInt(), numPagesCrawled);
        }
    }

    @Test
    void findCorrectNumberOfLinks() {
        for (JsonObject testJSON : testJSONs) {
            
            Crawler crawler = new Crawler();
            int numLinks = 0;
 
            try {
                crawler.crawl(testJSON);
                numLinks = crawler.getAllLinksFromJSON(testJSON);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Fehler beim Crawlen der Datei: " + testJSON, e);
            }
   
            assertEquals(testJSON.get("Num-Links").getAsInt(), numLinks);
        }
    }
}
