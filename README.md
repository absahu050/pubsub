# Spring Boot Application with Google Cloud Pub/Sub

This Spring Boot application demonstrates how to integrate **Google Cloud Pub/Sub** for asynchronous messaging in a microservices architecture. It includes publisher and subscriber components to send and receive messages efficiently.

## 📌 Features
- Publish messages to a **Google Cloud Pub/Sub topic**.
- Subscribe and process messages from a **Pub/Sub subscription**.
- Uses **Spring Boot** and **Spring Cloud GCP** for seamless integration.
- Supports structured message payloads with JSON serialization.
- Exception handling and retry mechanisms.

## 🏗️ Architecture Overview
Google Cloud Pub/Sub is a real-time messaging service that allows applications to communicate asynchronously. This project contains:
- **Publisher Service**: Sends messages to a Pub/Sub topic.
- **Subscriber Service**: Listens to messages from a subscription and processes them.

## 🚀 Getting Started

### 1️⃣ Prerequisites
- Java 17+
- Maven 3.x
- Google Cloud Account with **Pub/Sub enabled**
- A **Google Cloud Project ID**
- Service Account with the required **Pub/Sub permissions**
- Spring Boot installed

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/your-username/your-repo-name.git
cd your-repo-name
