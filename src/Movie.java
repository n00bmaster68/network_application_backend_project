import java.net.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Movie {
    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?include_adult=false&page=1&query=";
    private static final String CREDIT_URL = "https://api.themoviedb.org/3/movie/abcxyz/credits?count=10";
    private static final String TRAILER_URL = "https://api.themoviedb.org/3/movie/abcxyz/videos";
    private static final String YOUTUBE_VIDEO = "https://www.youtube.com/watch?v=";
    private static final String GENRE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String IMAGE_PREFIX = "https://www.themoviedb.org/t/p/w300_and_h450_bestv2/";
    private static final String API_KEY = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxZDNlODQ0YTEzZmQ1MDgzOGFjNmQ5NTUxYzY4YmI5MyIsInN1YiI6IjY1M2ZhMjAzMTA5Y2QwMDBjOTQ5MzgwMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.h474QYCF9Iiy9OGT3R2PNwY5kMNs80UTVQ5t6eWn3SY";
    private String shortDescription;
    private String actorsAndDirector;
    private String genre;
    private String image;
    private float ratingScore;
    private String trailer;

    // Constructor that returns an object with all fields are null
    public Movie() {
        shortDescription = null;
        actorsAndDirector = null;
        genre = null;
        image = null;
        ratingScore = 0.0f;
        trailer = null;
    }

    // Constructor that takes in all fields as parameters
    public Movie(String shortDescription, String actorsAndDirector, String genre, String image, float ratingScore, String trailer) {
        this.shortDescription = shortDescription;
        this.actorsAndDirector = actorsAndDirector;
        this.genre = genre;
        this.image = image;
        this.ratingScore = ratingScore;
        this.trailer = trailer;
    }

    // Public method to search for a movie by keyword
    public static String searchMovie(String keyword) {
        String result = "[";
        try {
            String url = SEARCH_URL + URLEncoder.encode(keyword, "UTF-8");
            Document doc = Jsoup.connect(url).method(Connection.Method.GET).ignoreContentType(true).header("Authorization", API_KEY).execute().parse();
            JSONObject obj = new JSONObject(doc.text());
            JSONArray movies = obj.getJSONArray("results");
            int numOfResults = movies.length() > 10 ? 10 : movies.length();
            for (int i = 0; i < numOfResults; i++) {
                JSONObject movie = (JSONObject)movies.get(i);
                String movieID = String.valueOf(movie.getInt("id"));
                String castsAndDirectors = searchCastAndDirector(movieID);
                String trailer = searchTrailer(movieID);
                String genres = searchGenres(movieID);
                Movie movieObject = new Movie(movie.getString("overview"), castsAndDirectors, genres, IMAGE_PREFIX + movie.getString("poster_path"), movie.getFloat("vote_average"),  trailer);
                // System.out.println(movieObject.toString());
                result += movieObject.toJsonString() + ",";
                // System.out.println(result);
            }
            result = result.endsWith(",") ? result.substring(0, result.length() - 1) : result;
            result += "]";
            // System.out.println(obj);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return result;
    }

    private static String searchCastAndDirector (String movieID) {
        String result = "";
        try {
            String url = CREDIT_URL.replace("abcxyz", movieID);
            Document doc = Jsoup.connect(url).method(Connection.Method.GET).ignoreContentType(true).header("Authorization", API_KEY).execute().parse();
            JSONObject obj = new JSONObject(doc.text());
            JSONArray crew = obj.getJSONArray("crew");
            JSONArray cast = obj.getJSONArray("cast");
            String directors = "";
            String actors = "";
            for (int i = 0; i < crew.length(); i++) {
                JSONObject object = (JSONObject)crew.get(i);
                if (object.getString("job").equals("Director"))
                    directors += object.getString("name") + ",";
            }
            for (int i = 0; i <= 5; i++) {
                JSONObject object = (JSONObject)cast.get(i);
                actors += object.getString("name") + ",";
            }

            actors = actors.endsWith(",") ? actors.substring(0, actors.length() - 1) : actors;
            directors = directors.endsWith(",") ? directors.substring(0, directors.length() - 1) : directors;
            result = directors + "|" + actors;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private static String searchTrailer (String movieID) {
        String result = "";
        try {
            String url = TRAILER_URL.replace("abcxyz", movieID);
            Document doc = Jsoup.connect(url).method(Connection.Method.GET).ignoreContentType(true).header("Authorization", API_KEY).execute().parse();
            JSONObject obj = new JSONObject(doc.text());
            JSONArray videos = obj.getJSONArray("results");
            for (int i = 0; i < videos.length(); i++) {
                JSONObject video = (JSONObject)videos.get(i);
                if (video.getString("type").equals("Trailer")) {
                    result = video.getString("key");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result != null ? YOUTUBE_VIDEO + result : null;
    }

    private static String searchGenres (String movieID) {
        String result = "";
        try {
            String url = GENRE_URL + movieID;
            Document doc = Jsoup.connect(url).method(Connection.Method.GET).ignoreContentType(true).header("Authorization", API_KEY).execute().parse();
            JSONObject obj = new JSONObject(doc.text());
            JSONArray genres = obj.getJSONArray("genres");
            // System.out.println(genres);
            for (int i = 0; i < genres.length(); i++) {
                JSONObject genre = (JSONObject)genres.get(i);
                result += genre.getString("name") + ",";
            }
            result = result.endsWith(",") ? result.substring(0, result.length() - 1) : result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("shortDescription", this.shortDescription);
        jsonObject.put("actorsAndDirector", this.actorsAndDirector);
        jsonObject.put("genre", this.genre);
        jsonObject.put("image", this.image);
        jsonObject.put("ratingScore", this.ratingScore);
        jsonObject.put("trailer", this.trailer);

        return jsonObject.toString();
    }

    public String toString() {
        return "Movie{" +
                "shortDescription='" + shortDescription + '\'' +
                ", actorsAndDirector='" + actorsAndDirector + '\'' +
                ", genre='" + genre + '\'' +
                ", image='" + image + '\'' +
                ", ratingScore=" + ratingScore +
                ", trailer='" + trailer + '\'' +
                '}';
    }

    public static void main(String args[]) {
        Movie object = new Movie();
        System.out.println(object.searchMovie("Iron man"));
    }
}