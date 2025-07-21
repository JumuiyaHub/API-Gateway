# Microservices API Gateway

A Spring Cloud Gateway implementation that serves as a centralized entry point for microservices architecture, providing routing, security, circuit breaking, and API documentation aggregation.

## Architecture Overview

This API Gateway acts as a reverse proxy and aggregator for multiple microservices:

- **Product Service** (Port 8080)
- **Order Service** (Port 8082)
- **Inventory Service** (Port 8083)

## Key Features

### 🔐 Security
- **OAuth 2.0 JWT Authentication** via Keycloak
- **CORS Configuration** for frontend integration
- **Resource Server** implementation with JWT token validation

### 🔄 Resilience
- **Circuit Breaker Pattern** using Resilience4J
- **Timeout Management** with configurable duration
- **Retry Mechanism** with exponential backoff
- **Health Monitoring** with Spring Boot Actuator

### 📚 API Documentation
- **Aggregated Swagger UI** combining all microservices documentation
- **Centralized API Explorer** accessible at `/swagger-ui.html`
- **Individual Service Documentation** endpoints

### 🌐 Routing
- **Path-based Routing** to different microservices
- **Load Balancing** ready (when using service discovery)
- **Request/Response Filtering** capabilities

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Keycloak (included in docker-compose)

## Quick Start

### 1. Start Infrastructure Services

```bash
# Start Keycloak and MySQL
docker-compose up -d
```

Wait for Keycloak to fully initialize (usually 2-3 minutes).

### 2. Access Keycloak Admin Console

- **URL**: http://localhost:8181
- **Username**: admin
- **Password**: admin

### 3. Start the API Gateway

```bash
# Run the application
mvn spring-boot:run
```

The gateway will be available at `http://localhost:9000`

## API Endpoints

### Core Routes

| Path | Target Service | Description |
|------|---------------|-------------|
| `/api/product` | Product Service (8080) | Product management operations |
| `/api/order` | Order Service (8082) | Order processing operations |
| `/api/inventory` | Inventory Service (8083) | Inventory management operations |

### Documentation Routes

| Path | Description |
|------|-------------|
| `/swagger-ui.html` | Aggregated API documentation |
| `/api-docs` | OpenAPI specification |
| `/aggregate/product-service/v3/api-docs` | Product service API docs |
| `/aggregate/order-service/v3/api-docs` | Order service API docs |
| `/aggregate/inventory-service/v3/api-docs` | Inventory service API docs |

### Health & Monitoring

| Path | Description |
|------|-------------|
| `/actuator/health` | Gateway health status |
| `/actuator/circuitbreakers` | Circuit breaker status |
| `/actuator` | All actuator endpoints |

## Configuration

### Security Configuration

The gateway uses OAuth 2.0 with JWT tokens issued by Keycloak:

```yaml
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8181/realms/spring-microservices-security-realm
```

### CORS Configuration

Configured to allow requests from frontend applications:

```yaml
Allowed Origins: http://localhost:4200
Allowed Methods: GET, POST
Allowed Headers: *
Credentials: Enabled
```

### Circuit Breaker Configuration

Resilience4J circuit breaker settings:

```yaml
Sliding Window Size: 10 requests
Failure Rate Threshold: 50%
Wait Duration in Open State: 5 seconds
Timeout Duration: 3 seconds
Max Retry Attempts: 3
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/kiru/microservices/gateway/
│   │       ├── ApiGatewayApplication.java     # Main application class
│   │       ├── config/
│   │       │   └── SecurityConfig.java       # Security configuration
│   │       └── routes/
│   │           └── Routes.java               # Gateway routes definition
│   └── resources/
│       └── application.properties            # Application configuration
├── test/
│   └── java/
│       └── ...                               # Test classes
docker/
└── keycloak/
    └── realms/                               # Keycloak realm configuration
docker-compose.yml                            # Infrastructure services
volume-data/
└── mysql_keycloak_data/                      # Persistent MySQL data
```

## Security Setup

### 1. Keycloak Realm Configuration

1. Access Keycloak Admin Console
2. Create or import the realm: `spring-microservices-security-realm`
3. Configure clients for your microservices
4. Set up users and roles as needed

### 2. JWT Token Usage

To access protected endpoints, include the JWT token in the Authorization header:

```bash
curl -H "Authorization: Bearer <your-jwt-token>" \
     http://localhost:9000/api/product
```

## Monitoring and Health Checks

### Circuit Breaker Monitoring

Monitor circuit breaker states:

```bash
curl http://localhost:9000/actuator/circuitbreakers
```

### Health Status

Check overall gateway health:

```bash
curl http://localhost:9000/actuator/health
```

## Development

### Running in Development Mode

1. Start infrastructure services:
   ```bash
   docker-compose up -d
   ```

2. Start your microservices on their respective ports:
    - Product Service: 8080
    - Order Service: 8082
    - Inventory Service: 8083

3. Start the gateway:
   ```bash
   mvn spring-boot:run
   ```

### Testing

Run the test suite:

```bash
mvn test
```

## Troubleshooting

### Common Issues

1. **Circuit Breaker Tripped**
    - Check if target microservices are running
    - Verify service URLs in routes configuration
    - Monitor circuit breaker metrics

2. **Authentication Failures**
    - Verify Keycloak is running and accessible
    - Check JWT token validity
    - Ensure realm configuration is correct

3. **CORS Issues**
    - Verify frontend origin in CORS configuration
    - Check allowed methods and headers

### Logs

Enable debug logging for troubleshooting:

```properties
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Configuration Reference

### Key Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Gateway port | 9000 |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | Keycloak realm URL | Required |
| `resilience4j.circuitbreaker.configs.default.slidingWindowSize` | Circuit breaker window size | 10 |
| `resilience4j.circuitbreaker.configs.default.failureRateThreshold` | Failure rate threshold | 50% |
| `resilience4j.timelimiter.configs.default.timeout-duration` | Request timeout | 3s |

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Check the troubleshooting section
- Review logs for error details
- Ensure all prerequisites are met
- Verify microservices are running and accessible