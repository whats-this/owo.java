package me.bramhaag.owo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.NonNull;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.tika.Tika;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.io.IOUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;

public class OwO {

    @NonNull
    private OwOService service;

    @NonNull
    private Tika tika;

    /**
     * @param key OwO API key
     *
     * @throws NullPointerException when {@code key} is null
     */
    public OwO(@NonNull final String key) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("key", key).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.awau.moe/")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().registerTypeAdapter(OwOFile.class, new FileDeserializer()).create()))
                .build();

        this.service = retrofit.create(OwOService.class);
        this.tika = new Tika();
    }

    /**
     * Upload a file
     *
     * @param file File to upload
     *
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException when {@code file} is null
     */
    public OwOAction<OwOFile> upload(@NonNull File file) {
        return upload(file, null);
    }

    /**
     * Upload a file with a specified contentType
     *
     * @param file File to upload
     * @param contentType content type of file
     *
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException when {@code file} is null
     */
    public OwOAction<OwOFile> upload(@NonNull File file, String contentType) {
        RequestBody filePart;
        try {
            if(contentType == null) {
                contentType = tika.detect(file);
            }

            filePart = RequestBody.create(MediaType.parse(contentType), file);
        } catch (IOException e) {
            return new OwOAction<>(e);
        }

        return upload(filePart, file.getName());
    }

    /**
     * Upload a file from an url
     *
     * @param url File's url
     *
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException when {@code file} is null
     */
    public OwOAction<OwOFile> upload(@NonNull URL url) {
        return upload(url, null);
    }

    /**
     * Upload a file from an url
     *
     * @param url File's url
     * @param contentType content type of file
     *
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException when {@code url} is null
     */
    public OwOAction<OwOFile> upload(@NonNull URL url, String contentType) {
        RequestBody filePart;

        try {
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");

            byte[] data = IOUtils.toByteArray(connection.getInputStream());

            if(contentType == null) {
                contentType = tika.detect(data);
            }

            filePart = RequestBody.create(MediaType.parse(contentType), data);
        } catch (IOException e) {
            return new OwOAction<>(e);
        }

        return upload(filePart, FilenameUtils.getName(url.getPath()));
    }

    /**
     * Upload a file from a {@code byte[]}
     *
     * @param data data of file
     * @param fileName name of file
     *
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException when {@code data} or {@code fileName} is null
     */
    public OwOAction<OwOFile> upload(@NonNull byte[] data, @NonNull String fileName) {
        return upload(data, fileName, null);
    }

    /**
     * Upload a file from a {@code byte[]}
     *
     * @param data data of file
     * @param fileName name of file
     * @param contentType content type of file
     *
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException when {@code data} or {@code fileName} is null
     */
    public OwOAction<OwOFile> upload(@NonNull byte[] data, @NonNull String fileName, String contentType) {
        if(contentType == null) {
            contentType = tika.detect(data);
        }

        RequestBody filePart = RequestBody.create(MediaType.parse(contentType), data);
        return upload(filePart, fileName);
    }

    /**
     * Upload file
     *
     * @param requestBody file's data to upload
     * @param fileName file's name
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException when {@code requestBody} or {@code fileName} is null
     */
    private OwOAction<OwOFile> upload(@NonNull RequestBody requestBody, @NonNull String fileName) {
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files[]", fileName, requestBody);
        return new OwOAction<>(service.upload(uploadFile));
    }

    /**
     * Shorten an URL
     *
     * @param url URL to shorten
     * @return {@link OwOAction} of type {@link String}
     */
    public OwOAction<String> shorten(@NonNull String url) {
        return new OwOAction<>(service.shorten(url));
    }

    /**
     * Custom Gson deserializer to convert {@code files[]} to an {@link OwOFile}
     */
    private class FileDeserializer implements JsonDeserializer<OwOFile> {

        @Override
        public OwOFile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new Gson().fromJson(json.getAsJsonObject().get("files").getAsJsonArray().get(0), OwOFile.class);
        }
    }
}
