package com.wanyi.plugins.commandFunc;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wanyi.plugins.model.FuncInputData;
import com.wanyi.plugins.model.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Function;

public class ImageSaverFunc implements Function<FuncInputData, JSONObject> {
    private static final String TAG = "ImageSaverFunc";
//    private final Context context;
    private final String mimeType = "image/png";
    private final String fileExtension = ".png";

    private final String[] acceptedFormats = {"png", "jpeg", "jpg"};

    // 支持的图片格式
    private static final byte[] PNG_HEADER = {(byte) 0x89, 0x50, 0x4E, 0x47}; // PNG 文件头
    private static final byte[] JPEG_HEADER = {(byte) 0xFF, (byte) 0xD8};   // JPEG 文件头


    @Override
    public JSONObject apply(FuncInputData data) {
        String payload = data.getPayload();
        JSONObject jsonObject = JSONObject.parseObject(payload);
        JSONObject jsonPayload = jsonObject.getJSONObject("payload");
        String imgFormat = jsonPayload.getString("format");
        String imgBase64 = jsonPayload.getString("data");
        Context context = data.getContext();

        if (imgFormat == null || !Arrays.stream(acceptedFormats).findAny().isPresent()){
            return Response.fail("不支持的图片格式");
        }
        // Decode Base64 string to bytes
        byte[] imageBytes;
        try {
            imageBytes = Base64.decode(imgBase64, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid Base64 string, ignoring");
            return Response.fail();
        }

        // Determine image format and validate
        ImageFormat format = getImageFormat(imageBytes);
        if (format == null) {
            Log.w(TAG, "Decoded data is not a valid PNG or JPEG image, ignoring");
            return Response.fail();
        }

        try {
            // 准备图片文件名（带时间戳）
            String fileName = "image_" + System.currentTimeMillis() + "." + imgFormat;

            // 使用 MediaStore 保存图片到相册
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            // 兼容 Android 10 及以上（分区存储）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
            }

            // 插入图片记录到 MediaStore
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri == null) {
                Log.i(TAG, "Failed to create image URI");
                return Response.fail();
            }

            // 将 ByteBuffer 数据写入文件
            try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                if (outputStream != null) {
                    outputStream.write(imageBytes);
                    outputStream.flush();
                } else {
                    Log.i(TAG, "Failed to open output stream");
                    return Response.fail();
                }
            }

            // 完成保存，更新状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear();
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
                resolver.update(imageUri, contentValues, null, null);
            }

            Log.i(TAG, "Image saved to gallery: " + fileName);
            return Response.success();

        } catch (IOException e) {
            Log.e(TAG, "Error saving image to gallery: " + e.getMessage());
            return Response.fail();
        }
    }

    private enum ImageFormat {
        PNG("image/png", ".png"),
        JPEG("image/jpeg", ".jpg");

        final String mimeType;
        final String fileExtension;

        ImageFormat(String mimeType, String fileExtension) {
            this.mimeType = mimeType;
            this.fileExtension = fileExtension;
        }
    }

    // Determine image format (PNG or JPEG) based on byte header
    private ImageFormat getImageFormat(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return null; // Data too short
        }

        // Check PNG header
        boolean isPng = true;
        for (int i = 0; i < PNG_HEADER.length; i++) {
            if (bytes[i] != PNG_HEADER[i]) {
                isPng = false;
                break;
            }
        }
        if (isPng) {
            return ImageFormat.PNG;
        }

        // Check JPEG header
        boolean isJpeg = true;
        for (int i = 0; i < JPEG_HEADER.length; i++) {
            if (bytes[i] != JPEG_HEADER[i]) {
                isJpeg = false;
                break;
            }
        }
        if (isJpeg) {
            return ImageFormat.JPEG;
        }

        return null; // Not a supported image format
    }
}