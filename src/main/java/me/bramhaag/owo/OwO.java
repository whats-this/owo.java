package me.bramhaag.owo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.NonNull;
import me.bramhaag.owo.util.Consumer;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.lang.reflect.Type;

public class OwO {

    @NonNull
    private OwOService service;

    @NonNull
    private static final String USER_AGENT = String.format("WhatsThisClient (%s, %s)", "https://github.com/bramhaag/owo.java/", OwO.class.getPackage().getImplementationVersion());

    private static final String DEFAULT_ENDPOINT    = "https://api.awau.moe/";
    private static final String DEFAULT_UPLOAD_URL  = "https://owo.whats-th.is";
    private static final String DEFAULT_SHORTEN_URL = "https://awau.moe";

    @NonNull private String shortenUrl;

    /**
     * @param key OwO API key
     * @param endpoint Endpoint URL, defaults to {@link OwO#DEFAULT_ENDPOINT} when null
     * @param uploadUrl Upload URL, defaults to {@link OwO#DEFAULT_UPLOAD_URL} when null
     * @param shortenUrl Shorten URL, defaults to {@link OwO#DEFAULT_SHORTEN_URL} when null
     *
     * @throws NullPointerException when {@code key} is null
     */
    private OwO(@NonNull final String key, String endpoint, final String uploadUrl, String shortenUrl) {
        System.out.println(USER_AGENT);
        this.shortenUrl = shortenUrl;
        this.service = createService(key, endpoint, uploadUrl);
    }

    /**
     * Upload a file
     * @param builder containing details for the file upload
     * @return {@link OwOAction} of type {@link OwOFile}, which can be used with {@link OwOAction#execute(Consumer)} or {@link OwOAction#executeSync()}
     */
    public OwOAction<OwOFile> upload(@NonNull UploadBuilder builder) {
        if(builder.getException() != null) {
            return new OwOAction<>(builder.getException());
        }

        RequestBody filePart = RequestBody.create(MediaType.parse(builder.getContentType()), builder.getData());

        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files[]", builder.getFileName(), filePart);
        return new OwOAction<>(service.upload(uploadFile));
    }

    /**
     * Shorten a URL
     * @param url URL to be shortened
     * @return {@link OwOAction} of type {@link String}, which can be used with {@link OwOAction#execute(Consumer)} or {@link OwOAction#executeSync()}
     */
    public OwOAction<String> shorten(@NonNull String url) {
        return new OwOAction<>(service.shorten(url, shortenUrl));
    }

    /**
     * Upload a file
     * @param key OwO API key
     * @param builder containing details for the file upload
     * @return {@link OwOAction} of type {@link OwOFile}, which can be used with {@link OwOAction#execute(Consumer)} or {@link OwOAction#executeSync()}
     */
    public static OwOAction<OwOFile> upload(String key, @NonNull UploadBuilder builder) {
        if(builder.getException() != null) {
            return new OwOAction<>(builder.getException());
        }

        RequestBody filePart = RequestBody.create(MediaType.parse(builder.getContentType()), builder.getData());

        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files[]", builder.getFileName(), filePart);
        return new OwOAction<>(createService(key, null, null).upload(uploadFile));
    }

    /**
     * Shorten a URL
     * @param key OwO API key
     * @param url URL to be shortened
     * @return {@link OwOAction} of type {@link String}, which can be used with {@link OwOAction#execute(Consumer)} or {@link OwOAction#executeSync()}
     */
    public static OwOAction<String> shorten(String key, @NonNull String url) {
        return new OwOAction<>(createService(key, null, null).shorten(url, DEFAULT_SHORTEN_URL));
    }

    /**
     * Create service
     * @param key OwO API key
     * @param endpoint Endpoint URL, defaults to {@link OwO#DEFAULT_ENDPOINT} when null
     * @param uploadUrl Upload URL, defaults to {@link OwO#DEFAULT_UPLOAD_URL} when null
     * @return service
     */
    private static OwOService createService(@NonNull final String key, String endpoint, final String uploadUrl) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("key", key).build();
                return chain.proceed(request.newBuilder().header("User-Agent", USER_AGENT).url(url).build());
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint == null ? DEFAULT_ENDPOINT : endpoint)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().registerTypeAdapter(OwOFile.class, new JsonDeserializer<OwOFile>() {
                    @Override
                    public OwOFile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                        return new Gson().fromJson(json.getAsJsonObject().get("files").getAsJsonArray().get(0), OwOFile.class).setFullUrl(uploadUrl == null ? DEFAULT_UPLOAD_URL : uploadUrl);
                    }}).create()))
                .build();

        return retrofit.create(OwOService.class);
    }

    public static class Builder {

        String key;
        String endpoint;

        String uploadUrl;
        String shortenUrl;

        /**
         * Set OwO API key
         * @param key OwO API key
         * @return instance of builder
         */
        public Builder setKey(@NonNull String key) {
            this.key = key;

            return this;
        }

        /**
         * Set API endpoint
         * @param endpoint endpoint URL
         * @return instance of builder
         */
        public Builder setEndpoint(@NonNull String endpoint) {
            this.endpoint = endpoint;

            return this;
        }

        /**
         * Set upload url
         * @param uploadUrl upload url
         * @return instance of builder
         */
        public Builder setUploadUrl(@NonNull String uploadUrl) {
            this.uploadUrl = uploadUrl;

            return this;
        }

        /**
         * Set shorten url
         * @param shortenUrl shorten url
         * @return instance of builder
         */
        public Builder setShortenUrl(@NonNull String shortenUrl) {
            this.shortenUrl = shortenUrl;

            return this;
        }

        /**
         * Build current builder
         * @return OwO class with properties from current builder
         *
         * @throws IllegalArgumentException when key is not set or null
         */
        public OwO build() {
            if(key == null) {
                throw new IllegalArgumentException("key cannot be null!");
            }

            return new OwO(key, endpoint, uploadUrl, shortenUrl);
        }
    }
}
