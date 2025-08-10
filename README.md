# Internet Banking Distributed System

A comprehensive, enterprise-grade distributed banking system built with Spring Boot, Spring Cloud, and Netflix Eureka for service discovery, with PostgreSQL for data persistence and comprehensive monitoring using Prometheus and Grafana.

## System Architecture

### Microservices
- **Eureka Server** - Service discovery and registration
- **API Gateway** - Centralized routing and security
- **User Service** - User management and authentication
- **Account Service** - Account management and operations
- **Transaction Service** - Transaction processing and history
- **Notification Service** - Email and SMS notifications
- **Payment Service** - Payment processing and transfers

### Technology Stack
- **Backend**: Spring Boot 3.x, Spring Cloud 2023.x
- **Service Discovery**: Netflix Eureka
- **Database**: PostgreSQL
- **Monitoring**: Prometheus + Grafana
- **Containerization**: Docker
- **Build Tool**: Maven

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker and Docker Compose
- PostgreSQL 15+

### Running the System

1. **Start Infrastructure Services**
   ```bash
   docker-compose up -d
   ```

2. **Build and Run Services**
   ```bash
   mvn clean install
   mvn spring-boot:run -pl eureka-server
   mvn spring-boot:run -pl api-gateway
   mvn spring-boot:run -pl user-service
   mvn spring-boot:run -pl account-service
   mvn spring-boot:run -pl transaction-service
   mvn spring-boot:run -pl notification-service
   mvn spring-boot:run -pl payment-service
   ```

3. **Access Services**
   - Eureka Dashboard: http://localhost:8761
   - API Gateway: http://localhost:8080
   - Grafana: http://localhost:3000 (admin/admin)
   - Prometheus: http://localhost:9090

## API Documentation

### User Service
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Account Service
- `POST /api/accounts` - Create account
- `GET /api/accounts/{id}` - Get account by ID
- `GET /api/accounts/user/{userId}` - Get accounts by user
- `PUT /api/accounts/{id}/balance` - Update balance

### Transaction Service
- `POST /api/transactions` - Create transaction
- `GET /api/transactions/{id}` - Get transaction by ID
- `GET /api/transactions/account/{accountId}` - Get transactions by account

### Payment Service
- `POST /api/payments/transfer` - Transfer between accounts
- `POST /api/payments/external` - External payment
- `GET /api/payments/{id}` - Get payment status

## Monitoring and Observability

### Metrics Collected
- Service response times
- Request rates and error rates
- Database connection pool metrics
- JVM metrics (memory, GC, threads)
- Custom business metrics

### Dashboards
- Service Overview Dashboard
- Database Performance Dashboard
- Business Metrics Dashboard
- Error Rate Dashboard

## Security Features
- JWT-based authentication
- Role-based access control
- API rate limiting
- Input validation and sanitization
- Audit logging

## Scalability Features
- Horizontal scaling support
- Load balancing
- Circuit breaker pattern
- Retry mechanisms
- Distributed caching

## Development

### Project Structure
```
bankingapplication/
├── eureka-server/          # Service discovery
├── api-gateway/            # API gateway and routing
├── user-service/           # User management
├── account-service/        # Account operations
├── transaction-service/     # Transaction processing
├── notification-service/    # Notifications
├── payment-service/        # Payment processing
├── common/                 # Shared components
├── docker-compose.yml      # Infrastructure services
└── monitoring/             # Prometheus and Grafana configs
```

### Adding New Services
1. Create new service module
2. Add Eureka client dependency
3. Configure service properties
4. Add monitoring endpoints
5. Update API gateway routes

## Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## License
MIT License
