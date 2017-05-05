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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;

public class OwO {

    @NonNull
    private OwOService service;

    /**
     * @param key OwO API key
     *
     * @throws NullPointerException when {@code key} is null
     */
    public OwO(@NonNull final String key) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder().addQueryParameter("key", key).build();
            request = request.newBuilder().url(url).build();
            return chain.proceed(request);
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.awau.moe/")
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().registerTypeAdapter(OwOFile.class, new FileDeserializer()).create()))
                .build();

        this.service = retrofit.create(OwOService.class);
    }

    /**
     * Upload a file
     *
     * @param file File to upload
     * @return {@link OwOAction} of type {@link OwOFile}
     *
     * @throws NullPointerException when {@code file} is null
     */
    public OwOAction<OwOFile> upload(@NonNull File file) {
        RequestBody filePart;
        try {
            filePart = RequestBody.create(MediaType.parse(Files.probeContentType(file.toPath())), file);
        } catch (IOException e) {
            return new OwOAction<>(e);
        }

        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files[]", file.getName(), filePart);
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
