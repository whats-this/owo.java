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

import lombok.NonNull;
import me.bramhaag.owo.util.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class OwOAction<T> {

    private Call<T> call;
    private Throwable throwable;

    public OwOAction(Call<T> call) {
        this.call = call;
    }

    public OwOAction(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Execute {@link OwOAction} of type {@link T} in an asynchronous manner
     *
     * @param response Callback containing the response of type {@link T}
     *
     * @throws NullPointerException when {@code response} is null
     */
    public void execute(@NonNull Consumer<T> response) {
        execute(response, null);
    }

    /**
     * Execute {@link OwOAction} of type {@link T} in an asynchronous manner with error handling
     *
     * @param response Callback containing the response of type {@link T}
     * @param throwable Callback which is called when an exception is thrown
     *
     * @throws NullPointerException when {@code response} is null
     */
    public void execute(@NonNull final Consumer<T> response, final Consumer<Throwable> throwable) {
        Callback<T> callback = new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> result) {
                if(result.isSuccessful()) {
                    response.accept(result.body());
                } else {
                    this.onFailure(call, new HttpException(result));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if(throwable != null) {
                    throwable.accept(t);
                }
            }
        };

        if(this.throwable != null) {
            callback.onFailure(null, this.throwable);
            return;
        }

        call.enqueue(callback);
    }

    /**
     * Execute {@link OwOAction} of type {@link T} on the current thread
     * @return response of type {@link T}
     * @throws Throwable thrown when an error occurred
     */
    public T executeSync() throws Throwable {
        if(this.throwable != null) {
            throw throwable;
        }

        Response<T> response = call.execute();
        if(response.isSuccessful()) {
            return response.body();
        } else {
            throw new HttpException(response);
        }
    }
}
