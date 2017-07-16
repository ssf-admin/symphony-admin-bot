package com.symphony.adminbot.bots;

import com.symphony.adminbot.api.V1AdminApi;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.model.session.AdminSession;
import com.symphony.adminbot.model.session.AdminSessionManager;
import com.symphony.adminbot.model.tomcat.TomcatCertManager;
import com.symphony.api.adminbot.api.factories.V1ApiServiceFactory;
import com.symphony.api.clients.AuthorizationClient;
import com.symphony.api.clients.SymphonyClient;
import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.pod.client.ApiException;

import org.apache.log4j.BasicConfigurator;
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
import javax.ws.rs.ForbiddenException;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class AdminBot extends HttpServlet {
  private static final Logger LOG = LoggerFactory.getLogger(AdminBot.class);

  private TomcatCertManager tomcatCertManager;
  private AdminSessionManager adminSessionManager;

  @Override
  public void init(ServletConfig config){
    Security.addProvider(new BouncyCastleProvider());
    BasicConfigurator.configure();
    BotConfig.init();

    LOG.info("AdminBot starting...");
    setupBot();
  }

  void setupBot() {
    try {
      tomcatCertManager = new TomcatCertManager(
          System.getProperty(BotConfig.KEYSTORE_FILE),
          System.getProperty(BotConfig.KEYSTORE_PASSWORD),
          System.getProperty(BotConfig.TRUSTSTORE_FILE),
          System.getProperty(BotConfig.TRUSTSTORE_PASSWORD),
          System.getProperty(BotConfig.CERTS_DIR),
          System.getProperty(BotConfig.KEYS_PASSWORD_FILE));
      tomcatCertManager.buildStoresFromCerts();
      tomcatCertManager.refreshStores(Integer.parseInt(System.getProperty(BotConfig.AUTH_PORT)));
      tomcatCertManager.generateKeyMap();
      tomcatCertManager.setSSLStores();
    } catch (Exception e) {
      LOG.error("Could not set up cert manager for tomcat: ", e);
    }
    adminSessionManager = new AdminSessionManager();
    V1ApiServiceFactory.setService(new V1AdminApi(adminSessionManager));
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

      //Init client
      SymphonyClient symClient = new SymphonyClient();

      AuthorizationClient authClient = new AuthorizationClient(
          System.getProperty(BotConfig.SESSIONAUTH_URL),
          System.getProperty(BotConfig.KEYAUTH_URL));

      authClient.setKeystores(
          System.getProperty(BotConfig.TRUSTSTORE_FILE),
          System.getProperty(BotConfig.TRUSTSTORE_PASSWORD),
          tomcatCertManager.getKeyPath(certs[0]),
          tomcatCertManager.getKeyPassword(certs[0]));

      SymphonyAuth symAuth = authClient.authenticate();

      symClient.init(
          symAuth,
          System.getProperty(BotConfig.SYMPHONY_AGENT),
          System.getProperty(BotConfig.SYMPHONY_POD));

      String sessionToken = symAuth.getSessionToken().getToken();
      String keyManagerToken = symAuth.getKeyToken().getToken();

      AdminSession adminSession = new AdminSession(symClient);
      adminSessionManager.putAdminSession(sessionToken, keyManagerToken, adminSession);

      out.println("{\"sessionToken\":\"" + sessionToken + "\", "
          + "\"keyManagerToken\":\"" + keyManagerToken + "\"}");
      res.setStatus(200);
      out.close();
    } catch (ApiException | ForbiddenException e) {
      LOG.error("User entitlement check failed: ", e);
      handleError(res, out,400, BotConstants.NOT_ENTITLED);
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
