[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
# 🚀 NextHire - AI Powered Job Portal

### Frontend Repository
https://github.com/shubham9345/NextHire_frontend.git


## 📌 Overview

**NextHire** is a modern AI-powered job portal built using **Microservices Architecture** that connects candidates and companies through intelligent hiring workflows.

The platform allows candidates to create professional profiles, search and apply for jobs, track application status, participate in AI-based interviews, and share interview experiences. Companies can post jobs, discover talent, evaluate applicants using AI-generated ATS scores, conduct AI interviews, and analyze candidate performance in detail.

The system is designed with scalability, maintainability, and domain-driven architecture principles by dividing the application into independent business-focused microservices.

---

# ✨ Key Features

## 👨‍💼 Candidate Features

- User Registration & Login
- JWT Authentication
- Forgot Password via OTP Verification
- Create and Update Profile
- Resume Management
- Search Jobs
- Apply for Jobs
- Track Application Status
- View Interview Experiences
- Share Interview Experiences
- Participate in AI Interviews
- Receive Job Invitations from Companies

---

## 🏢 Company Features

- Company Registration & Login
- Create and Update Company Profile
- Post Jobs
- Manage Posted Jobs
- View Applicants
- ATS Score Based Candidate Ranking
- Candidate Shortlisting
- Conduct AI Interviews
- Detailed Candidate Analysis
- Talent Discovery
- Send Job Invitations

---

## 🤖 AI Features

### ATS Resume Scoring

- Automatic ATS score calculation
- Resume-job description matching
- Skill gap analysis
- Candidate ranking

### AI Interview

- Dynamic interview question generation
- AI-based answer evaluation
- Technical and behavioral assessment
- Candidate performance analytics
- Detailed interview result generation

---

# 🏗️ Microservices Architecture

NextHire follows a domain-driven microservices architecture consisting of **7 independent services**.

---

## 1. Auth Service

Handles authentication and authorization.

### Responsibilities

- User Signup
- User Login
- Company Signup
- Company Login
- JWT Token Generation
- Password Reset
- OTP Verification
- Authentication Management
---

## 2. User Service

Manages candidate-related operations.

### Responsibilities

- Create Profile
- Update Profile
- Fetch Profile
- Resume Management
- Candidate Details Management

### Performance Optimization

- Redis Caching for User Profiles

---

## 3. Company Service

Handles company operations and hiring workflows.

### Responsibilities

- Company Profile Management
- Job Posting
- Job Management
- Candidate Applications
- ATS Score Processing
- Candidate Ranking
- Candidate Shortlisting
- Interview Scheduling
- Talent Discovery
- Job Invitations

### Features

- Asynchronous ATS Score Calculation
- Candidate Sorting Based on ATS Score
  
---

## 4. AI Service

Centralized AI engine for ATS and interview functionalities.

### Responsibilities

- ATS Score Calculation
- Resume Analysis
- AI Interview Generation
- Candidate Evaluation
- Interview Report Generation

### AI Integration

- Groq AI API
- LLM-based Candidate Evaluation
---

## 5. Interview Experience Service

Community-driven interview preparation platform.

### Responsibilities

- Share Interview Experiences
- Browse Interview Experiences
- Search Experiences by Company
- Candidate Feedback Management
- 
---

## 6. Eureka Service Registry

Service discovery and registration.

### Responsibilities

- Service Registration
- Service Discovery
- Health Monitoring
- 
---

## 7. API Gateway

Single entry point for all client requests.

### Responsibilities

- Request Routing
- JWT Validation
- Security Enforcement
- Load Balancing
- Cross-Cutting Concerns

---

# 🏛️ High Level Architecture

```text
                         +----------------+
                           |     Client     |
                           +-------+--------+
                                   |
                                   |
                          +--------v--------+
                          |   API Gateway   |
                          +--------+--------+
                                   |
      ----------------------------------------------------------------
      |                |                |                |            |
      |                |                |                |            |
+-----v------+  +------v------+  +------v------+  +------v------+  +--v---------+
| Auth       |  | User        |  | Company     |  | AI          |  | Interview  |
| Service    |  | Service     |  | Service     |  | Service     |  | Service    |
+-----+------+  +------+------+\ +------+------+\ +------+------+\ +------+------+
      |                |                |                |               |
      |                |                |                |               |
+-----v------+  +------v------+  +------v------+  +------v------+  +-----v------+
| Auth DB    |  | User DB     |  | Company DB  |  | AI DB       |  | Interview DB|
| PostgreSQL |  | PostgreSQL  |  | PostgreSQL  |  | PostgreSQL  |  | PostgreSQL |
+------------+  +-------------+  +-------------+  +-------------+  +------------+

                     +----------------------+
                     | Eureka Service       |
                     | Registry Server      |
                     +----------+-----------+
                                ^
                                |
             All Microservices Register Themselves
                                |
      -------------------------------------------------------
      |            |             |            |             |
      |            |             |            |             |
    Auth         User         Company        AI       Interview
   Service      Service       Service      Service     Service


```

---

# 🔄 Application Workflow

## Candidate Flow

1. Register/Login
2. Create Profile
3. Upload Resume
4. Search Jobs
5. Apply to Job
6. ATS Score Generated
7. Application Ranked based on Ats score
8. Company Shortlists Candidate
9. Candidate Attends AI Interview
10. Interview Analysis Generated which is visible only to company

---

## Company Flow

1. Register/Login
2. Create Company Profile
3. Post Job
4. Receive Applications
5. ATS Score Calculation
6. Candidate Ranking
7. Shortlist Candidates
8. Conduct AI Interview
9. Analyze Candidate Reports
10. Hire Best Candidate

---

# 🛠️ Technology Stack

## Backend

- Java
- Spring Framework
- Spring Boot
- Spring Security
- Spring Cloud
- Hibernate
- Spring Data JPA
- Spring AI

---

## Database

- PostgreSQL
- SQL

---

## Caching

- Redis

---

## Search & Indexing

- Elasticsearch

---

## Authentication

- JWT Authentication
- OTP Verification while reseting password

---

## AI

- Groq API
- LLM Integration
- ATS Analysis
- AI Interview Evaluation

---

# 📂 Project Structure

```text
NextHire/
│
├── api-gateway/
│
├── service-registry/
│
├── auth-service/
│
├── user-service/
│
├── company-service/
│
├── ai-service/
│
├── interview-service/
│
├── docs/
│
└── README.md
```

---

# 🔐 Security

- JWT Authentication
- Role Based Authorization
- API Gateway Level Security
- Secure Password Storage
- OTP Verification
- Token Validation Across Services

---

# ⚡ Performance Optimizations

### Redis Caching

Used in User Service for:

- Frequently accessed profiles
- Reduced database load
- Faster profile retrieval

### Elasticsearch

Used for:

- Fast job searching
- Talent discovery
- Search indexing

### Asynchronous Processing

Used for:

- ATS score generation
- AI evaluation tasks

---

# 📋 Prerequisites

Before running the project, ensure the following are installed:

- Java 17+
- Maven 3.8+
- PostgreSQL
- Redis
- Elasticsearch
- Git

---

# ⚙️ Environment Variables

Create an `.env` file or configure the following properties:

```properties
# PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/nexthire
DB_USERNAME=postgres
DB_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Elasticsearch
ELASTICSEARCH_HOST=localhost
ELASTICSEARCH_PORT=9200

# Groq API
GROQ_API_KEY=your-groq-api-key

# Eureka
EUREKA_SERVER=http://localhost:8761/eureka
```

---

# 🚀 Installation & Setup

## 1. Clone Repository

```bash
git clone  https://github.com/shubham9345/NextHire.git

```

---

## 2. Configure PostgreSQL

Create database:

```sql
CREATE DATABASE nexthire;
```

Update database credentials in application properties.

---

## 3. Start Redis

```bash
redis-server
```

Verify:

```bash
redis-cli ping
```

Output:

```text
PONG
```

---

## 4. Start Elasticsearch

```bash
bin/elasticsearch
```

Verify:

```bash
curl http://localhost:9200
```

---

## 5. Configure Groq API Key

Set environment variable:

```bash
export GROQ_API_KEY=your-groq-api-key
```

Windows:

```cmd
set GROQ_API_KEY=your-groq-api-key
```

---

## 6. Start Eureka Service Registry

```bash
cd service-registry

mvn clean install

mvn spring-boot:run
```

Runs on:

```text
http://localhost:8761
```

---

## 7. Start Auth Service

```bash
cd auth-service

mvn clean install

mvn spring-boot:run
```

---

## 8. Start User Service

```bash
cd user-service

mvn clean install

mvn spring-boot:run
```

---

## 9. Start Company Service

```bash
cd company-service

mvn clean install

mvn spring-boot:run
```

---

## 10. Start AI Service

```bash
cd ai-service

mvn clean install

mvn spring-boot:run
```

---

## 11. Start Interview Service

```bash
cd interview-service

mvn clean install

mvn spring-boot:run
```

---

## 12. Start API Gateway

```bash
cd api-gateway

mvn clean install

mvn spring-boot:run
```

# 📡 Default Service Ports

| Service | Port |
|----------|--------|
| API Gateway | 8080 |
| Eureka Server | 8761 |
| Auth Service | 8081 |
| User Service | 8082 |
| Company Service | 8083 |
| AI Service | 8084 |
| Interview Service | 8085 |
| Redis | 6379 |
| PostgreSQL | 5432 |
| Elasticsearch | 9200 |

---

# 👨‍💻 Developed With

- Java
- Spring Boot
- Spring Cloud
- PostgreSQL
- Redis
- Elasticsearch
- Groq AI
- Microservices Architecture

---

# 📄 License

This project is licensed under the MIT License.

---
