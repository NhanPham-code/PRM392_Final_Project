package com.example.bakeryshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class VnPayWebViewActivity extends AppCompatActivity {

    private static final String DEMO_URL = "http://sandbox.vnpayment.vn/tryitnow/Home/CreateOrder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (url.contains("vnp_ResponseCode=00")) {
                    setResult(Activity.RESULT_OK);
                    finish();
                    return true;
                }

                if (url.contains("vnp_ResponseCode=24")) { // bị hủy
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
