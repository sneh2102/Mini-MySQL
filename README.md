# MINI SQL Project

## Overview
This is a MINI SQL project implemented in Java, designed to mimic the functionality of MySQL. Instead of using a traditional database server, this project stores all data in files on the local filesystem. It supports querying data, user authentication, and transaction management, providing a lightweight alternative for database operations.

## Features
- **File-based Storage**: Data is persisted in files, allowing for easy backup and portability.
- **Authentication**: Secure login system with success and failure handling (e.g., `login-success`, `login-failed`).
- **SQL-like Queries**: Supports basic SQL commands such as:
  - `CREATE DATABASE` and `CREATE TABLE`
  - `INSERT`, `SELECT`, `UPDATE`, and `DELETE`
  - `SELECT` with column specification and `WHERE` clause
  - Transaction management with `TRANSACTION` and `TRANSACTION ROLLBACK`
- **Error Handling**: Detects and reports incorrect syntax (e.g., `incorrect-syntax`).
- **Logging**: Tracks operations and errors (e.g., `log`).
- **Captcha Integration**: Adds a layer of security during login (e.g., `captcha`).

## Screenshots
Below are screenshots demonstrating the functionality of the MINI SQL project:

### Captcha
![Captcha](images/captcha.png)
Displays the captcha verification during the login process.

### Create Database
![Create Database](images/create-database.png)
Shows the successful creation of a new database.

### Create Table
![Create Table](images/create-table.png)
Illustrates the creation of a new table within the database.

### Delete
![Delete](images/delete.png)
Demonstrates the deletion of data from a table.

### Incorrect Syntax
![Incorrect Syntax](images/incorrect-syntax.png)
Shows an example of error handling for invalid SQL syntax.

### Insert
![Insert](images/insert.png)
Depicts the insertion of new data into a table.

### Log
![Log](images/log.png)
Displays the logging of operations and errors.

### Login
![Login](images/login.png)
Shows the login interface with authentication.

### Login Failed
![Login Failed](images/login-failed.png)
Illustrates a failed login attempt.

### Login Success
![Login Success](images/login-success.png)
Confirms a successful login.

### Select
![Select](images/select.png)
Displays a basic `SELECT` query result.

### Select with Column
![Select with Column](images/select-with-column.png)
Shows a `SELECT` query with specific columns.

### Select with Where
![Select with Where](images/select-with-where.png)
Demonstrates a `SELECT` query with a `WHERE` clause.

### Transaction
![Transaction](images/transaction.png)
Illustrates the start of a transaction.

### Transaction Rollback
![Transaction Rollback](images/transaction-rollback.png)
Shows the rollback of a transaction.

### Update
![Update](images/update.png)
Depicts the updating of existing data.

### Use Database
![Use Database](images/use-database.png)
Shows the selection of a database for operations.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- An IDE (e.g., IntelliJ IDEA, Eclipse) or a command-line environment

### Installation
1. Clone the repository:
   ```bash
    git clone https://github.com/sneh2102/Mini-MySQL.git
    ```
2. Navigate to the project directory:
    ```bash
    cd mini-sql-project
    ```
3. Compile the Java files:
    ```bash
    javac src/*.java
    ```
4. Run the application:
    ```bash
    java -cp src Main
    ```

### Usage

- Start the application and use the command-line interface to execute SQL-like commands.
- Example commands:

  - `CREATE DATABASE mydb;`
  - `USE mydb;`
  - `CREATE TABLE users (id INT, name VARCHAR(50));`
  - `INSERT INTO users VALUES (1, 'John');`
  - `SELECT * FROM users;`
  - `SELECT * FROM users where id = 1;`
  - `SELECT * FROM users where id > 1;`
  - `SELECT * FROM users where id < 1;`
  - `SELCT name,id from users;`
  - `SELCT name,id from users where id = 6;`
  - `BEGIN TRANSACTION;`
  - `ROLLBACK;`