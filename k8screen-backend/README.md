# k8screen-backend

Backend application of k8screen


## Tech Stack

**Language:** Java

**Framework:** Spring

**Security**: Spring Security, JWT, Google OAuth2 Authentication

**Database**: PostgreSQL

**Migration**: Flyway


**API and Clients:**
- https://github.com/kubernetes-client/java/


## Run Locally

- Clone the project

```bash
   git clone https://mansur74/k8screen
```

- Go to the project directory

```bash
   cd k8screen/k8screen-backend
```

- Deploy the docker containers

```bash
   docker compose up
```

- Open ``k8screen`` project directory with Intellij IDEA

- Open ``application.yml`` on ``k8screen-backend`` project directory

- Open ``Project Structure...`` on File menu, add ``k8screen-backend`` as module on  ``Modules`` session, apply changes and save

- Change OAuth 2.0 Client ID informations with your Google ClientId and Clint Secret informations(If you don't have, you should create k8screen application and OAuth 2.0 Client ID)
```
   client-id: YOUR_GOOGLE_CLIENT_ID
   client-secret: YOUR_GOOGLE_CLIENT_SECRET
```

- Run K8screenBackendApplication.java on ``k8screen/k8screen-backend/src/main/java/io/k8screen/backend/K8screenBackendApplication.java``
