# MORAGO Backend

**MORAGO** is a mobile and web application designed to assist foreigners living in Korea who do not speak the Korean language. The app connects users with freelance interpreters and provides on-demand voice translation services via internet calls.

## 📦 Features

- **User registration & authentication** via JWT  
- **Profile creation & role management** (user / interpreter / admin)  
- **Call setup & processing** between users and interpreters  
- **Real-time notifications** via WebSocket (Socket.io)  
- **File storage & upload** (documents, avatars) using AWS S3 or local filesystem  
- **API documentation** with Swagger  
- **WebRTC** for voice calls  
- Deployment on free hosting (e.g., Heroku, Railway)

## 🛠 Technology Stack

| Layer            | Technologies           |
| ---------------- | ---------------------- |
| **Language**     | Java                   |
| **Framework**    | Spring Boot            |
| **Database**     | MySQL                  |
| **API Type**     | REST                   |
| **Auth**         | JWT                    |
| **Docs**         | Swagger (OpenAPI 3)    |
| **Notifications**| WebSocket (Socket.io)  |
| **Calls**        | WebRTC                 |
| **Storage**      | AWS S3 / Local FS      |

## 🚀 Getting Started

### Prerequisites
- JDK 21
- Maven 3.9+
- MySQL 8.x
- Docker (optional, for DB)
### Installation

1. **Clone the repo**
     ```bash
   git clone https://github.com/your-org/morago-backend.git
   cd morago-backend
2. Copy environment config
    ```bash
   cp .env.example .env
3. Create database
    ```bash
   mysql -u root -p -e "CREATE DATABASE morago;"
4. Build & run (Or in IntelliJ IDEA: Run → BackendApplication.)
    ```bash
   ./mvnw spring-boot:run

### Environment Variables
The app uses a `.env` file for configuration.  
A template is provided in `.env.example` — copy it and fill in your values:

### API Documentation
Once the app is running, Swagger UI is available at:
    http://localhost:8080/swagger
