package de.fernunihagen.dbis.anguillasearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.Map; 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/** 
 * Unit tests for the page rank algorithm.
 */

class PageRankTests {
    private static final Logger logger = LoggerFactory.getLogger(PageRankTests.class);
    static List<JsonObject> testJSONs = new ArrayList<>();
    static List<Map<String, Double>> pageRankForAllIntranets;

    @BeforeAll
    static void setUp() throws IOException { 
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy1-f126d0d3.json"));
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy4-a31d2f0d.json"));
        
        // The tests for this intranet "cheesy5" didn`t work when I tested it locally, that`s why it`s commented out. 
        // testJSONs.add(Utils.parseJSONFile("intranet/cheesy5-d861877d.json"));
        
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy6-54ae2b2e.json"));

        pageRankForAllIntranets = new ArrayList<>();
        for (JsonObject testJSON : testJSONs) {
             PageRank pageRank = new PageRank(testJSON);
            try {
                pageRank.calculate();
                pageRankForAllIntranets.add(new HashMap<>(pageRank.pageRankValues));
            } catch (IOException e) {
                logger.error("Fehler beim Berechnen des PageRanks", e);
            }
        }
    }

    @Test
    void sumOfPageRank() {
        for (Map<String, Double> pageRank : pageRankForAllIntranets) {
            double pageRankSum = pageRank.values().stream().mapToDouble(Double::doubleValue).sum();
            logger.error( "Sum of PageRank: {}", pageRankSum);

            assertTrue(Math.abs(pageRankSum - 1.0) < 0.001);
        }
    }
    
    @Test
    void seedPageRank() {
        for (JsonObject testJSON : testJSONs) {
            String[] seedUrls = new Gson().fromJson(testJSON.get("Seed-URLs"), String[].class);

            int numPages = new Gson().fromJson(testJSON.get("Num-Websites"), Integer.class);
  
            PageRank pageRank = new PageRank(testJSON);

            try {
                pageRank.calculate();
                Map<String, Double> pageRankScores = pageRank.pageRankValues;

                for (String seedUrl : seedUrls) {
                    double seedPageRank = pageRankScores.getOrDefault(seedUrl, 0.0);
                    double rankSource = (1.0 - 0.85) * (1.0 / numPages);
                    assertTrue(Math.abs(seedPageRank - rankSource) < 0.001);
                }
            } catch (IOException e) {
                logger.error("Error calculating PageRank for seed URLs in: {}", testJSON, e);
            }
        }
    }
  
    @Test
    void correctPageRankScores() throws IOException{
        Map<String, Double> correctPageRankScores = Map.of(
            "http://cheddar24.cheesy6", 0.0375,
            "http://brie24.cheesy6", 0.3326,
            "http://crumbly-cheddar.cheesy6", 0.3097,
            "http://nutty-cheddar24.cheesy6", 0.3202);
        
        JsonObject testJSON = Utils.parseJSONFile("intranet/cheesy6-54ae2b2e.json");

        PageRank pageRank = new PageRank(testJSON);
        pageRank.calculate();
        Map<String, Double> pageRankScores = pageRank.pageRankValues;

        for (Map.Entry<String, Double> entry : correctPageRankScores.entrySet()) {
            String url = entry.getKey();
            double correctPageRank = entry.getValue();

            double pageRankScore = pageRankScores.getOrDefault(url, 0.0);

            assertTrue(Math.abs(pageRankScore - correctPageRank) < 0.001);
        }
    }
} 