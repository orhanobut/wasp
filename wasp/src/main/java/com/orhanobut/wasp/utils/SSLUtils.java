package com.orhanobut.wasp.utils;

import android.content.Context;

import com.orhanobut.wasp.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class SSLUtils {

  private SSLUtils() {
    //no instance
  }

  /**
   * Helper method to create an empty {@link SSLSocketFactory} which trusts all certificates
   * It is intended to be used only for testing purposes
   *
   * @return created empty factory
   */
  public static SSLSocketFactory getTrustAllCertSslSocketFactory() {
    try {
      TrustManager[] trustAllCerts = new TrustManager[]{
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

  /**
   * Helper method to create an {@link SSLSocketFactory} which trusts only given certificate
   *
   * @return created specific factory
   */
  public static SSLSocketFactory getPinnedCertSslSocketFactory(Context context,
                                                               int keyStoreRawResId,
                                                               String keyStorePassword) {
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
        Logger.e(e.getMessage());
      }
    }
  }

  /**
   * Helper method to create an empty {@link HostnameVerifier} which verifies all hosts
   * It is intended to be used only for testing purposes
   *
   * @return created empty hostnameVerifier
   */
  public static HostnameVerifier getEmptyHostnameVerifier() {
    return new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };
  }

}
