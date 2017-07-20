# symphony-admin-bot
Symphony Admin Bot

**Features**
1. Bootstrap developers onto given Pod.
   - > Creating developer user on pod, and emailing sign in info.
   - > Generating and registering bot cert and creating bot service user on pod for developer.
   - > Generating and registering app cert and creating new app on pod for developer.
   - > Messaging developer within symphony bootstrap info (Certs and bot/app credentials).

**Quickstart**
1. Copy the quickstart folder within project to a desired folder.
2. Replace paths appropriately in bot.properties (see path labels).
3. Add the admin user cert/pem and pod CA cert to the quickstart "certs" folder. 
   - > This folder will automagically build truststore/keystore based on certs. Folder accepts file ".p12", ".cer" and ".pem" types.
   - > If you add any ".p12" keys, make sure to add respective password to password.txt file in quickstart "data" folder.
5. Adjust message templates in quickstart "templates" folder to your liking.
6. Adjust tomcat connector truststore/keystore to the truststore/keystore you defined in bot.properties.
