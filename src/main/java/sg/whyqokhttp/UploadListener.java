package sg.whyqokhttp;

/**
 * Created by khandelwal@SYON.COM on 7/3/17.
 */

public interface UploadListener {

    void onSuccess(int requestCode, int responseCode,String response);
    void onFailed();
}
