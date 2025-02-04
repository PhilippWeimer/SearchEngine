package de.fernunihagen.dbis.anguillasearch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
  
/**
 * Unit tests for the reverse index. 
 */

class ReverseIndexTests {
 
    static List<JsonObject> testPages;
    static JsonObject correctReverseIdex;
    static ReverseIndex reverseIndex;
 
    @BeforeAll
    static void setUp() throws IOException {

        testPages = Utils.parseAllJSONFiles(java.util.Optional.of("src/test/resources/tf-idf/pages"));
        correctReverseIdex = Utils.parseJSONFile("src/test/resources/tf-idf/index.json");

        reverseIndex = new ReverseIndex();
        reverseIndex.processDocuments(testPages);
    }

    @Test
    void reverseIdexTFIDF() {
        for (Entry<String, JsonElement> entry : correctReverseIdex.entrySet()) {
            // The token of the reverse index
            String token = entry.getKey();
            JsonObject pagesMap= entry.getValue().getAsJsonObject();
            
            for (Entry<String, JsonElement> pageEntry : pagesMap.entrySet()) {

                String url = pageEntry.getKey();
                Double tfidf = pageEntry.getValue().getAsDouble();

                System.out.println(tfidf);

                assertTrue(reverseIndex.getReverseIndex().containsKey(token), "Token fehlt im ReverseIndex: " + token);
                assertTrue(reverseIndex.getTokenInfo(token).containsKey(url), "URL fehlt für Token: " + token);

                Double indexTfidf = reverseIndex.getTokenInfo(token).get(url);
                
                assertTrue(Math.abs(tfidf - indexTfidf) < 0.0001);
            }
        }
    }
}