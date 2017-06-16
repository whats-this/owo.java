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

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class OwOFile {

    /**
     * Hash of file
     */
    @Getter private String hash;

    /**
     * Original name of uploaded file
     */
    @Getter private String name;

    /**
     * Key of URL which can be found at the end of the URL
     * For example: filename.extension
     */
    @Getter private String url;

    /**
     * Get full URL
     * For example: https://owo.whats-th.is/filename.extension
     */
    @Getter private String fullUrl;

    /**
     * Size of file
     */
    @Getter private long size;

    /**
     * Set fullUrl
     * @param uploadUrl URL to display before key
     */
    OwOFile setFullUrl(String uploadUrl) {
        uploadUrl = uploadUrl.endsWith("/") ? uploadUrl : uploadUrl + "/";
        fullUrl = uploadUrl + url;

        return this;
    }
}
