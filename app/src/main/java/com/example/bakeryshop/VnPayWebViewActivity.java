package com.example.bakeryshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VnPayWebViewActivity extends AppCompatActivity {

    private static final String DEMO_URL = "https://sandbox.vnpayment.vn/tryitnow/Home/CreateOrder"; // dùng HTTPS

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true); // bật DOM storage
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // Cho phép Mixed content nếu có
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // Gán WebChromeClient để tránh treo
        webView.setWebChromeClient(new WebChromeClient());

        // Gán WebViewClient xử lý callback thanh toán
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (url.contains("vnp_ResponseCode=00")) {
                    setResult(Activity.RESULT_OK);
                    finish();
                    return true;
                } else if (url.contains("vnp_ResponseCode=24")) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                    return true;
                }

                return false;
            }
        });

        webView.loadUrl(DEMO_URL);
    }
}