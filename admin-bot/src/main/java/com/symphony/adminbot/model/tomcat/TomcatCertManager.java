package com.symphony.adminbot.model.tomcat;

import com.symphony.adminbot.config.BotConfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

/**
 * Created by nick.tarsillo on 7/8/17.
 */
public class TomcatCertManager implements X509TrustManager, X509KeyManager {
  private static ObjectMapper MAPPER = new ObjectMapper();
  private static final Logger LOG = LoggerFactory.getLogger(TomcatCertManager.class);

  private static X509TrustManager trustManager = null;
  private static X509KeyManager keyManager = null;

  private Map<String, String> passwordMap;

  private Set<String> registeredCerts = new HashSet<>();

  private String keyStoreFile;
  private KeyStore keyStore;
  private char[] keyOutPass;
  private String trustStoreFile;
  private KeyStore trustStore;
  private char[] trustOutPass;
  private String certDir;

  public TomcatCertManager() throws Exception {
    super();
    LOG.info("Setting up tomcat...");
    BotConfig.init();

    keyStoreFile = System.getProperty(BotConfig.AUTH_KEYSTORE_FILE);
    keyOutPass = System.getProperty(BotConfig.AUTH_KEYSTORE_PASSWORD).toCharArray();
    trustOutPass = System.getProperty(BotConfig.AUTH_TRUSTSTORE_PASSWORD).toCharArray();
    trustStoreFile = System.getProperty(BotConfig.AUTH_TRUSTSTORE_FILE);
    certDir = System.getProperty(BotConfig.CERTS_DIR);

    passwordMap = MAPPER.readValue(
        new FileInputStream(System.getProperty(BotConfig.KEYS_PASSWORD_FILE)),
        new TypeReference<HashMap<String, Object>>() {});

    keyStore = KeyStore.getInstance("JKS");
    keyStore.load(null, keyOutPass);

    trustStore = KeyStore.getInstance("JKS");
    trustStore.load(null, trustOutPass);

    checkForNewCerts();
    setSSLStores();
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    checkForNewCerts();

    return trustManager.getAcceptedIssuers();
  }
  @Override
  public void checkClientTrusted(X509Certificate[] certs, String authType)
      throws CertificateException {
    checkForNewCerts();
    trustManager.checkClientTrusted(certs, authType);
  }

  @Override
  public void checkServerTrusted(X509Certificate[] certs, String authType)
      throws CertificateException {
    checkForNewCerts();
    trustManager.checkServerTrusted(certs, authType);
  }

  @Override
  public String[] getClientAliases(String s, Principal[] principals) {
    checkForNewCerts();
    return keyManager.getClientAliases(s, principals);
  }

