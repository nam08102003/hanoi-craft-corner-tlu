package com.example.hanoicraftcorner.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudinaryUploader {
    private static final String TAG = "CloudinaryUploader";
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY = "253475272448492"; // TODO: Replace with secure retrieval
    private static final String API_SECRET = "iEoKOqAhHEUIR0ayzY1Y-EwLU5c"; // TODO: Replace with secure retrieval
    private static final String CLOUD_NAME = "dcrohfxnw"; // Đã dùng ở upload

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    public static void uploadImage(File imageFile, UploadCallback callback) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/*")))
                .addFormDataPart("upload_preset", "HnCraftCorner")
                .build();

        Request request = new Request.Builder()
                .url("https://api.cloudinary.com/v1_1/dcrohfxnw/image/upload")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Upload failed", e);
                if (callback != null) callback.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : null;
                        Log.e(TAG, "Upload failed: " + response + ", body: " + errorBody);
                        if (callback != null) callback.onFailure(new Exception("Upload failed: " + response + ", body: " + errorBody));
                        return;
                    }
                    String responseBody = response.body() != null ? response.body().string() : null;
                    // Parse the URL from the response JSON
                    String imageUrl = responseBody != null ? parseImageUrl(responseBody) : null;
                    if (callback != null) callback.onSuccess(imageUrl);
                } catch (Exception e) {
                    if (callback != null) callback.onFailure(e);
                }
            }
        });
    }

    public static void uploadImage(InputStream inputStream, String fileName, UploadCallback callback) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes);
            if (read <= 0) throw new IOException("No data read from InputStream");
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName,
                            RequestBody.create(bytes, MediaType.parse("image/*")))
                    .addFormDataPart("upload_preset", "HnCraftCorner")
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dcrohfxnw/image/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e(TAG, "Upload failed", e);
                    if (callback != null) callback.onFailure(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : null;
                            Log.e(TAG, "Upload failed: " + response + ", body: " + errorBody);
                            if (callback != null) callback.onFailure(new Exception("Upload failed: " + response + ", body: " + errorBody));
                            return;
                        }
                        String responseBody = response.body() != null ? response.body().string() : null;
                        String imageUrl = responseBody != null ? parseImageUrl(responseBody) : null;
                        if (callback != null) callback.onSuccess(imageUrl);
                    } catch (Exception e) {
                        if (callback != null) callback.onFailure(e);
                    }
                }
            });
        } catch (Exception e) {
            if (callback != null) callback.onFailure(e);
        }
    }

    // Simple JSON parsing to extract the 'secure_url' field
    private static String parseImageUrl(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj.optString("secure_url", null);
        } catch (Exception e) {
            return null;
        }
    }

    // Hàm xóa ảnh trên Cloudinary bằng URL
    public interface DeleteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void deleteImageByUrl(String imageUrl, DeleteCallback callback) {
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId == null) throw new Exception("Không lấy được public_id từ URL ảnh");
            String url = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/destroy";
            String credentials = okhttp3.Credentials.basic(API_KEY, API_SECRET);
            okhttp3.RequestBody requestBody = new okhttp3.FormBody.Builder()
                    .add("public_id", publicId)
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Authorization", credentials)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (callback != null) callback.onFailure(e);
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    if (response.isSuccessful()) {
                        if (callback != null) callback.onSuccess();
                    } else {
                        if (callback != null) callback.onFailure(new Exception("Delete failed: " + response));
                    }
                }
            });
        } catch (Exception e) {
            if (callback != null) callback.onFailure(e);
        }
    }

    // Helper: Lấy public_id từ URL Cloudinary
    private static String extractPublicIdFromUrl(String url) {
        try {
            // Ví dụ: https://res.cloudinary.com/dcrohfxnw/image/upload/v1718888888/product_123456.jpg
            // public_id là phần sau /upload/ và trước .jpg
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) return null;
            String afterUpload = url.substring(uploadIndex + 8); // 8 = length of "/upload/"
            int dotIndex = afterUpload.lastIndexOf('.');
            if (dotIndex == -1) return null;
            // Nếu có thêm folder thì giữ nguyên
            return afterUpload.substring(0, dotIndex);
        } catch (Exception e) {
            return null;
        }
    }
}
