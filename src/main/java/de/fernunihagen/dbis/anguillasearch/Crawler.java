package de.fernunihagen.dbis.anguillasearch;

import org.jsoup.*; 
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.util.*;
import java.io.IOException;
import com.google.gson.*;
 
/** 
 * The class "Crawler" fetches the websites from a given intranet. 
 * It builds a Forward Index, Reverse Index and calculates the TF-IDF values for the tokenized content of the crawled pages. 
 */

public class Crawler { 

    private Set<String> visitedURLs = new HashSet<>();
    // Queue of urls to be processed 
    private Queue<String> queueURL = new LinkedList<>();
    private Set<String> allLinks = new HashSet<>();
    private List<PageData> crawledPages = new ArrayList<>();
    private ForwardIndex forwardIndex = new ForwardIndex();
    private ReverseIndex reverseIndex = new ReverseIndex();
 
    int totalLinkCount = 0;

    /** 
     * Extracts the seed-urls from the provided JSON-file and initializes the queue of urls to be crawled.
     * 
     * @param jsonFile  the JSON-Object containing the seed urls.
     */

    public void getSeeds(JsonObject jsonFile) {
        JsonArray jsonSeedUrls = jsonFile.getAsJsonArray("Seed-URLs");

        for (int a = 0; a < jsonSeedUrls.size(); a++) {
            String seedUrl = jsonSeedUrls.get(a).getAsString();
            if (!visitedURLs.contains(seedUrl)) {
                queueURL.add(seedUrl);
                visitedURLs.add(seedUrl);
            }
        }
    }

    /** 
     * Extracts further urls from a given website and adds them to the crawling queue.
     * 
     * @param url  the url of the page to etract the furhter urls from 
     * 
     * @throws IOException  if an error occurs while page fetching 
     */

    public void getFutherUrl (String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        Elements links = doc.select("a[href]");

        for (Element link : links) {
            String otherLink = link.attr("abs:href");
            if (otherLink != null && !otherLink.isEmpty() && !visitedURLs.contains(otherLink)) {
                visitedURLs.add(otherLink);
                queueURL.add(otherLink);
            }
        }
    }

    /** 
     * Counts the number of unique links on a given url 
     * 
     * @param url  the url to fetch the links from 
     * 
     * @return  the total count of unique links on the website 
     * 
     * @throws IOException  if an error occurs while page fetching
     */

     public int getAllLinks(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a");

        int linkCount = 0;
        
        for (Element link : links) {
            String href = link.attr("href"); 
            if (href != null && !href.isEmpty()) {
                allLinks.add(href);
                linkCount++;
            }
        }

        return linkCount; 
    }

    /** 
     * Retrieves all unique links from a given url as a Set
     * 
     * @param url  the url to fetch the links from 
     * 
     * @return  a set of unique links found on the website 
     * 
     * @throws IOException  if an error occurs while page fetching
     */

    public Set<String> getAllLinksFromURL(String url) throws IOException {
        getAllLinks(url);
        return allLinks;
    }

    // This method is necessary for the test-method findCorrectNumberOfLinks() in CrawlerTests.java
    public int getAllLinksFromJSON (JsonObject jsonFilePath) {
        return totalLinkCount;
    }

    /** 
     * Crawls all pages from an intranet starting from the seed-urls and processes their content.
     * 
     * @param jsonFilePath  the JSON-Object conatining the seed-urls
     * 
     * @throws IOException  if an error occurs during the crawling 
     */

    public void crawl (JsonObject jsonFilePath) throws IOException {
        getSeeds(jsonFilePath);

        while (!queueURL.isEmpty()) {
            String url = queueURL.poll();
            getFutherUrl(url);
        }

        List<String> visitedUrlList = new ArrayList<>(visitedURLs);

        for (int i = 0; i < visitedUrlList.size(); i++) {
            String visitedUrl = visitedUrlList.get(i);
            totalLinkCount += getAllLinks(visitedUrl); 
    
            PageData pageData = new PageData();
            Vector vektor = new Vector();
    
            Document doc = Jsoup.connect(visitedUrl).get();
            
            String title = doc.title();
            pageData.setTitle(title);
    
            Element headerElement = doc.selectFirst("h1");
            String header = (headerElement != null) ? headerElement.text() : "";
            pageData.setHeader(header); 

            Elements linkElements = doc.select("a[href]");

            doc.select("p a").remove();
            String content = doc.body().text();
            List<String> tokens = reverseIndex.tokenizeContent(content);
            pageData.setTokens(tokens);

            for (Element linkElement : linkElements) {
                String link = linkElement.attr("abs:href");
                if (link != null && !link.isEmpty()) {
                    pageData.addLink(link);
                } 
            }
            String id = "doc_"+(i+1);

            String url = visitedUrl;

            pageData.setID(id);
            pageData.setURL(url);
    
            crawledPages.add(pageData);
            forwardIndex.createForwardIndex(id, vektor);
        }
        calculateTFIDF();
    }

    public int getVisitedWebsites (JsonObject jsonFilePath) throws IOException { 
        crawl(jsonFilePath);
        return visitedURLs.size();
    }
 
    public List<PageData> getCrawledPages() {
        return crawledPages;
    }

    public ForwardIndex getForwardIndex() {
        return forwardIndex;
    }

    public ReverseIndex getReverseIndex() {
        return reverseIndex;
    }

    /** 
     * Calculates the TF-IDF values for the token in all crawled pages.
     */

    public void calculateTFIDF() {
        Map<String, Integer> documentFrequencies = new HashMap<>();
        int totalDocuments = crawledPages.size();

        // Calculate the document frequencies
        for(PageData page : crawledPages) {
            Set<String> uniqueTokens = new HashSet<>(page.getTokens());
            for (String token : uniqueTokens) {
                documentFrequencies.put(token, documentFrequencies.getOrDefault(token, 0) + 1);                
            }
        } 

        // Calculate the TF-IDF values for each page from all the crawled pages.
        for (PageData page : crawledPages) {
            List<String> tokens = page.getTokens();
            Map<String, Double> tfidfScores = TFIDF.calculate(tokens, totalDocuments, documentFrequencies);
    
            Vector vektor = new Vector();
            for (Map.Entry<String, Double> entry : tfidfScores.entrySet()) {
                String token = entry.getKey();
                double tfidf = entry.getValue();
    
                reverseIndex.addToken(token, page.getURL(), tfidf);
                vektor.addToken(token, tfidf);
            }
    
            forwardIndex.createForwardIndex(page.getID(), vektor);
        }
    }

    /** 
     * Ranks the websites based on their similarity to a query vector and their PageRank. 
     * 
     * @param queryVektor  Vector which represents the query
     * 
     * @return a list of ranked Pagedata-Objects
     */
 
    public List<PageData> rankWebsites(Vector queryVektor) {
        List<PageData> rankedPages = new ArrayList<>();
 
        for (PageData page : crawledPages) {
            double pageRank = page.getPageRank();
            Vector vektor = forwardIndex.getForwardIndex(page.getID());
            double cosineSimilarity = Cosine.cosineSimilarity(vektor, queryVektor);
            double combined = 0.7 * pageRank + 0.3 * cosineSimilarity;

            page.setScore(combined);
            rankedPages.add(page);
        }
        
        rankedPages.sort((p1, p2) -> Double.compare(p2.getScore(), p1.getScore()));
        return rankedPages;
    }
}