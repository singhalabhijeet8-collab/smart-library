# Backend API Documentation

## Backend Overview

The Smart Library backend is built using Spring Boot. It provides REST APIs for managing books, users, authentication, and book issue/return records.

The backend source code is located in:

```text
backend/src/main/java/com/smartlib
# Backend API Documentation

## Backend Overview

The Smart Library backend is built using Spring Boot. It provides REST APIs for managing books, users, authentication, and book issue/return records.

The backend source code is located in:

```text
backend/src/main/java/com/smartlib
```

## Base URL

```text

http://localhost:8080
```

## Main Backend Modules

| Module | Purpose |
|---|---|
| controller | Handles REST API requests |
| service | Contains business logic |
| repository | Connects services with the database |
| entity | Defines database tables/models |
| security | Handles JWT request filtering |
| config | Contains web and security configuration |

## Controllers

### AuthController

Handles admin/user login authentication.

Related file:

```text
backend/src/main/java/com/smartlib/controller/AuthController.java
```

### BookController

Handles book-related operations such as viewing and adding books.

Related file:

```text
backend/src/main/java/com/smartlib/controller/BookController.java
```

### UserController

Handles library user/member registration and user-related operations.

Related file:

```text
backend/src/main/java/com/smartlib/controller/UserController.java
```

### IssueController

Handles book issue and return operations.

Related file:

```text
backend/src/main/java/com/smartlib/controller/IssueController.java
```

## Running The Backend

From the project root:

```powershell
cd backend
mvn clean package
java -jar target/smart-library-1.0.jar
```

## Shubh's Contribution

Shubh contributed to backend API documentation by explaining the controller layer, backend modules, and API structure of the Smart Library System.
