/*
 * Copyright 2017 Bram "bramhaag" Hagens
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.bramhaag.owo;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface OwOService {

    /**
     * Upload a file to the {@code /upload/pomf} endpoint
     *
     * @param file File to upload
     * @return {@link Call} of type {@link OwOFile}
     */
    @Multipart
    @POST("upload/pomf")
    Call<OwOFile> upload(@Part MultipartBody.Part file);

    /**
     * Shorten link using the {@code /shorten/polr} endpoint
     *
     * @param url URL to shorten
     * @param resultUrl shorten url used
     * @return {@link Call} of type {@link String}
     */
    @GET("shorten/polr?action=shorten")
    Call<String> shorten(@Query("url") String url, @Query("resultUrl") String resultUrl);
}
