package com.orhanobut.wasp;

import android.content.Context;

import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author Emmar Kardeslik
 */
public class OkHttpStack extends HurlStack implements WaspHttpStack<HttpStack> {

    private final OkUrlFactory okUrlFactory;

    public OkHttpStack() {
        this(new OkHttpClient());
    }

    public OkHttpStack(final OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException("HttpClient may not be null.");
        }
        okUrlFactory = new OkUrlFactory(client);
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return okUrlFactory.open(url);
    }

    @Override
    public HttpStack getHttpStack() {
        return this;
    }

    @Override
    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        okUrlFactory.client().setSslSocketFactory(sslSocketFactory);
    }

    @Override
    public void setCookieHandler(CookieHandler cookieHandler) {
        okUrlFactory.client().setCookieHandler(cookieHandler);
    }

    static SSLSocketFactory getTrustAllCertSslSocketFactory() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static SSLSocketFactory getPinnedCertSslSocketFactory(Context context, int keyStoreRawResId, String keyStorePassword) {
        InputStream in = null;
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");

            // Get the keystore from raw resource
            in = context.getResources().openRawResource(keyStoreRawResId);
            // Initialize the keystore with the provided trusted certificates
            // Also provide the password of the keystore
            trusted.load(in, keyStorePassword.toCharArray());

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trusted);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
