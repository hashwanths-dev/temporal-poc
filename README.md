## TEMPORAL POC

### SETUP

#### Start Temporal Server (Local)
```bash
brew install temporal
temporal server start-dev
```

> This setup is intended for local development only. Not for production use.

#### Running Application (Local)

1. Start Springboot application in local
2. Hit Endpoint - ```postman request POST 'localhost:8080/api/v1/banking/start'```
3. Track Workflow in Temporal UI - http://localhost:8233