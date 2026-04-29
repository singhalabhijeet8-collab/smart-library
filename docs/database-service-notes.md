# Database And Service Layer Notes

## Database Overview

The Smart Library System uses MySQL as the database. The backend connects to the database using Spring Data JPA.

Database name:

```text
library_db
```

The database configuration is stored in:

```text
backend/src/main/resources/application.properties
```

The SQL schema file is available at:

```text
docs/database/schema.sql
```

## Main Entities

### Book

The `Book` entity stores information about library books.

Typical book data includes:

- book ID
- title
- author
- availability/status

Related file:

```text
backend/src/main/java/com/smartlib/entity/Book.java
```

### User

The `User` entity stores information about library members or users.

Typical user data includes:

- user ID
- name
- email/contact details
- role or account details

Related file:

```text
backend/src/main/java/com/smartlib/entity/User.java
```

### Issue

The `Issue` entity stores book issue and return records.

It connects a book with a user and tracks issue/return activity.

Typical issue data includes:

- issue ID
- book reference
- user reference
- issue date
- return date
- status

Related file:

```text
backend/src/main/java/com/smartlib/entity/Issue.java
```

## Repository Layer

Repositories connect the service layer with the database.

Repository files:

```text
backend/src/main/java/com/smartlib/repository/BookRepository.java
backend/src/main/java/com/smartlib/repository/UserRepository.java
backend/src/main/java/com/smartlib/repository/IssueRepository.java
```

Spring Data JPA provides built-in CRUD methods such as:

- save
- findAll
- findById
- delete

## Service Layer

The service layer contains the business logic of the project.

Service files:

```text
backend/src/main/java/com/smartlib/service/BookService.java
backend/src/main/java/com/smartlib/service/UserService.java
backend/src/main/java/com/smartlib/service/IssueService.java
```

### BookService

Handles book-related logic such as adding books and retrieving book records.

### UserService

Handles user/member-related logic such as registering users and finding user records.

### IssueService

Handles issue and return book logic. It manages the connection between books and users during book issuing and returning.

## Database Relationship

The main relationship is:

```text
User + Book -> Issue Record
```

A user can issue a book, and the issue table stores the transaction details.

## Sarthak's Contribution

Sarthak contributed to the database and service layer documentation by explaining entities, repositories, services, and database relationships in the Smart Library System.
