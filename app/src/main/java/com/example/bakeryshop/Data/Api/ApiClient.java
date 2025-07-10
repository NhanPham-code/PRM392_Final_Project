package com.example.bakeryshop.Data.Api;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // QUAN TRỌNG: Đối với localhost trên trình giả lập Android, sử dụng 10.0.2.2
    // Đối với thiết bị thực, sử dụng địa chỉ IP của máy tính của bạn trong mạng cục bộ
    private static final String BASE_URL = "https://10.0.2.2:7112/"; // Giữ nguyên HTTPS nếu bạn chọn giải pháp này
    private static ApiClient instance;
    private ApiService apiService;

    private ApiClient(Context context) {
        // Tạo HttpLoggingInterceptor để log các yêu cầu và phản hồi (hữu ích cho việc gỡ lỗi)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Tạo OkHttpClient và thêm interceptor
        AuthInterceptor authInterceptor = new AuthInterceptor(context);
        OkHttpClient client = getUnsafeOkHttpClient(loggingInterceptor, authInterceptor); // Sử dụng phương thức mới để lấy client không an toàn

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client) // Sử dụng OkHttpClient đã tùy chỉnh (không an toàn cho dev)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context);
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    /**
     * Phương thức này tạo một OkHttpClient bỏ qua tất cả các kiểm tra chứng chỉ SSL.
     * CHỈ ĐƯỢC SỬ DỤNG CHO MÔI TRƯỜNG PHÁT TRIỂN/THỬ NGHIỆM!
     * KHÔNG BAO GIỜ SỬ DỤNG TRONG MÔI TRƯỜNG PRODUCTION!
     */
    private static OkHttpClient getUnsafeOkHttpClient(HttpLoggingInterceptor loggingInterceptor, Interceptor authInterceptor) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(loggingInterceptor);
            builder.addInterceptor(authInterceptor); // ✅ GẮN INTERCEPTOR ĐÃ TẠO
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
