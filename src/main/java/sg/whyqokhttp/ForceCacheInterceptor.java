package sg.whyqokhttp;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ForceCacheInterceptor implements Interceptor {

    Context mContext;
    ForceCacheInterceptor(Context mContext)
    {

        this.mContext=mContext;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {


        Request.Builder builder=chain.request().newBuilder();
        if(!ConnectionDetector.isConnectingToInternet(mContext))
        {
            builder.cacheControl(CacheControl.FORCE_CACHE);
        }
        return chain.proceed(builder.build());

    }
}
