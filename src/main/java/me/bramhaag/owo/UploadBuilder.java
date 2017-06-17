package me.bramhaag.owo;

import lombok.Getter;
import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadBuilder {

    @Getter
    private byte[] data;

    @Getter
    private String fileName;

    @Getter
    private String contentType;

    @Getter
    private Exception exception;

    /**
     * Set file to upload
     * @param file file to upload
     * @return instance of builder
     */
    public UploadBuilder setFile(@NonNull File file) {
        return setFile(Paths.get(file.getPath()));
    }

    /**
     * Set file to upload
     * @param path path to file
     * @return instance of builder
     */
    public UploadBuilder setFile(@NonNull Path path) {
        try {
            this.data = Files.readAllBytes(path);
            this.fileName = path.getFileName().toString();

            if(contentType == null) {
                String guessedType = URLConnection.guessContentTypeFromName(path.getFileName().toString());
                contentType = guessedType == null ? "application/octect-stream" : guessedType;
            }
        } catch (Exception e) {
            if(exception != null) this.exception = e;
        }

        return this;
    }

    /**
     * Set url to get file from to upload
     * @param url url to file as a String
     * @return instance of builder
     */
    public UploadBuilder setUrl(@NonNull String url) {
        try {
            setUrl(new URL(url));
        } catch (MalformedURLException e) {
            if (exception != null) this.exception = e;
        }

        return this;
    }


    /**
     * Set url to get file from to upload
     * @param url url to file
     * @return instance of builder
     */
    public UploadBuilder setUrl(@NonNull URL url) {
        setUrl(url, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");

        return this;
    }

    /**
     * Set url to get file from to upload with an user agent
     * @param url url to file
     * @param userAgent user agent used to get file
     * @return instance of builder
     */
    public UploadBuilder setUrl(@NonNull URL url, @NonNull String userAgent) {
        try {
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            try (InputStream stream = connection.getInputStream()) {
                byte[] buffer = new byte[8192];
                int pos;

                try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    while ((pos = stream.read(buffer)) != -1) {
                        output.write(buffer, 0, pos);
                    }

                    this.data = output.toByteArray();

                    if(contentType == null) {
                        String guessedType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(this.data));
                        contentType = guessedType == null ? "application/octect-stream" : guessedType;
                    }
                }
            }
        } catch (Exception e) {
            if (exception != null) this.exception = e;
        }

        this.fileName = url.toString().substring(url.toString().lastIndexOf('/') + 1, url.toString().length());

        return this;
    }

    /**
     * Set content type of upload
     * @param contentType content type of upload
     * @return instance of builder
     */
    public UploadBuilder setContentType(@NonNull String contentType) {
        this.contentType = contentType;

        return this;
    }
}
