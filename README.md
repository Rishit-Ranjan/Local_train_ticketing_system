# 🚆 Train Ticketing System

A robust, full-stack web application for managing train schedules, booking tickets, and handling secure payments via a wallet system. Built with modern web technologies including **Spring Boot** and **React**.

![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![React](https://img.shields.io/badge/React-18-blue)

## ✨ Features

### 👤 User Module
- **User Authentication**: Secure Login & Registration with JWT.
- **Train Search**: Search trains between stations for specific dates.
- **Booking System**: Select travel class, add passengers, and book tickets.
- **Wallet Integration**: Built-in wallet to add funds and pay for tickets seamlessly.
- **Booking History**: View past and upcoming trips, download tickets, or cancel bookings.

### 🛡️ Admin Module
- **Station Management**: Add and manage railway stations.
- **Schedule Management**: Create and update train schedules.
- **Reports**: View booking reports and revenue analytics.

## 🛠️ Tech Stack

### Backend
- **Framework**: Java Spring Boot
- **Architecture**: RESTful API
- **Security**: Spring Security + JWT
- **Database**: MySQL with Hibernate/JPA
- **Build Tool**: Gradle

### Frontend
- **Framework**: React.js (Vite)
- **Styling**: Tailwind CSS
- **State Management**: React Context API
- **HTTP Client**: Axios

## 🚀 Getting Started

### Prerequisites
- Java JDK 17 or higher
- Node.js (v18+) and npm
- MySQL Server

### 1. Database Setup
Create a MySQL database named `train_ticketing`:
```sql
CREATE DATABASE train_ticketing;
```

### 2. Backend Setup
Navigate to the `backend` directory:
```bash
cd backend
```

Configure your environment variables. You can set these in your OS or update `src/main/resources/application.yml` (not recommended for production).
**Required Environment Variables:**
- `DB_URL`: `jdbc:mysql://localhost:3306/train_ticketing`
- `DB_USERNAME`: Your MySQL username (default: `root`)
- `DB_PASSWORD`: Your MySQL password
- `JWT_SECRET`: A secure secret key for token generation

Run the application:
```bash
./gradlew bootRun
```
The backend server will start on `http://localhost:8080`.

### 3. Frontend Setup
Navigate to the `frontend` directory:
```bash
cd frontend
```

Install dependencies:
```bash
npm install
```

Start the development server:
```bash
npm run dev
```
The application will be available at `http://localhost:5173`.

## 📂 Project Structure
```
train_ticketing_system/
├── backend/            # Spring Boot Application
│   ├── src/main/java   # Source code
│   └── build.gradle    # Backend dependencies
├── frontend/           # React Application
│   ├── src/            # Components, Pages, Services
│   └── vite.config.js  # Vite configuration
└── README.md           # Project Documentation
```

## 🔒 Configuration
- **Port**: Backend runs on `8080`, Frontend on `5173`.
- **Proxy**: Frontend is configured to proxy API requests to `localhost:8080` during development.

## 🤝 Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
