# OpsCopilot

OpsCopilot is an AI-powered Operations Assistant designed to answer natural language queries about orders, payments, deliveries, customers, and operational timelines. It acts as a bridge between an operations employee and the backend database, ensuring that data retrieved is accurate, deterministic, and read-only. [See Features & Capabilities](#features--capabilities)

## Features & Capabilities

- **Natural Language Order Tracking**: Ask about any order in plain English and get up-to-date information on its status.
- **Payment Verification**: Quickly check if an order has been paid for, what the amount was, and the payment status.
- **Delivery Status Monitoring**: Get real-time updates on delivery schedules, dispatched orders, and expected arrival dates.
- **Customer Information Retrieval**: Fetch customer details seamlessly without running complex database queries.
- **Comprehensive Timelines**: View a complete end-to-end timeline of an order, from placement to delivery, including all operational events.
- **Read-Only Safety**: The AI is restricted to read-only API endpoints, guaranteeing that your database cannot be inadvertently modified through chat interactions.

## Project Overview

The system uses **Google Gemini** for intent understanding and tool selection, combined with a **Spring Boot** backend serving as the source of truth, connected to a **PostgreSQL** database. The LLM interacts safely with the backend by executing predefined RESTful tools and never directly querying the database.

## Architecture

- **Frontend**: Next.js, React, Tailwind CSS (Chat-style interface)
- **Backend**: Java 21, Spring Boot 3.x, Spring Data JPA
- **Database**: PostgreSQL
- **AI Integration**: Google Gemini via explicit Tool/Function calling

## Prerequisites
- Java 21
- Maven
- Node.js (v18+)
- Docker and Docker Compose
- A Google Gemini API Key

## Setup & Run

### 1. Configure Environment
Create a `.env` file in `backend/ops-copilot/` based on `.env.example`:
```
GEMINI_API_KEY=your-actual-api-key
GEMINI_MODEL=gemini-1.5-pro-latest
DB_HOST=localhost
DB_PORT=5432
DB_NAME=opscopilot
DB_USERNAME=opscopilot
DB_PASSWORD=opscopilot_pass
```

### 2. Start PostgreSQL
```bash
cd backend/ops-copilot
docker-compose up -d
```
The application will automatically initialize the database schema and insert seed data from `database/seed.sql` upon startup.

### 3. Start the Backend API
```bash
cd backend/ops-copilot
./mvnw spring-boot:run
```
The API runs at `http://localhost:8080`.

### 4. Start the Frontend
```bash
cd frontend
npm install
npm run dev
```
The UI runs at `http://localhost:3000`.

## Example Queries

Try asking the Copilot:
- *"What's the payment status for order #4521?"*
- *"Customer says they've paid for order #1289 but delivery isn't scheduled. What's going on?"*
- *"Give me a full status summary for order #2231."*

## Testing
Run backend unit and integration tests using:
```bash
./mvnw test
```

## Security Considerations
- The `GEMINI_API_KEY` is completely hidden on the backend and never exposed to the frontend.
- AI is restricted to calling explicitly registered tools (`get_order`, `get_payment`, `get_delivery`, `get_customer`, `get_order_timeline`).
- No direct SQL execution is permitted.

## Limitations & Future Improvements
- **Authentication**: Currently omitted for MVP, but role-based access control would be essential for production.
- **Caching**: Could be added for read-heavy operations like `get_order`.
- **Scaling**: As demand grows, an LLM gateway could help balance AI requests and handle rate limits.