package sg.whyqokhttp;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import sg.whyqokhttp.Method;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MultipartRequest {

    private UploadListener listener;
    private int requestCode;
    private MultipartBody.Builder builder;
    private OkHttpClient client;
    private HashMap<String, String> header;
    private HashMap<String, String> queryParam;

    private CustomProgress progressDialog;

    public MultipartRequest(Context caller) {
        this.builder = new MultipartBody.Builder();
        this.client = new OkHttpClient();
        progressDialog = new CustomProgress(caller);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void setListener(UploadListener listener) {
        this.listener = listener;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }


    public void addImage(String name, String filePath, String fileName) {
        MediaType type = MediaType.parse("image/jpeg");
        this.builder.addFormDataPart(name, fileName, RequestBody.create(type, new File(filePath)));
    }

    public void addImageURI(String name, String filePath, String fileName) {
        MediaType type = MediaType.parse("multipart/form-data");
        this.builder.addFormDataPart(name, fileName, RequestBody.create(type, new File(filePath)));
    }

    public void addVideo(String name, String filePath, String fileName) {
        MediaType type = MediaType.parse("video/mp4");
        this.builder.addFormDataPart(name, fileName, RequestBody.create(type, new File(filePath)));
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
        Logs.e("REQUEST_Header", header.toString());
    }

    public void setRequestParam(HashMap<String, String> queryParam) {
        Logs.e("REQUEST_PARAM", queryParam.toString());
        this.queryParam = queryParam;
    }

    public Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public void execute(String url) {

        Logs.e("URL", url);
        progressDialog.show();
        Request request = null;
        try {
            for (Map.Entry<String, String> entry : queryParam.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            RequestBody requestBody = builder
                    .setType(MultipartBody.FORM)
                    .build();
            request = new Request
                    .Builder()
                    .headers(getHeaders())
                    .url(url)
                    .post(requestBody)
                    .build();
            Logs.e("Reeq_ContentLength", request.body().contentLength() + "");

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    progressDialog.dismiss();
                    if (listener != null)
                        listener.onFailed();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String strResponse = "";
                    if (response.isSuccessful()) {
                        strResponse = response.body().string();
                        Logs.e("::::::: response :: ", strResponse);
                        progressDialog.dismiss();

                        if (listener != null)
                            listener.onSuccess(requestCode, response.code(), strResponse);
                    } else{
                        progressDialog.dismiss();
                        if (listener != null)
                            listener.onFailed();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void prepareFilePartBitmap(String partName, Bitmap bitmap, Context context) {

        Random random = new Random();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(context.getCacheDir(), "temporary_file" + random.nextInt((9999 - 1 + 1) + 1) + ".jpg");
        try {
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        this.builder.addPart(MultipartBody.Part.createFormData(partName, file.getName(), requestFile));
    }
}