package de.fernunihagen.dbis.anguillasearch;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import com.google.gson.JsonObject; 

/**
 * Unit tests for the search.
 */ 
 
class SearchTests {

    @Test  
    void findCorrectURLs() throws IOException {
        JsonObject testJSON = Utils.parseJSONFile("intranet/cheesy1-f126d0d3.json");
        String query = String.join(" ", new Gson().fromJson(testJSON.get("Query-Token"), String[].class));        
        String[] expectedURLs = new Gson().fromJson(testJSON.get("Query-URLs"), String[].class);
        
        Crawler crawler = new Crawler();
        crawler.crawl(testJSON);

        ReverseIndex reverseIndex = crawler.getReverseIndex();

        List<Map.Entry<String, Double>> searchResults = reverseIndex.searchQuery(query, false, testJSON);

        List<String> foundURLs = new ArrayList<>();
        
        for (Map.Entry<String, Double> entry : searchResults) {
            foundURLs.add(entry.getKey());
        }
    
        assertTrue(foundURLs.containsAll(Arrays.asList(expectedURLs)));
    }
} 