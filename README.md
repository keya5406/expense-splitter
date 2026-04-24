# Group Expense Splitter

A Splitwise-like web application built using:

* Java Servlets
* JSP
* JDBC
* MySQL
* Apache Tomcat

## Features

* User registration and login
* Create groups
* Join groups using group code
* Add expenses
* Equal expense splitting
* View balances
* Partial and full settlement

## Setup Instructions

Follow these steps to run the project locally.

---

## 1. Create Database

Open MySQL and create the database:

```sql
CREATE DATABASE expense_splitter;
```

Then create all required tables:

* users
* user_groups
* group_members
* expenses
* expense_splits
* settlements

---

## 2. Configure Database Connection

Open:

```text
src/util/DBConnection.java
```

Update database credentials:

```java
String url = "jdbc:mysql://localhost:3306/expense_splitter";
String user = "your_mysql_username";
String password = "your_mysql_password";
```

---

## 3. Compile Java Files

Compile the Java source files to generate `.class` files.

Example:

```bash
javac -d . src/controller/*.java src/util/*.java
```

You can also compile using your IDE if preferred.

---

## 4. Deploy to Apache Tomcat

Go to your Tomcat folder:

```text
apache-tomcat/webapps/
```

Create project folder:

```text
ExpenseSplitter/
```

Inside it maintain this structure:

```text
ExpenseSplitter/
│
├── jsp/
├── WEB-INF/
│   ├── web.xml
│   └── classes/
│       ├── controller/
│       └── util/
```

---

## 5. Copy Required Files

Copy the following:

### Compiled class files

Copy `.class` files into:

```text
ExpenseSplitter/WEB-INF/classes/
```

Keep package folders:

```text
controller/
util/
```

### JSP files

Copy all `.jsp` files into:

```text
ExpenseSplitter/jsp/
```

### web.xml

Copy:

```text
web.xml
```

into:

```text
ExpenseSplitter/WEB-INF/
```

---

## 6. Run Project

Start Tomcat.

Open browser:

```text
http://localhost:8080/ExpenseSplitter/
```

---

## Important

* Do not copy `.java` files into Tomcat
* Copy only:

  * `.class`
  * `.jsp`
  * `web.xml`
* Keep folder structure correct
* Restart Tomcat after changes if needed

---

## Workflow


1. Clone the repository
2. Create local database
3. Update DBConnection.java
4. Compile Java files
5. Copy files into Tomcat
6. Start Tomcat
7. Run project

---

## Future Improvement

This project can later be converted to Maven to avoid manual compilation and file copying.

