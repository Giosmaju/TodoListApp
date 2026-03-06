# TodoList App - Spring Boot Microservice

A comprehensive Spring Boot microservice for managing todo tasks with REST API and Telegram bot integration, containerized with Docker.

## Features

- **REST API** for todo management (CRUD operations)
- **Telegram Bot Integration** with polling architecture
- **H2 In-Memory Database** for development
- **Docker Containerization** for easy deployment
- **Multi-platform Access** via web API and Telegram
- **Spring Boot Auto-configuration** for rapid development

## Technologies

- **Framework**: Spring Boot 4.0.3
- **Database**: H2 (in-memory for development)
- **Build Tool**: Maven
- **Container**: Docker with Eclipse Temurin 17 JDK
- **Bot API**: Telegram Bots API v6.1.0
- **ORM**: Spring Data JPA with Hibernate

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (for containerized deployment)
- Telegram Bot Token (for bot functionality)

## Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/Giosmaju/TodoListApp.git
cd todolistapp
```

### 2. Build the Application
```bash
mvn clean package
```

### 3. Run Locally
```bash
java -jar target/todolistapp-0.0.1-SNAPSHOT.jar
```

### 4. Run with Docker
```bash
# Build the image
docker build -t todolistapp .

# Run the container
docker run -p 8080:8080 todolistapp
```

## API Documentation

### Base URL
```
http://localhost:8080
```

### Endpoints

#### Health Check
- **GET** `/`
- **Response**: `Microservicio corriendo en Docker`

#### Todo Management
- **GET** `/todos` - Retrieve all todos
- **POST** `/todos` - Create a new todo

**POST Body Example:**
```json
{
  "title": "Buy groceries",
  "description": "Milk, bread, and eggs",
  "completed": false
}
```

## Telegram Bot Usage

### Bot Configuration
1. Create a bot with [@BotFather](https://t.me/botfather) on Telegram
2. Get your bot token
3. Set environment variable: `TELEGRAM_BOT_TOKEN=your_bot_token_here`
4. Update `application.properties` with your bot username

### Available Commands
- `/start` - Welcome message and help
- `/list` - Show all your todos
- `/add <title> - <description>` - Create a new todo
- `/done <id>` - Mark todo as completed
- `/delete <id>` - Remove a todo
- `/help` - Show available commands

### Example Usage
```
User: /add Buy milk - Go to the supermarket
Bot: Task created!
     ID: 1
     Title: Buy milk
     Description: Go to the supermarket

User: /list
Bot: Your tasks:
     [PENDING] ID 1: Buy milk
        Go to the supermarket

User: /done 1
Bot: Task completed: Buy milk
```

## Docker Deployment

### Build and Run
```bash
# Build image
docker build -t todolistapp .

# Run container
docker run -p 8080:8080 todolistapp

# Or run on different port
docker run -p 8081:8080 todolistapp
```
