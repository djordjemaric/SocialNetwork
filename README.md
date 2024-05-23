# Social network


### Local setup

Copy .env.example to .env

To start:
- docker compose up -d
- aws sso login --profile [PROFILE_NAME]


To stop containers: docker compose stop

### AWS Credendtials

1. Install AWS CLI [link](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
2. In your .aws directory, create a config file and paste the following. Set PROFILE_NAME as you desire:
 ```
[default]
region = eu-central-1
output = json

[profile PROFILE_NAME]
sso_session = random
sso_account_id = 211125711045
sso_role_name = AWSAdministratorAccess
region = eu-central-1
output = json

[sso-session random]
sso_start_url = https://d-9367654d55.awsapps.com/start/#
sso_region = eu-west-1
sso_registration_scopes = sso:account:access
```
3. Run aws sso login --profile [PROFILE_NAME]. Follow the prompts in your browser to login.
4. Run aws configure. When prompted, enter:
   - test
   - test
   - eu-central-1
   - json
5. Set an environment variable AWS_PROFILE_NAME to the name of the profile you created. 
