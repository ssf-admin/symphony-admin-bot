package com.symphony.adminbot.util.file;

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
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.xml.bind.DatatypeConverter;

/**
 * Created by nick.tarsillo on 7/8/17.
 */
public class TomcatCertManager {
  private static ObjectMapper MAPPER = new ObjectMapper();
  private static final Logger LOG = LoggerFactory.getLogger(TomcatCertManager.class);

  private Map<String, String> passwordMap;
  private Map<String, File> keyMap;

  private String keyStoreFile;
  private KeyStore keyStore;
  private char[] keyOutPass;
  private String trustStoreFile;
  private KeyStore trustStore;
  private char[] trustOutPass;
  private String certDir;

  public TomcatCertManager(String keyStoreFile, String keyStorePassword,
      String trustStoreFile, String trustStorePassword,
      String certDir, String passwordFile) throws Exception {
    passwordMap = MAPPER.readValue(
        new FileInputStream(passwordFile),
        new TypeReference<HashMap<String, Object>>() {});

    keyOutPass = keyStorePassword.toCharArray();
    trustOutPass = trustStorePassword.toCharArray();

    keyStore = KeyStore.getInstance("JKS");
    keyStore.load(null, keyOutPass);

    trustStore = KeyStore.getInstance("JKS");
    trustStore.load(null, trustOutPass);

    this.keyStoreFile = keyStoreFile;
    this.trustStoreFile = trustStoreFile;
    this.certDir = certDir;
  }

  public void buildStoresFromCerts() throws Exception {
    File folder = new File(certDir);
    File[] files = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));

    for (File file : files) {
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
            trustStore.setKeyEntry(fileName + strAlias, key, trustOutPass, chain);

            LOG.info("Added p12 key with alias: " + fileName + strAlias);
          }
        }
      } else if (ext.equals("pem")) {
        byte[] certAndKey = IOUtils.toByteArray(new FileInputStream(file));
        byte[] certBytes = parseDERFromPEM(certAndKey,
            "-----BEGIN CERTIFICATE-----",
            "-----END CERTIFICATE-----");

        X509Certificate cert = generateCertificateFromDER(certBytes);

        if (keyStore.containsAlias(fileName)) {
          keyStore.deleteEntry(fileName);
        }
        keyStore.setCertificateEntry(fileName, cert);

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
          trustStore.setKeyEntry(fileName, key, trustOutPass, new Certificate[] {cert});
        }

        LOG.info("Added pem with alias: " + fileName);
      }
    }

    keyStore.store(new FileOutputStream(keyStoreFile), keyOutPass);
    trustStore.store(new FileOutputStream(trustStoreFile), trustOutPass);
  }

  public void refreshStores(int httpsPort) throws Exception {
    String objectString = "*:type=Connector,port=" + httpsPort + ",*";

    final ObjectName objectNameQuery = new ObjectName(objectString);

    for (final MBeanServer server : MBeanServerFactory.findMBeanServer(null)) {
      if (server.queryNames(objectNameQuery, null).size() > 0) {
        MBeanServer mbeanServer = server;
        ObjectName objectName = (ObjectName) server.queryNames(objectNameQuery, null).toArray()[0];

        mbeanServer.invoke(objectName, "stop", null, null);

        // Polling sleep to reduce delay to safe minimum.
        // Use currentTimeMillis() over nanoTime() to avoid issues
        // with migrating threads across sleep() calls.
        long start = System.currentTimeMillis();
        // Maximum of 6 seconds, 3x time required on an idle system.
        long max_duration = 6000L;
        long duration = 0L;
        do {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }

          duration = (System.currentTimeMillis() - start);
        } while (duration < max_duration &&
            server.queryNames(objectNameQuery, null).size() > 0);

        // Use below to get more accurate metrics.
        String message = "TrustStoreManager TrustStore Stop: took " + duration + "milliseconds";
        LOG.info(message);

        mbeanServer.invoke(objectName, "start", null, null);

        break;
      }
    }
  }

  public void generateKeyMap() throws Exception{
    File folder = new File(certDir);
    File[] files = folder.listFiles((dir, name) -> FilenameUtils.getExtension(name).equals("p12"));
    keyMap = new HashMap<>();

    for(File file: files){
      String password = passwordMap.get(file.getName());
      KeyStore p12 = KeyStore.getInstance("PKCS12");
      p12.load(new FileInputStream(file), password.toCharArray());
      Enumeration eAliases = p12.aliases();
      while (eAliases.hasMoreElements()) {
        String strAlias = (String) eAliases.nextElement();
        Certificate certificate = p12.getCertificate(strAlias);
        keyMap.put(certificate.getPublicKey().toString(), file);
      }
    }
  }

  public String getKeyPassword(X509Certificate certificate){
    return passwordMap.get(keyMap.get(certificate.getPublicKey().toString()).getName());
  }

  public String getKeyPath(X509Certificate certificate){
    String publicKey = certificate.getPublicKey().toString();
    return keyMap.get(publicKey).getAbsolutePath();
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
