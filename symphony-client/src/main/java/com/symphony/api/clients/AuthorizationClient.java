package com.symphony.api.clients;

import com.symphony.api.clients.model.SymphonyAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.authenticator.api.AuthenticationApi;
import org.symphonyoss.symphony.authenticator.invoker.ApiClient;
import org.symphonyoss.symphony.authenticator.invoker.ApiException;
import org.symphonyoss.symphony.authenticator.invoker.Configuration;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class AuthorizationClient {
  private final Logger LOG = LoggerFactory.getLogger(AuthorizationClient.class);

  private final String sessionAuthUrl;
  private final String keyAuthUrl;

  private Client httpClient;

  public AuthorizationClient(String sessionAuthUrl, String keyAuthUrl){
    this.sessionAuthUrl = sessionAuthUrl;
    this.keyAuthUrl = keyAuthUrl;
  }

  public AuthorizationClient(SymphonyAuth symAuth) {
    this.sessionAuthUrl = symAuth.getSessionUrl();
    this.keyAuthUrl = symAuth.getKeyUrl();
    this.httpClient = symAuth.getClient();
  }


  public SymphonyAuth authenticate() throws ApiException {
      if(sessionAuthUrl == null || keyAuthUrl == null)
        throw new NullPointerException("Session URL or Keystore URL is null.");

      SymphonyAuth symAuth = new SymphonyAuth();
      symAuth.setKeyUrl(keyAuthUrl);
      symAuth.setSessionUrl(sessionAuthUrl);
      symAuth.setClient(httpClient);

      ApiClient authenticatorClient = Configuration.getDefaultApiClient();
      authenticatorClient.setHttpClient(httpClient);
      AuthenticationApi authenticationApi = new AuthenticationApi(authenticatorClient);

      authenticatorClient.setBasePath(sessionAuthUrl);
      symAuth.setSessionToken(authenticationApi.v1AuthenticatePost());
      LOG.debug("SessionToken: {} : {}", symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());

      authenticatorClient.setBasePath(keyAuthUrl);
      symAuth.setKeyToken(authenticationApi.v1AuthenticatePost());
      LOG.debug("KeyToken: {} : {}", symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());

      return symAuth;
  }

  /**
   * Create custom client with specific keystores.
   *
   * @param clientKeyStore     Client (BOT) keystore file
   * @param clientKeyStorePass Client (BOT) keystore password
   * @param trustStore         Truststore file
   * @param trustStorePass     Truststore password
   * @return Custom HttpClient
   * @throws Exception Generally IOExceptions thrown from instantiation.
   */
  public void setKeystores(String trustStore, String trustStorePass, String clientKeyStore,
      String clientKeyStorePass) throws Exception {
    KeyStore cks = KeyStore.getInstance("PKCS12");
    KeyStore tks = KeyStore.getInstance("JKS");

    loadKeyStore(cks, clientKeyStore, clientKeyStorePass);
    loadKeyStore(tks, trustStore, trustStorePass);

    httpClient = ClientBuilder.newBuilder()
        .keyStore(cks, clientKeyStorePass.toCharArray())
        .trustStore(tks)
        .hostnameVerifier((string, sll) -> true)
        .build();
  }

  /**
   * Internal keystore loader
   *
   * @param ks     Keystore object which defines the expected type (PKCS12, JKS)
   * @param ksFile Keystore file to process
   * @param ksPass Keystore password for file to process
   * @throws Exception Generally IOExceptions generated from file read
   */
  private static void loadKeyStore(KeyStore ks, String ksFile, String ksPass) throws Exception {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(ksFile);
      ks.load(fis, ksPass.toCharArray());
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
  }
}
