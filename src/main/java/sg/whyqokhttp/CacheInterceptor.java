package sg.whyqokhttp;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {


        Response response = chain.proceed(chain.request());

        CacheControl cacheControl = new CacheControl.Builder().maxAge(1, TimeUnit.DAYS).build();

        return response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build();
    }
}
