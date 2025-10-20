package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://dog.ceo/api/";
    private static final String STATUS = "status";
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        final Request request = new Request.Builder().url(String.format("%sbreed/%s/list", API_URL, breed))
                .build();
        try (Response response = client.newCall(request).execute()) {
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getString(STATUS).equals(SUCCESS)) {
                final JSONArray subBreeds = responseBody.getJSONArray(MESSAGE);
                ArrayList<String> subBreedsArray = new ArrayList<>();
                for (int i = 0; i < subBreeds.length(); i++) {
                    subBreedsArray.add(subBreeds.get(i).toString());
                }
                return subBreedsArray;
            }
            else {
                throw new BreedNotFoundException(breed);
            }

        } catch (IOException | JSONException event) {
            throw new BreedNotFoundException(breed);
        }
    }
}