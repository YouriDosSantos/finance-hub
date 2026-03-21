# 📘 Finance Hub — Backend

A secure, role‑driven financial CRM backend built with **Spring Boot**, **Spring Security**, **OAuth2**, and a **custom JDBC persistence layer**. Designed for real‑world enterprise workflows: authentication, user management, financial data modeling, and scalable service boundaries.

---

## 🚀 Overview

Finance Hub is a backend platform that powers a financial CRM application. It provides:

- Robust authentication using OAuth2 Password Grant + JWT  
- Role‑based access control (Admin, Operator, NewUser)  
- Custom JDBC repositories for high‑performance, fine‑grained SQL control  
- User onboarding flow with password hashing, default roles, and secure registration  
- Modular service architecture for contacts, accounts, relationships, and dashboards  
- Production‑ready patterns: DTO mapping, layered architecture, exception handling, and transaction management  

This project reflects a backend engineer who understands both enterprise constraints and clean, maintainable design.

---

## 🧱 Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot |
| Security | Spring Security, OAuth2, JWT |
| Persistence | Custom JDBC Repositories, HikariCP |
| Database | MySQL |
| Build | Maven |
| Architecture | Layered (Controller → Service → Repository) |

---

## 🔐 Authentication & Authorization

- OAuth2 Password Grant flow  
- JWT‑based sessionless authentication  
- Custom `UserDetailsService` loading users + roles via JDBC  
- Role‑based method security using `@PreAuthorize`  
- Secure password hashing with `PasswordEncoder`  

---

## 🗄️ Persistence Layer

Unlike typical JPA/Hibernate apps, Finance Hub uses **hand‑written JDBC repositories** for:

- Full control over SQL  
- Predictable performance  
- Explicit transaction boundaries  
- Clear separation between domain and persistence  

Example features implemented via JDBC:

- User registration  
- Role assignment  
- Password updates  
- User/role joins  
- Contact, Relationship and financial data queries  

---

## 📂 Project Structure

**src/main/java/com/finance/hub**
- **controller/** — REST endpoints  
- **service/** — Business logic  
- **jdbcRepo/** — Custom JDBC repositories  
- **model/** — Domain models  
- **dataTransfer/** — DTOs  
- **security/** — OAuth2, JWT, RBAC  



---

## 🧪 Key Features

### **User Registration**
- Email validation  
- Password hashing  
- Default role assignment  
- Transaction‑safe JDBC insert  

### **Login / Token Issuance**
- OAuth2 token endpoint  
- JWT generation  
- Custom authentication provider  

### **User Management**
- Change password  
- Update profile  
- Role updates  
- Fetch authenticated user (`/me`)  

### **Financial CRM Modules**
- Contacts  
- Relationships  
- Financial accounts  
- Dashboard data  

---

## 🧭 Why This Project Matters

This backend demonstrates:

- Mastery of **Spring Security** beyond tutorials  
- Ability to build **enterprise‑grade authentication flows**  
- Comfort with **SQL, JDBC, and transaction management**  
- Clean separation of concerns and maintainable architecture  
- Real‑world problem solving: onboarding, RBAC, password resets, data modeling  
- Production‑ready engineering habits (logging, error handling, DTOs, services)  




