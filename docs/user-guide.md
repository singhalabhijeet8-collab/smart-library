# User Guide

## Project Overview

Smart Library System is a Java-based library management application. It includes a Spring Boot backend, a Java Swing desktop application, a MySQL database, and an optional HTML frontend dashboard.

## Running The Backend

Open a terminal in the project root and run:

```powershell
cd backend
mvn clean package
java -jar target/smart-library-1.0.jar
The backend runs at:

http://localhost:8080
Running The Desktop Application
From the project root, run:

javac desktop\LibraryApp.java
java -cp desktop LibraryApp
Opening The Frontend Dashboard
Open this file in a browser:

frontend/index.html
You can also use VS Code Live Server to preview the frontend page.

Basic Usage Steps
Start the backend server.
Open the Swing desktop application.
Login or create an admin account.
Add books to the library catalog.
Register library users or members.
Issue a book using book ID and user ID.
Return a book using the issue ID.
View issue and return records.
Frontend Notes
The frontend dashboard provides a visual prototype of the Smart Library System. It represents how a web-based interface can be added in the future for easier access through a browser.

Kanika's Contribution
Kanika Tyagi contributed to the user guide, frontend usage notes, and project documentation for explaining how to run and use the Smart Library System.