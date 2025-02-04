package de.fernunihagen.dbis.anguillasearch;

import java.io.IOException;
import java.util.*;
import com.google.gson.JsonObject;

/**  
 * The class "PageRank" implements the PageRank algorithm which shows the importance of websites
 * in their intranet, based on their interlinking structure. It uses crawling to fetch the websites and iteratively calculates the 
 * PageRank score for each page.
 */

public class PageRank {
  
    // Map to store the PageRank values for each url 
    Map<String, Double> pageRankValues = new HashMap<>();
    JsonObject jsonFilePath;
    private static final double EPSILON = 0.0001;
    private static final double DAMPING_FACTOR = 0.85;
    private final Crawler crawler;

    /** 
     * A public constructor, used for initializing the JSON-Object and create an new instance of a Crawler object.
     */
 
    public PageRank(JsonObject jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
        this.crawler = new Crawler();
    }

    /** 
     * Algorithm to calculate the PageRank scores for all fetched pages. It uses the 
     * damping factor and iterates until the PageRank values converge.
     * 
     * @throws IOException  if there is an error during the crawling.
     */

    public void calculate() throws IOException {
        crawler.crawl(jsonFilePath);
        int crawledPages = crawler.getCrawledPages().size();

        if (crawler.getCrawledPages().isEmpty()) {
            return;
        }

        // Initialize the PageRank values for all pages.
        double initialRank = 1.0 / crawledPages;

        for (PageData page : crawler.getCrawledPages()) {
            pageRankValues.put(page.getURL(), initialRank);
            page.setPageRank(initialRank);
        }  

        boolean converged = false; 

        // Iterates until the difference between the new and the old PageRank score converge. 
        while(!converged) {
            double rankDifferenz = 0.0;
            Map<String, Double> pageRankAktualisiert = new HashMap<>();

            for (PageData page : crawler.getCrawledPages()) {
                String url = page.getURL();
                double newRank = 0.0;

                // Calculate the contribution of each linking page.
                for (PageData otherPage : crawler.getCrawledPages()) {
                    if (otherPage.getLinks().contains(url)) {
                        String linkingURL = otherPage.getURL();
                        double linkingRank = pageRankValues.get(linkingURL);
                        int outgoingLinks = otherPage.getLinks().size();

                        newRank += linkingRank / outgoingLinks;
                    }
                }
               
                newRank = (DAMPING_FACTOR * newRank) + ((1 - DAMPING_FACTOR) / crawledPages);
                
                pageRankAktualisiert.put(url, newRank);
                rankDifferenz += Math.abs(newRank - pageRankValues.get(url));
            }

            // Update the PageRank values and heck for convergence
            pageRankValues = pageRankAktualisiert;
            converged = rankDifferenz < EPSILON;
        }
    }
} 