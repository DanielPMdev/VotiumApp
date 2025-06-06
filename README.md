# 🗳️ Votium App – Plataforma de Votaciones Inteligente

**Votium App** es una solución completa de votaciones online que incluye una **API REST segura** y una **interfaz web con Spring Boot y Thymeleaf**. Diseñada para gestionar eventos, mercados y opciones de votación, la plataforma permite a los usuarios participar de forma segura mediante autenticación JWT.

---

## 🚀 Características Principales

- 👥 **Gestión de Usuarios**: Registro, login y roles (ADMIN, USER).
- 📅 **Eventos Personalizados**: Los administradores crean eventos con su respectiva categoría.
- 🧩 **Mercados y Opciones**: Cada evento contiene uno o más mercados, y cada mercado tiene múltiples opciones de voto.
- 🗳️ **Sistema de Votación Único**: Cada usuario puede emitir un voto por mercado.
- 🔐 **Autenticación Segura con JWT**.
- 🌐 **Interfaz Web Interactiva**: Acceso al frontend web desarrollado con Thymeleaf + Bootstrap.

---

## 🛠️ Tecnologías Utilizadas

| API (Backend)        | Web (Frontend)     | Seguridad           | Persistencia |
|----------------------|--------------------|---------------------|--------------|
| Spring Boot          | Thymeleaf          | Spring Security + JWT | Oracle DB   |
| Spring Data JPA      | Bootstrap 5        | Token Bearer        |              |
| REST Controllers     | JavaScript         | Roles: ADMIN, USER  |              |

---

## 📦 Requisitos Previos

- ☕ **Java 21+**
- 🧰 **Maven**
- 🗄️ **Oracle Database**
- 🔐 Archivo `jwt-secret.txt` con la clave secreta
- 🧾 Archivo `application-secret.properties` con configuración sensible (DB, credenciales...)

---

## 📁 Estructura del Proyecto

```bash
Votium/
├── VotiumAPI/         # API REST
├── VotiumWeb/         # Aplicación Webºº
```

---

## ⚙️ Configuración Inicial

### 1️⃣ Clonar el Repositorio

```bash
git clone https://github.com/DanielPMdev/VotiumApp.git
cd VotiumApp
```

---

### 2️⃣ Crear archivos de configuración requeridos

#### 📄 `application-secret.properties`

Ubicación:  
`votium-api/src/main/resources/application-secret.properties`

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=usuario
spring.datasource.password=contraseña
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

#### 🔐 `jwt-secret.txt`

Ubicación esperada:  
`votium-api/src/main/resources/jwt-secret.txt`

Contenido:
```
mi-clave-super-secreta
```

---

## 🚀 Ejecución del Proyecto

### ▶️ 1. Ejecutar la API

```bash
cd VotiumAPI
```

#### 🛠️ Editar `application.properties`

Archivo:  
`src/main/resources/application.properties`

```properties
spring.config.import=application-secret.properties
jwt.secret-file=classpath:jwt-secret.txt
server.port=8080
```

📥 Ejecutar API:

```bash
mvn spring-boot:run
```

API disponible en:  
`http://localhost:8080/api`

---

### 🌐 2. Ejecutar la Web

```bash
cd ../VotiumWeb
```

#### 🛠️ Editar `application.properties`

Archivo:  
`src/main/resources/application.properties`

```properties
api.base.url=http://localhost:8080/api
server.port=9000
```

▶️ Ejecutar Web:

```bash
mvn spring-boot:run
```

🌍 Abrir en navegador:  
`http://localhost:9000`

---

## 🔓 Endpoints Públicos

- `/login` – Autenticación de usuario
- `/register` – Registro de nuevo usuario
- `/` – Página de inicio
- `/portfolio` – Página personal

---

## 🧪 Pruebas

Se recomienda usar Postman o Swagger (si habilitado) para probar los endpoints privados.  

Recuerda añadir el token JWT en la cabecera:  
```
Authorization: Bearer <token>
```

---

## 🤝 Contribuciones

¡Se agradecen sugerencias, mejoras y reportes de errores!

---

## 🏆 Créditos

- 💡 Desarrollado por [Daniel Porras Morales](https://github.com/DanielPMdev)  
- 🎓 Proyecto Final de Ciclo de **Desarrollo de Aplicaciones Multiplataforma (DAM)**

---

## 📌 Etiquetas

`#SpringBoot` `#Thymeleaf` `#JWT` `#API` `#VotiumApp` `#Votaciones`
