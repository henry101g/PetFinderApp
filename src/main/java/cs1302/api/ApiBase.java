package cs1302.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Api class.
 */

public class ApiBase {

    public static final String CATBREEDURL = "https://api.thecatapi.com/v1/breeds";
    public static final String DOGBREEDURL = "https://api.thedogapi.com/v1/breeds";
    public static final String CATIMAGEURL = "https://api.thecatapi.com/v1/images/search";
    public static final String DOGIMAGEURL = "https://api.thedogapi.com/v1/images/search";
    public static String CatAPIKey;
    public static String DogAPIKey;
    public static final String DOGIMAGE =
        "https://static.parade.com/wp-content/uploads/2021/02/cutest-dog-breeds.jpg";
    public static final String CATIMAGE =
        "https://i.pinimg.com/originals/f9/3c/65/f93c6564fc3e8a1c7e7c73c10ffc45d7.jpg";

    /**
     * Function init initializes the variables.
     */

    public static void init() {

        String configPath = "resources/config.properties";

        // the following try-statement is called a try-with-resources statement
        try (FileInputStream configFileStream = new FileInputStream(configPath)) {
            Properties config = new Properties();
            config.load(configFileStream);
            CatAPIKey = config.getProperty("cat.apiKey");
            DogAPIKey = config.getProperty("dog.apiKey");
            //System.out.printf("dogAPI = %s\n", DogAPIKey);
            //System.out.printf("catAPI = %s\n", CatAPIKey);
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try

        breedArray = new ArrayList<Breed>();
        imageArray = new ArrayList<PetImage>();

    }
    /**
     * Function resets arrays.
     */

    public static void reset() {
        breedArray = new ArrayList<Breed>();
        imageArray = new ArrayList<PetImage>();

    }

    /**
     * Class represents image details.
     */

    public static class ImageDetails {
        public String height;
        public String id;
        public String url;
        public String width;

        /**
         * Function toString.
         * @return returns url.
         */

        public String toString() {
            return url;
        }
    }

    /**
     * Represents an Open Library Search API document.
     */
    public static class Breed {
        public String name;
        public String id;
        public ImageDetails image;
    } // Breed

    /**
     * Class PetImage.
     */

    public static class PetImage {
        public String url;

    }


    public static ArrayList<Breed> breedArray;
    public static ArrayList<PetImage> imageArray;

    /**
     * Represents an Open Library Search API result.
     */
    public static class BreedResult {
        // public int numFound;
        public Breed[] breeds;
    } // BreedResult

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    /**
     * Main funtction.
     * @param args is aarray.
     */

    public static void main(String[] args) {
        ApiBase.init();
        ApiBase.getBreeds(CATBREEDURL, CatAPIKey)
            .ifPresent(response -> fillBreeds(response));
        ApiBase.getImages(CATIMAGEURL, CatAPIKey, "abys", 60)
            .ifPresent(response -> fillImages(response));
        ApiBase.reset();
        ApiBase.getBreeds(DOGBREEDURL, DogAPIKey)
            .ifPresent(response -> fillBreeds(response));
        ApiBase.getImages(DOGIMAGEURL, DogAPIKey, "5", 60)
            .ifPresent(response -> fillImages(response));
    } // main


    /**
     * An example of some things you can do with a response.
     * @param result the ope library search result
     */
    public static void fillImages(PetImage[] result) {
        // print what we found
        // System.out.printf("numFound = %d\n", result.numFound);
        int count = 0;
        for (PetImage petImage: result) {
            imageArray.add(petImage);
            System.out.println(petImage.url);
            count++;
        } // for
        System.out.println(count);
    } // fillImages


    /**
     * An example of some things you can do with a response.
     * @param result the ope library search result
     */
    public static void fillBreeds(Breed[] result) {
        // print what we found
        // System.out.printf("numFound = %d\n", result.numFound);
        for (Breed breed: result) {
            //if (breed.image != null && !breed.image.equals("")) {
            breedArray.add(breed);
            System.out.println(breed.name + " " + breed.id);
            //System.out.println(breed.image);
            //}
        } // for
    } // fillBreeds


    /**
     * Returns the response body string data from a URI.
     * @param uri location of desired content
     * @return response body string
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the HTTP client's {@code send} method is
     *    interrupted
     */
    public static String fetchString(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build();
        HttpResponse<String> response = HTTP_CLIENT
            .send(request, BodyHandlers.ofString());
        final int statusCode = response.statusCode();
        if (statusCode != 200) {
            throw new IOException("response status code not 200:" + statusCode);
        } // if
        return response.body().trim();
    } // fetchString


    /**
     * Return an {@code Optional} describing the root element of the JSON
     * response for a "search" query.
     * @param baseUrl is a string and the base url.
     * @param APIKey is the api key in a string.
     * @return an {@code Optional} describing the root element of the response
     */

    public static Optional<Breed[]> getBreeds(String baseUrl,
        String APIKey) {
        //public static void getBreeds(String APIKey) {
        System.out.println("Getting Breeds");
        System.out.println("This may take some time to download...");
        try {
            String url =  String.format(
                "%s?x-api-key=%s",
                baseUrl,
                URLEncoder.encode(APIKey, StandardCharsets.UTF_8));
            System.out.println(url);
            String json = ApiBase.fetchString(url);
            System.out.println(json);
            //BreedResult result = null;
            Breed[] result = GSON.fromJson(json, Breed[].class);

            return Optional.<Breed[]>ofNullable(result);
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            return Optional.<Breed[]>empty();
        } // try
    } // getBreeds

    /**
     * Return an {@code Optional} describing the root element of the JSON
     * response for a "search" query.
     * @param baseUrl is a string and the base url.
     * @param APIKey is the api key in a string.
     * @param breedID is the id for the animal in a string.
     * @param limit is an int and the limit on the number of items.
     * @return an {@code Optional} describing the root element of the response
     */

    public static Optional<PetImage[]> getImages(String baseUrl,
        String APIKey, String breedID, int limit) {

        System.out.println("Getting Images");
        System.out.println("This may take some time to download...");
        System.out.println(baseUrl);
        try {
            String url =  String.format(
                "%s?breed_id=%s&limit=%d&x-api-key=%s",
                baseUrl,
                breedID,
                limit,
                URLEncoder.encode(APIKey, StandardCharsets.UTF_8));
            System.out.println(url);
            String json = ApiBase.fetchString(url);
            System.out.println("Boris " + json);
            //BreedResult result = null;
            PetImage[] result = GSON.fromJson(json, PetImage[].class);
            return Optional.<PetImage[]>ofNullable(result);
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            return Optional.<PetImage[]>empty();
        } // try
    } // getImages


    /**
     * This function gets the breed name.
     * @return returns the ID as a String.
     * @param breed is the breed name.
     */
    public static String getID(String breed) {
        for (int i = 0; i < breedArray.size(); i++) {
            if (breedArray.get(i).name.equals(breed)) {
                return breedArray.get(i).id;
            }
        }
        return null;
    }

} // ApiBase
