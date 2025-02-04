package de.fernunihagen.dbis.anguillasearch;
 
import java.io.IOException;
import java.util.*;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/**
 * The class "ReverseIndex" implements a datastructure, which recieves a user input as a String and 
 * crawls the intranet and returns an Array of urls (Document-IDs from the urls) as a result.  
 */
  
public class ReverseIndex {

    /*
     * This Map represents the Reverse-Index. The outer-map uses the Token (String) as the key and another inner-map as the Value.
     * The inner-map uses the Document-ID (String) as the key and the TF-IDF-score from the Token as a value (Double)-
     */ 

    private Map<String, Map<String, Double>> reversedIndex = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(ReverseIndex.class);

    /**  
     * Adds a token to the Reverse Index.
     * 
     * @param token  this token will be added to the reverse index.
     * @param docID  the Document-ID in which the token appears 
     * @param tfidf  The TF-IDF-score related to the token in the document.
     */ 

    public void addToken(String token, String docID, double tfidf) {
        Map<String, Double> innerMap = reversedIndex.computeIfAbsent(token, k -> new HashMap<>());

        if (innerMap == null) {
            innerMap = new HashMap<>();
            reversedIndex.put(token, innerMap);
        }
        innerMap.put(docID, tfidf);
    }

    /** 
     * Returns the full Reverse Index.
     * 
     * @return  The Reverse Index as a map of tokens to the document/tfidf mappings.
     */

    public Map<String, Map<String, Double>> getReverseIndex() {
        return reversedIndex;
    }

    /** 
     * Returns the information for a specific token from the Reverse Index.
     * 
     * @param token  the specific token, from which the information is needed for.
     * 
     * @return  a map of Document-IDs related to TF-IDF-scores for the given token.
     */

    public Map<String, Double> getTokenInfo(String token) {
        return reversedIndex.getOrDefault(token, new HashMap<>());
    }

    /** 
     * This method is using an input content (String) and removes predefined stopwords, applies Lemmatization, convertes all words into lower case.
     * 
     * @param content  the content which should be tokenized.
     * 
     * @return  a list of tokenized tokens.
     */

    public List<String> tokenizeContent(String content) {

        List<String> tokens = Arrays.asList(content.toLowerCase().split("\\W+"));

        // Removes predefined stopwords and return the filtered tokens.
        return tokens.stream()
                     .filter(token -> !Stopwords.ENGLISH_STOPWORDS.contains(token))
                     .toList();
    }

    /** 
     * This method uses a list of documents as an input, calculates the TF-IDF.
     * 
     * @param documents  the list of documents to process, each as a JsonObject.
     */

    public void processDocuments(List<JsonObject> documents) {
        int totalDocuments = documents.size();
        Map<String, Integer> documentFrequencies = new HashMap<>();
 
        // Calculate the document frequencies which is used to calculate the TF-IDF
        for(JsonObject doc : documents) {
            String content = doc.get("title").getAsString() + " " +
                             doc.get("headings").getAsString() + " " + 
                             doc.get("paragraphs").getAsString();
            List<String> tokens = tokenizeContent(content);
            Set<String> uniqueTokens = new HashSet<>(tokens);

            for (String token : uniqueTokens) {
                documentFrequencies.put(token, documentFrequencies.getOrDefault(token, 0) + 1);
            }
        }

        // Calculating the TF-IDF
        for(JsonObject doc : documents) {
            String docID = doc.get("url").getAsString();
            String content = doc.get("title").getAsString() + " " +
                             doc.get("headings").getAsString() + " " +
                             doc.get("paragraphs").getAsString();
    
            List<String> tokens = tokenizeContent(content);

            Map<String, Double> tfidfValues = TFIDF.calculate(tokens, totalDocuments, documentFrequencies);

            for (Map.Entry<String, Double> entry : tfidfValues.entrySet()) {
                String token = entry.getKey();
                double tfidf = entry.getValue();
    
                addToken(token, docID, tfidf);
            }
        }
    }
    
    /**
     * This is the most important method and is used in the main-Search. Given a user input (String) this method searches the 
     * Reverse Index for documents, which are mathing the input-token. it used the PageRank-algorithm and optionally the 
     * Cosine Similarity for sorting the ranking results. 
     * 
     * @param searchQuery  the search query as a string
     * @param useCosineSimilarity  whether to use Cosine Similarity in the ranking 
     * @param json  a JSON-Object containing PageRank data
     * 
     * @return  a sorted list of Document-IDs and their TF-IDF-scores.
     */

    public List<Map.Entry<String, Double>> searchQuery (String searchQuery, boolean useCosineSimilarity, JsonObject json) {
        List<String> tokens = tokenizeContent(searchQuery);
        Vector vektor = new Vector();
        Map<String, Double> results = new HashMap<>();
        Map<String, Double> cosineSim = new HashMap<>();
        Map<String, Vector> vektors = new HashMap<>();

        // Calculates the PageRank-scores.
        PageRank pageRank = new PageRank(json);
        Map<String, Double> pageRanks = new HashMap<>();
        
        try {
            pageRank.calculate();
            pageRanks = pageRank.pageRankValues;
        } catch (IOException e) {
            logger.error("PageRank calculation failed: {}", e.getMessage(), e);
        }

        // Optional use of Cosine Similarity for ranking 
        if(useCosineSimilarity) {
            for (String token : tokens) {
                vektor.addToken(token, 1);
            }
        }

        for (String token : tokens) {
            Map<String, Double> tokenInfo = getTokenInfo(token);

            for (Map.Entry<String, Double> entry : tokenInfo.entrySet()) {
                String url = entry.getKey();
                double tfidf = entry.getValue();

                if(useCosineSimilarity) {
                    vektors.put(url, new Vector());
                    vektors.get(url).addToken(token, tfidf);
                }
                else {
                    results.put(url, results.getOrDefault(url, 0.0) + tfidf);
                }
            } 
        } 

        // Calculates the Cosine Similarity if useCosineSimilarity is true
        for (Map.Entry<String, Vector> entry : vektors.entrySet()) {
            String url = entry.getKey();
            Vector otherVector = entry.getValue();

            double cosineSimilarity = Cosine.cosineSimilarity(vektor, otherVector);
            cosineSim.put(url, cosineSimilarity);
        }

        // Combine PageRank and Cosine Similarity Scores.
        for (String url : cosineSim.keySet()) {
            double cosineScore = cosineSim.getOrDefault(url, 0.0);
            double pageRankScore = pageRanks.getOrDefault(url, 0.0);

            double factor = 0.75;
            double combinedScore = factor * pageRankScore + (1 - factor) * cosineScore;
            results.put(url, combinedScore);
        }

        // Sorts the results in descending order by their scores.
        List<Map.Entry<String, Double>> sortedResults = new ArrayList<>(results.entrySet());
        sortedResults.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        return sortedResults;
    } 
}