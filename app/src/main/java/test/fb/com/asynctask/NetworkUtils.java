package test.fb.com.asynctask;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";

    // Use HttpURLConnection to @param query books with googleapis REST APIs
    static String getBookInfo(String query) {
        HttpURLConnection urlConn = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            // Common practice to separate query parameters into constants and combine them using an Uri.Builder.
            Uri uri = Uri.parse(BOOK_BASE_URL).buildUpon()
                        // Search parameters follow the '?' in the URL
                        .appendQueryParameter(QUERY_PARAM, query)
                        .appendQueryParameter(MAX_RESULTS, "10")
                        .appendQueryParameter(PRINT_TYPE, "books")
                        .build();
            // A URI is a string that identifies a resource. A URL is a certain type of URI that identifies a web resource.
            URL requestURL = new URL(uri.toString());
            // Open connection and make request
            urlConn = (HttpURLConnection) requestURL.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.connect();

            InputStream inputStream = urlConn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            if (builder.length() == 0) return null;
            bookJSONString = builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null) urlConn.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(LOG_TAG, bookJSONString);
        return bookJSONString;
    }
}
