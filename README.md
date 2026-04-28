# Smart Library System

Smart Library System is a Java-based library management project with a Swing desktop GUI, Spring Boot backend, and MySQL database.

## Features

- Admin account creation and login
- Book catalog viewing
- Add new books
- Register library members and generate unique user IDs
- Issue books using book ID and user ID
- Return books using issue ID
- View issue and return logs

## Technology Stack

- Java Swing for GUI panels
- Spring Boot for backend REST APIs
- Spring Data JPA for database operations
- MySQL for persistent storage
- Maven for build and dependency management
- JUnit support through Spring Boot test dependency

## Project Structure

```text
smart-library-backend/
  README.md                         Project overview and setup
  .gitignore                        Files ignored by Git
  backend/                          Spring Boot backend
    pom.xml                         Maven build file
    src/main/java/com/smartlib/
      config/                       Security and web configuration
      controller/                   REST controllers
      dto/                          Request/response DTOs
      entity/                       Database entities
      repository/                   JPA repositories
      security/                     JWT request filtering
      service/                      Business logic
      util/                         Shared utilities
    src/main/resources/
      application.properties        MySQL connection settings
    src/test/java/com/smartlib/     Unit and integration tests
  desktop/
    LibraryApp.java                 Swing desktop application
  frontend/
    index.html                      Optional HTML frontend prototype
  docs/
    README.md                       Diagram and documentation guide
    GITHUB_STEPS.md                 GitHub workflow notes
    database/                       ER diagram and SQL schema
    uml/                            UML diagrams
  config/
    vscode-settings.json            Optional VS Code Java setting
```

## Database Configuration

The backend connects to MySQL using:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=khush123
spring.jpa.hibernate.ddl-auto=update
```

Create the database before running the backend:

```sql
CREATE DATABASE library_db;
```

Optional schema and sample data are available in:

```text
docs/database/schema.sql
```

## How To Run

Build the backend:

```powershell
cd backend
mvn clean package
```

Run the backend:

```powershell
java -jar target/smart-library-1.0.jar
```

Compile and run the Swing GUI from the project root:

```powershell
javac desktop\LibraryApp.java
java -cp desktop LibraryApp
```

## Diagrams

Project diagrams are stored in `docs/`:

- Use case diagram
- Class diagram
- Issue/return sequence diagram
- MySQL ER diagram
- SQL schema

## Requirement Checklist

| Requirement | Status |
|---|---|
| Standalone Java Project | Completed with Swing GUI and Spring Boot backend |
| UML Diagrams | Added in `docs/uml` |
| Database Diagram | Added in `docs/database` |
| GUI Panel Development using Swing | Completed |
| Database Connection | Completed with MySQL configuration |
| Panel and Database Integration | Completed through REST APIs |
| Build Management | Completed using Maven |
| Unit Testing | Completed with service tests |
| Integration Testing | Completed with controller API test |
| Git/GitHub Collaboration | Initialize Git and push repository before submission |
| Artifactory Management | Local jar artifact generated in `backend/target` |

## Team Contributions

| Member | Contribution | Share |
|---|---|---|
| Abhijeet | Project setup, Swing desktop application, GitHub setup, final integration, and main README | 40% |
| Shubh | Backend REST controllers, security configuration, and API documentation | 20% |
| Sarthak | Entity models, repositories, service layer, and database schema | 20% |
| Kanika | Frontend dashboard, UML diagrams, and project documentation | 20% |

## Maintainer Notes

Abhijeet handled the final project integration by organizing the desktop app, backend, frontend, documentation, and GitHub repository structure into one complete submission.

