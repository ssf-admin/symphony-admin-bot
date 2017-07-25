package com.symphony.adminbot.bots;

import com.symphony.adminbot.api.V1AdminApi;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.model.session.AdminBotSession;
import com.symphony.adminbot.model.session.AdminBotUserSessionManager;
import com.symphony.adminbot.model.tomcat.TomcatCertManager;
import com.symphony.api.adminbot.api.factories.V1ApiServiceFactory;
import com.symphony.api.clients.AuthorizationClient;
import com.symphony.api.clients.SymphonyClient;
import com.symphony.api.clients.model.SymphonyAuth;

import org.apache.log4j.BasicConfigurator;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class AdminBot extends HttpServlet {
  private static final Logger LOG = LoggerFactory.getLogger(AdminBot.class);

  private TomcatCertManager tomcatCertManager;
  private AdminBotUserSessionManager adminSessionManager;

  @Override
  public void init(ServletConfig config){
    Security.addProvider(new BouncyCastleProvider());
    BasicConfigurator.configure();
    BotConfig.init();

    LOG.info("AdminBot starting...");
    setupBot();
  }

  void setupBot() {
    LOG.info("Setting up tomcat...");
    try {
      tomcatCertManager = new TomcatCertManager(
          System.getProperty(BotConfig.TOMCAT_KEYSTORE_FILE),
          System.getProperty(BotConfig.TOMCAT_KEYSTORE_PASSWORD),
          System.getProperty(BotConfig.TOMCAT_TRUSTSTORE_FILE),
          System.getProperty(BotConfig.TOMCAT_TRUSTSTORE_PASSWORD),
          System.getProperty(BotConfig.CERTS_DIR),
          System.getProperty(BotConfig.KEYS_PASSWORD_FILE));
      tomcatCertManager.buildStoresFromCerts();
      if(System.getProperty(BotConfig.TOMCAT_RESTART_AUTH_PORT).equals("true")) {
        tomcatCertManager.refreshStores(Integer.parseInt(System.getProperty(BotConfig.AUTH_PORT)));
      }
      tomcatCertManager.generateKeyMap();
      tomcatCertManager.setSSLStores();
    } catch (Exception e) {
      LOG.error("Could not set up cert manager for tomcat: ", e);
    }

      //Init client
      SymphonyClient symClient = new SymphonyClient();

      AuthorizationClient authClient = new AuthorizationClient(
          System.getProperty(BotConfig.SESSIONAUTH_URL),
          System.getProperty(BotConfig.KEYAUTH_URL));

    LOG.info("Setting up auth http client...");
      try {
        authClient.setKeystores(
            System.getProperty(BotConfig.TOMCAT_TRUSTSTORE_FILE),
            System.getProperty(BotConfig.TOMCAT_TRUSTSTORE_PASSWORD),
            System.getProperty(BotConfig.CERTS_DIR) + System.getProperty(
                BotConfig.BOT_KEYSTORE_FILE_NAME),
            System.getProperty(BotConfig.BOT_KEYSTORE_PASSWORD));
      } catch (Exception e) {
        LOG.error("Could not create HTTP Client for authentication: ", e);
      }
    LOG.info("Attempting bot auth...");
      try {
        SymphonyAuth symAuth = authClient.authenticate();

        symClient.init(
            symAuth,
            System.getProperty(BotConfig.SYMPHONY_AGENT),
            System.getProperty(BotConfig.SYMPHONY_POD));
      } catch (Exception e) {
        LOG.error("Authentication failed for bot: ", e);
      }

      adminSessionManager = new AdminBotUserSessionManager();
      AdminBotSession adminBotSession = new AdminBotSession(symClient);
      V1ApiServiceFactory.setService(new V1AdminApi(adminSessionManager, adminBotSession));

    LOG.info("AdminBot startup complete.");
  }

  /**
   * Authentication for admins to use the bot.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException {
    ServletOutputStream out = res.getOutputStream();
    res.setContentType("application/json");
    try {
      X509Certificate[] certs =
          (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");

      if (certs == null || certs.length == 0) {
        LOG.error("Req has no certs attached.");
        handleError(res, out, 400, BotConstants.NO_CERT);
        return;
      }

      X509Certificate certificate = certs[0];
      X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
      RDN cn = x500name.getRDNs(BCStyle.CN)[0];

      String sessionToken =
          adminSessionManager.getSessionToken(IETFUtils.valueToString(cn.getFirst().getValue()));
      out.println("{\"sessionToken\":\"" + sessionToken + "\"}");
      res.setStatus(200);
      out.close();
    } catch (Exception e) {
      LOG.error("Cert load from file failed: ", e);
      handleError(res, out,500, BotConstants.INTERNAL_ERROR);
    }

    out.close();
  }

  private void handleError(HttpServletResponse res, ServletOutputStream out, int errorCode,
      String errorMessage) throws IOException {

    out.println("{\"code\":" + errorCode + ",\"message\":\"" + errorMessage + "\"}");
    res.setStatus(errorCode);
    out.close();
  }
}
