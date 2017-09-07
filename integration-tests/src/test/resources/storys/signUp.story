Meta:

Narrative:
As a admin bot user
I want to perform an action
So that I can achieve a business goal

Scenario: Send a valid request for a developer welcome
Given a valid form for the bootstrap process
When the admin user authenticates using a certificate
When the admin user adds a team member to the form
Then the admin user sends a developer welcome with automatic bootstrap set to false

Scenario: Send a valid request for a developer welcome with bootstrap
Given a valid form for the bootstrap process
When the admin user authenticates using a certificate
When the admin user adds a team member to the form
When the admin user sends a developer welcome with automatic bootstrap set to true
Then the welcome response contains valid bootstrap data

Scenario: Send a signup form with app domain not matching app url
Given a valid form for the bootstrap process
When the admin user authenticates using a certificate
When the admin user modifies the app domain field on the sign up form to notMatching.com
Then the admin user cannot send a developer welcome with automatic bootstrap set to false, fail on BAD_REQUEST

Scenario: Send a signup form with app url without https
Given a valid form for the bootstrap process
When the admin user authenticates using a certificate
When the admin user modifies the app url field on the sign up form to www.nohttps.com
When the admin user modifies the app domain field on the sign up form to nohttps.com
Then the admin user cannot send a developer welcome with automatic bootstrap set to false, fail on BAD_REQUEST

Scenario: Send a signup form with icon url without https
Given a valid form for the bootstrap process
When the admin user authenticates using a certificate
When the admin user modifies the app icon url field on the sign up form to wwww.nohttps.com
Then the admin user cannot send a developer welcome with automatic bootstrap set to false, fail on BAD_REQUEST

Scenario: Send a signup form with no developer email
Given a valid form for the bootstrap process
When the admin user authenticates using a certificate
When the admin user sets the email field to null
Then the admin user cannot send a developer welcome with automatic bootstrap set to false, fail on BAD_REQUEST