  @Override
  public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
    checkForNewCerts();
    return keyManager.chooseClientAlias(strings, principals, socket);
  }

  @Override
  public String[] getServerAliases(String s, Principal[] principals) {
    checkForNewCerts();
    return keyManager.getServerAliases(s, principals);
  }

  @Override
  public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
    checkForNewCerts();
    return keyManager.chooseServerAlias(s, principals, socket);
  }

  @Override
  public X509Certificate[] getCertificateChain(String s) {
    checkForNewCerts();
    return keyManager.getCertificateChain(s);
  }

  @Override
  public PrivateKey getPrivateKey(String s) {
    checkForNewCerts();
    return keyManager.getPrivateKey(s);
  }

  private void resetStores()
      throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(trustStore);
    TrustManager[] trustmanagers = trustManagerFactory.getTrustManagers();
    if (trustmanagers.length == 0) {
      throw new NoSuchAlgorithmException("No trust manager found");
    }

    trustManager = (X509TrustManager) trustmanagers[0];

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, keyOutPass);
    KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
    if (keyManagers.length == 0) {
      throw new NoSuchAlgorithmException("No key manager found");
    }

    keyManager = (X509KeyManager) keyManagers[0];
  }

  private void checkForNewCerts() {
    File folder = new File(certDir);
    File[] files = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));

    boolean newFiles = false;
    for(File file: files) {
      if(!registeredCerts.contains(file.getName())) {
        try {
          addToStore(file);
          newFiles = true;
        } catch (Exception e) {
          LOG.error("Could not add file to trust/keystore: ", e);
        }
        registeredCerts.add(file.getName());
      }
    }

    try {
      if(newFiles) {
        resetStores();
      }
    } catch (Exception e) {
      LOG.error("Could not reset stores: ", e);
    }
  }

  private void addToStore(File file) throws Exception {
    String ext = FilenameUtils.getExtension(file.getName());
    String fileName = FilenameUtils.removeExtension(file.getName());
    if (ext.equals("p12")) {
      String password = passwordMap.get(file.getName());
      KeyStore p12 = KeyStore.getInstance("PKCS12");
      p12.load(new FileInputStream(file), password.toCharArray());

      Enumeration eAliases = p12.aliases();
      while (eAliases.hasMoreElements()) {
        String strAlias = (String) eAliases.nextElement();
        if (p12.isKeyEntry(strAlias)) {
          Key key = p12.getKey(strAlias, password.toCharArray());

          Certificate[] chain = p12.getCertificateChain(strAlias);

          keyStore.setKeyEntry(fileName + strAlias, key, keyOutPass, chain);
          //trustStore.setKeyEntry(fileName + strAlias, key, trustOutPass, chain);

          LOG.info("Added p12 key with alias: " + fileName + strAlias);
        }
      }
    } else if (ext.equals("pem")) {
      byte[] certAndKey = IOUtils.toByteArray(new FileInputStream(file));
      byte[] certBytes = parseDERFromPEM(certAndKey,
          "-----BEGIN CERTIFICATE-----",
          "-----END CERTIFICATE-----");

      X509Certificate cert = generateCertificateFromDER(certBytes);

      if (trustStore.containsAlias(fileName)) {
        trustStore.deleteEntry(fileName);
      }
      trustStore.setCertificateEntry(fileName, cert);

      if (DERExists(certAndKey,
          "-----BEGIN PRIVATE KEY-----",
          "-----END PRIVATE KEY-----")) {
        byte[] keyBytes = parseDERFromPEM(certAndKey,
            "-----BEGIN PRIVATE KEY-----",
            "-----END PRIVATE KEY-----");
        RSAPrivateKey key = generatePrivateKeyFromDER(keyBytes);

        keyStore.setKeyEntry(fileName, key, keyOutPass, new Certificate[] {cert});
      }

      LOG.info("Added pem with alias: " + fileName);
    } else if (ext.equals("cer")) {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      Certificate cert = cf.generateCertificate(new FileInputStream(file));
      trustStore.setCertificateEntry(fileName, cert);
      LOG.info("Added cer with alias: " + fileName);
    }

    keyStore.store(new FileOutputStream(keyStoreFile), keyOutPass);
    trustStore.store(new FileOutputStream(trustStoreFile), trustOutPass);
  }

  public void setSSLStores(){
    System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
    System.setProperty("javax.net.ssl.trustStorePassword", new String(trustOutPass));
    System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    System.setProperty("javax.net.ssl.keyStore", keyStoreFile);
    System.setProperty("javax.net.ssl.keyStorePassword", new String(keyOutPass));
    System.setProperty("javax.net.ssl.keyStoreType", "JKS");
  }

  private boolean DERExists(byte[] pem, String beginDelimiter, String endDelimiter){
    String data = new String(pem);
    String[] tokens = data.split(beginDelimiter);
    return tokens.length > 1;
  }

  private byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
    String data = new String(pem);
    String[] tokens = data.split(beginDelimiter);
    tokens = tokens[1].split(endDelimiter);
    return DatatypeConverter.parseBase64Binary(tokens[0]);
  }

  private RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws Exception {
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

    KeyFactory factory = KeyFactory.getInstance("RSA");

    return (RSAPrivateKey)factory.generatePrivate(spec);
  }

  private X509Certificate generateCertificateFromDER(byte[] certBytes) throws Exception {
    CertificateFactory factory = CertificateFactory.getInstance("X.509");

    return (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(certBytes));
  }
}
