package me.bramhaag.owo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.bramhaag.owo.util.Consumer;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

@SuppressWarnings({"WeakerAccess", "unused"})
public class OwO {

    @NotNull private OwOService service;

    @NotNull private static final String USER_AGENT = String.format("WhatsThisClient (%s, %s)", "https://github.com/bramhaag/owo.java/", OwO.class.getPackage().getImplementationVersion());

    @NotNull private static final String DEFAULT_ENDPOINT    = "https://api.awau.moe/";
    @NotNull private static final String DEFAULT_UPLOAD_URL  = "https://owo.whats-th.is";
    @NotNull private static final String DEFAULT_SHORTEN_URL = "https://awau.moe";

    @NotNull private static final String DEFAULT_CONTENT_TYPE = "application/octect-stream";

    @Nullable private String shortenUrl;

    /**
     * @param key OwO API key
     *
     * @throws NullPointerException if {@code key} is null
     */
    public OwO(@NotNull final String key) {
        this.shortenUrl = DEFAULT_SHORTEN_URL;
        this.service = createService(key, DEFAULT_ENDPOINT, DEFAULT_UPLOAD_URL);
    }

    /**
     * @param key OwO API key
     * @param endpoint Endpoint URL, defaults to {@link OwO#DEFAULT_ENDPOINT} when null
     * @param uploadUrl Upload URL, defaults to {@link OwO#DEFAULT_UPLOAD_URL} when null
     * @param shortenUrl Shorten URL, defaults to {@link OwO#DEFAULT_SHORTEN_URL} when null
     *
     * @throws NullPointerException if {@code key} is null
     */
    private OwO(@NotNull final String key, @Nullable String endpoint, @Nullable String uploadUrl, @Nullable String shortenUrl) {
        System.out.println(USER_AGENT);
        this.shortenUrl = shortenUrl;
        this.service = createService(key, endpoint, uploadUrl);
    }

    /**
     * Upload a file with a guessed content type
     * @param file File to upload
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code file} is null
     */
    public OwOAction<OwOFile> upload(@NotNull File file) {
        return upload(file, null);
    }

    /**
     * Upload a file with specified content type
     * @param file File to upload
     * @param contentType content type of {@code file}
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code file} is null
     */
    public OwOAction<OwOFile> upload(@NotNull File file, @Nullable String contentType) {
        if(contentType == null) {
            String guessedType = URLConnection.guessContentTypeFromName(file.getName());
            contentType = guessedType == null ? DEFAULT_CONTENT_TYPE : guessedType;
        }

        try {
            return upload(Files.readAllBytes(file.toPath()), file.getName(), contentType);
        } catch (IOException e) {
            return new OwOAction<>(e);
        }
    }

    /**
     * Upload file from an URL with {@link OwO#USER_AGENT} as user agent
     * and with a guessed content type
     * @param url URL of file
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code url} is null
     */
    public OwOAction<OwOFile> upload(@NotNull URL url) {
        return upload(url, null, USER_AGENT);
    }

    /**
     * Upload file from an URL with a specified user agent and content type
     * @param url URL of file
     * @param contentType content type of file from {@code url}
     * @param userAgent user agent used to retrieve file
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code url} is null
     */
    public OwOAction<OwOFile> upload(@NotNull URL url, @Nullable String contentType, @Nullable String userAgent) {
        URLConnection connection;

        try {
            connection = url.openConnection();
        } catch (IOException e) {
            return new OwOAction<>(e);
        }

        connection.setRequestProperty("User-Agent", userAgent == null ? USER_AGENT : userAgent);

        try (InputStream stream = connection.getInputStream()) {
            byte[] buffer = new byte[8192];
            int pos;

            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                while ((pos = stream.read(buffer)) != -1) {
                    output.write(buffer, 0, pos);
                }

                byte[] data = output.toByteArray();

                if(contentType == null) {
                    String guessedType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(data));
                    contentType = guessedType == null ? DEFAULT_CONTENT_TYPE : guessedType;
                }

                return upload(data, null, contentType);
            }
        } catch (IOException e) {
            return new OwOAction<>(e);
        }
    }

    /**
     * Upload a string of text using {@code application/text} as content type
     * @param data String to upload
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code data} is null
     */
    public OwOAction<OwOFile> upload(@NotNull String data) {
        return upload(data, null);
    }

    /**
     * Upload a string of text using a specified content type
     * @param data String to upload
     * @param contentType content type of {@code data}
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code data} is null
     */
    public OwOAction<OwOFile> upload(@NotNull String data, @Nullable String contentType) {
        if(contentType == null) {
            contentType = "application/text";
        }

        return upload(data.getBytes(), null, contentType);
    }

    /**
     * Upload a {@code byte[]} without a filename and {@link OwO#DEFAULT_CONTENT_TYPE} as content type
     * @param data data to upload
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code data} is null
     */
    public OwOAction<OwOFile> upload(@NotNull byte[] data) {
        return upload(data, null);
    }

    /**
     * Upload a {@code byte[]} with a specified filename and {@link OwO#DEFAULT_CONTENT_TYPE} as content type
     * @param data data to upload
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code data} is null
     */
    public OwOAction<OwOFile> upload(@NotNull byte[] data, @Nullable String fileName) {
        return upload(data, fileName, null);
    }

    /**
     * Upload a {@code byte[]} with a specified filename and content type
     * @param data data to upload
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException if {@code data} is null
     */
    public OwOAction<OwOFile> upload(@NotNull byte[] data, @Nullable String fileName, @Nullable String contentType) {
        RequestBody filePart = RequestBody.create(MediaType.parse(contentType == null ? DEFAULT_CONTENT_TYPE : contentType), data);
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files[]", fileName, filePart);

        return new OwOAction<>(service.upload(uploadFile));
    }

    /**
     * Shorten a URL
     * @param url URL to be shortened
     * @return {@link OwOAction} of type {@link String}, which can be used with {@link OwOAction#execute(Consumer)} or {@link OwOAction#executeSync()}
     */
    public OwOAction<String> shorten(@NotNull String url) {
        return new OwOAction<>(service.shorten(url, shortenUrl == null ? DEFAULT_SHORTEN_URL : shortenUrl));
    }

    /**
     * Create service
     * @param key OwO API key
     * @param endpoint Endpoint URL, defaults to {@link OwO#DEFAULT_ENDPOINT} when null
     * @param uploadUrl Upload URL, defaults to {@link OwO#DEFAULT_UPLOAD_URL} when null
     * @return service
     */
    private static OwOService createService(@NotNull final String key, @Nullable String endpoint, @Nullable final String uploadUrl) {
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

        @Nullable String key;
        @Nullable String endpoint;

        @Nullable String uploadUrl;
        @Nullable String shortenUrl;

        /**
         * Set OwO API key
         * @param key OwO API key
         * @return instance of builder
         */
        public Builder setKey(@NotNull String key) {
            this.key = key;

            return this;
        }

        /**
         * Set API endpoint
         * @param endpoint endpoint URL
         * @return instance of builder
         */
        public Builder setEndpoint(@NotNull String endpoint) {
            this.endpoint = endpoint;

            return this;
        }

        /**
         * Set upload url
         * @param uploadUrl upload url
         * @return instance of builder
         */
        public Builder setUploadUrl(@NotNull String uploadUrl) {
            this.uploadUrl = uploadUrl;

            return this;
        }

        /**
         * Set shorten url
         * @param shortenUrl shorten url
         * @return instance of builder
         */
        public Builder setShortenUrl(@NotNull String shortenUrl) {
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
