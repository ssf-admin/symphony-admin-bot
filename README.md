# symphony-admin-bot
Symphony Admin Bot

**Features**
1. Bootstrap developers onto given Pod.
   - > Creating developer user on pod, and emailing sign in info.
   - > Generating and registering bot cert and creating bot service user on pod for developer.
   - > Generating and registering app cert and creating new app on pod for developer.
   - > Messaging developer within symphony bootstrap info (Certs and bot/app credentials).

**Running Tests**
- BDD tests run upon compiling the bot, and only require a test configuration to run properly.
To specify a configuration, run with the following VM option:
    - > -Dtest.properties.file="CONFIG_FILE_NAME_HERE"
- Optionally, you can also specify a custom configuration path by running with the following VM option:
    - > -Dtest.config.dir="CONFIG_PATH_HERE"
- For default configuration files, look in the default configuration path inside the integration-tests module's test resource "config" folder.
Example usage of running with default configuration files:
    - > mvn clean install -Dtest.properties.file="devpod.yaml"

**Tomcat Setup**
1. Download and unzip tomcat: https://tomcat.apache.org/download-80.cgi
2. Add the following text to server.xml file in tomcat conf folder:
   - > \<Connector port= "{ADD_DESIRED_AUTH_PORT_HERE}"  
       protocol="org.apache.coyote.http11.Http11NioProtocol" 
       SSLEnabled= "true" maxThreads="150" scheme="https" secure="true"  
       keystoreFile="./certs/tomcat.keystore"  
       keystorePass="{ADD_TOMCAT_KEYSTORE_PASSWORD_HERE}"  
       truststoreFile="./certs/server.truststore"  
       trustManagerClassName="com.symphony.adminbot.model.tomcat.TomcatCertManager" 
       truststorePass="{ADD_TOMCAT_TRUSTORE_PASSWORD}"  
       clientAuth="true" sslProtocol="TLS" /\>
   - > \<Connector port= "{ADD_DESIRED_SWAGGER_PORT_HERE}"  
       protocol="org.apache.coyote.http11.Http11NioProtocol" 
       SSLEnabled= "true" maxThreads="150" scheme="https" secure="true"  
       keystoreFile="./certs/tomcat.keystore"  
       keystorePass="{TOMCAT_KEYSTORE_PASSWORD}"  
       truststoreFile="./certs/server.truststore"  
       truststorePass="{ADD_TOMCAT_TRUSTORE_PASSWORD_HERE}"  
       clientAuth="false" sslProtocol="TLS" /\> 
3. Add compiled admin-bot-1.0-SNAPSHOT-jar-with-dependencies.jar to tomcat lib folder.
4. Add compiled ROOT folder to tomcat webapps folder.

**Quickstart**
1. Copy the quickstart folder within project to a desired folder.
2. Replace paths appropriately in bot.properties (see path labels and property explanations).
3. Add the admin user cert/pem and pod CA cert to the quickstart "certs" folder. 
   - > This folder will automatically build truststore/keystore based on certs. Folder accepts file ".p12", ".cer" and ".pem" types.
   - > If you add any ".p12" keys, make sure to add respective password to password.txt file in quickstart "data" folder.
5. Adjust message templates in quickstart "templates" folder to your liking.
6. Create sh file called "setenv.sh" in tomcat bin folder with following text:
   - > \#!/bin/sh <br/> CATALINA_OPTS="-Dbot.config.dir={PATH_TO_QUICKSTART}/quickstart" 

**Property Explanations**
- >  **auth.keystore.password** - password to the keystore responsible for authentication with bot. (This will only be used if custom key manager is defined in auth connector, which is not required.)
- >  **auth.truststore.password** - password to the truststore responsible for authentication with the bot.
- >  **bot.keystore.password** - Password for bot's .p12 key file. Used for bot auth.
- >  **keys.password.file** - file that maps .p12 files added in certs.dir to their respective password. (If you add a .p12 file to certs.dir you must map its password within this file.)
- >  **sessionauth.url** - url for bot to perform session auth against.
- >  **keyauth.url** - url for bot to perform key auth against.
- >  **symphony.agent.pod.url** - base url for bot to perform pod endpoint api requests against.
- >  **symphony.agent.agent.url** - base url for bot to perform agent endpoint api requests against.
- >  **bot.keystore.file.name** - the name of the bot .p12 key file. (Place this file in cert.dir.)
- >  **auth.keystore.file** - the keystore file responsible for authenticating with the bot. (If does not exist will generate file at set location. Only used if custom key manager is defined in auth connector, which is not required.)
- >  **auth.truststore.file** - the truststore file responsible for authentication with the bot. (If does not exist will generate file at set location. This is required and is used by custom trust manager defined auth connector.)
- >  **certs.dir** - directory containing all certs for bot. These certs will automatically be added into auth truststore/keystore.
- >  **adminbot.developer.p12.dir** - used for admin bot bootstrap functionality. All certs generated during bootstrap will be written here.
- >  **adminbot.developer.json.dir** - used for admin bot bootstrap functionality. All bootstrapped developer user data will be written here.
- >  **adminbot.user.json.dir** - used for to save information about a admin bot user, All admin bot user data wull be written here.
- >  **adminbot.auth.port** - tomcat configured connector port for client auth.
- >  **adminbot.swagger.port** - tomcat configured connector port to call swagger generated bot endpoints without client auth.
- >  **google.cred.file** - .p12 file used to authenticate with google api's. (Cred file for google service user.)
- >  **google.service.id** - id of the google service user.
- >  **adminbot.gmail.address** - gmail address for bot to use.
- >  **adminbot.bootstrap.bot.id** - used for admin bot bootstrap functionality. If no sevice bot username is provided with sign up form, a default user will be used. Default username equals botServiceUserX where X is the number saved in this file.
- >  **adminbot.bootstrap.email.subject.template** - used for admin bot bootstrap functionality. Message template for developer welcome email subject.
- >  **adminbot.bootstrap.email.message.template** - used for admin bot bootstrap functionality. Message template for developer welcome message.
- >  **adminbot.bootstrap.message.directional.template** - used for admin bot bootstrap functionality. Message template for directional developer welcome message.
- >  **adminbot.bootstrap.message.welcome.template** - used for admin bot bootstrap functionality. Message template for developer bootstrap welcome message.
- >  **adminbot.bootstrap.icon.url.template** - used for admin bot bootstrap functionality. Message template for default app icon url.

## Contributing

1. Fork it (<https://github.com/symphonyoss/symphony-admin-bot/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Read our [contribution guidelines](.github/CONTRIBUTING.md) and [Community Code of Conduct](https://www.finos.org/code-of-conduct)
4. Commit your changes (`git commit -am 'Add some fooBar'`)
5. Push to the branch (`git push origin feature/fooBar`)
6. Create a new Pull Request

## License

The code in this repository is distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Copyright 2016-2019 Symphony LLC