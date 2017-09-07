Meta: @standard @agent

Narrative:
As a admin bot user
I want to authenticate using a certificate
So that I can verify that I can identify my session using a session token

Scenario: Authenticate using a valid certificate
Then the admin user authenticates using a certificate, receives valid token back

Scenario: Attempt to use endpoint with invalid session token
Then the admin user can not use an invalid session token to identify their session