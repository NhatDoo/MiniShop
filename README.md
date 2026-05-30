# Pixel Shop Backend

MiniShop is a Spring Boot REST API for an e-commerce workflow. It supports account registration, email/password login, Google OIDC login, JWT access tokens, refresh-token cookies, product management, cart checkout, order history, MinIO image upload, and VNPay sandbox payment.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- MinIO object storage
- VNPay payment gateway
- Maven Wrapper
- OpenAPI / Swagger UI

## Features

- Register and login with email/password.
- Login with a Google ID token.
- Issue short-lived JWT access tokens and refresh tokens stored in HTTP-only cookies.
- Read current authenticated user profile.
- Manage products with optional image upload to MinIO.
- Add, update, and remove cart items.
- Checkout cart into an order.
- View and soft-delete user orders.
- Create VNPay payment URLs and handle VNPay return callbacks.
- Browse API documentation through Swagger UI.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/NhatDoo/Pixel_shop.git
cd Pixel_shop
```

### 2. Configure environment variables

Create a local `.env` file and fill it with your PostgreSQL, JWT, Google OAuth, MinIO, and VNPay values.

Required groups:

```properties
DB_URL=jdbc:postgresql://localhost:5432/tmdt
DB_USERNAME=postgres
DB_PASSWORD=your-database-password

JWT_SECRET=your-long-random-jwt-secret-at-least-64-bytes

GOOGLE_OAUTH_CLIENT_ID=your-google-oauth-client-id.apps.googleusercontent.com

MINIO_ENDPOINT=http://localhost:9000
MINIO_PUBLIC_URL=http://localhost:9000
MINIO_ACCESS_KEY=admin
MINIO_SECRET_KEY=password123
MINIO_BUCKET_NAME=hinhanh

VNPAY_TMN_CODE=your-vnpay-tmn-code
VNPAY_HASH_SECRET=your-vnpay-hash-secret
VNPAY_PAYMENT_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=http://localhost:8080/api/thanhtoan/vnpay/return
```

Important: `.env` is ignored by Git. Do not commit real passwords, JWT secrets, OAuth credentials, MinIO credentials, or VNPay production credentials.

### 3. Prepare PostgreSQL

Create a PostgreSQL database matching `DB_URL`. The default example expects:

```text
database: tmdt
host: localhost
port: 5432
```

### 4. Prepare MinIO

Run a local MinIO server and create the bucket configured by `MINIO_BUCKET_NAME`.

Example local values:

```text
endpoint: http://localhost:9000
bucket: hinhanh
```

The repository ignores `minio-data/` because it is local object-storage data.

### 5. Run the application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

The API starts at:

```text
http://localhost:8080
```

## API Documentation

Swagger UI is available after the app starts:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Main Endpoints

### Auth

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | Login with email/password |
| `POST` | `/auth/google` | Login with Google ID token |
| `POST` | `/auth/refresh` | Refresh access token using refresh cookie |
| `POST` | `/auth/logout` | Logout current session |
| `GET` | `/api/users/me` | Get current authenticated user |

### Products

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/sanpham` | Get all products |
| `GET` | `/api/sanpham/{id}` | Get product by ID |
| `POST` | `/api/sanpham` | Create product as admin, JSON or multipart |
| `PUT` | `/api/sanpham/{id}` | Update product as admin, JSON or multipart |
| `DELETE` | `/api/sanpham/{id}` | Delete product as admin |

### Cart

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/giohang` | Get current user's cart |
| `POST` | `/api/giohang/items` | Add item to cart |
| `PUT` | `/api/giohang/items/{itemId}` | Update cart item quantity |
| `DELETE` | `/api/giohang/items/{itemId}` | Remove item from cart |

### Orders

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/donhang/checkout` | Checkout current user's cart |
| `GET` | `/api/donhang` | Get current user's orders |
| `GET` | `/api/donhang/{orderId}` | Get current user's order by ID |
| `DELETE` | `/api/donhang/{orderId}` | Soft-delete current user's order |

### Payments

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/thanhtoan/vnpay/{donHangId}` | Create VNPay payment URL for an order |
| `GET` | `/api/thanhtoan/vnpay/return` | Handle VNPay return URL |

## Google Auth Test Page

A simple local test page is included:

```text
http://localhost:8080/google-auth-test.html
```

Enter your Google OAuth client ID from `.env`, sign in, exchange the Google ID token with `/auth/google`, then call `/api/users/me`.

## Running Tests

On Windows:

```powershell
.\mvnw.cmd test
```

On macOS/Linux:

```bash
./mvnw test
```

## Repository Safety

This repo does not track environment files. The real `.env`, build output, IDE files, Maven `target/` folder, and local `minio-data/` folder are ignored.

For a deeper architecture overview, see [DESIGN.md](DESIGN.md).
