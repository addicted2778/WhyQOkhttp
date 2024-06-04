package sg.whyqokhttp;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class FeedInterceptor {

    private final static String TAG = FeedInterceptor.class.getSimpleName();

    /**
     * Validate cache, return stream. Return cache if no network.
     * @param context
     * @return
     */
    public static Interceptor getOnlineInterceptor(final Context context){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                String headers = response.header("Cache-Control");
                if(ConnectionDetector.isConnectingToInternet(context) && (headers == null
                        || headers.contains("no-store")
                        || headers.contains("must-revalidate")
                        || headers.contains("no-cache")
                        || headers.contains("max-age=0"))) {

                    Logs.e(TAG, "Returning fresh response");
                    return response.newBuilder()
                            .header("Cache-Control", "public, max-age=600")
                            .build();
                } else{
                    Logs.e(TAG, "Returning old response");
                    return response;
                }
            }
        };

        return interceptor;
    }

    /**
     * Get me cache.
     * @param context
     * @return
     */
    public static Interceptor getOfflineInterceptor(final Context context){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if(!ConnectionDetector.isConnectingToInternet(context)){
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached")
                            .build();
                }

                return chain.proceed(request);
            }
        };

        return interceptor;
    }

}