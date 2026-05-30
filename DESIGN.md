# Pixel Shop Backend Design

This document summarizes the current backend design for Pixel Shop. The application follows a layered Spring Boot architecture with controllers for HTTP entry points, services for business rules, repositories for persistence, mappers for response shaping, and common infrastructure for security, configuration, and exception handling.

## Goals

- Provide a REST API for a small e-commerce backend.
- Keep authentication stateless for API calls with JWT access tokens.
- Store refresh-token sessions server-side so logout and token rotation can be controlled.
- Keep product images outside the database by storing them in MinIO.
- Support a VNPay sandbox payment flow for checked-out orders.
- Expose OpenAPI documentation for frontend and testing workflows.

## Architecture

```text
Client
  |
  v
Controller layer
  |
  v
Service layer
  |
  v
Repository layer
  |
  v
PostgreSQL

Product image upload
  |
  v
MinIO

Payment creation / return
  |
  v
VNPay
```

## Package Responsibilities

| Package | Responsibility |
| --- | --- |
| `controller` | Defines REST endpoints and request/response boundaries |
| `service` | Contains business workflows such as auth, cart, checkout, upload, and payment |
| `repository` | Provides Spring Data JPA access to PostgreSQL |
| `model` | Defines persistence entities |
| `dto.request` | Defines inbound request contracts |
| `dto.response` | Defines outbound response contracts |
| `mapper` | Converts entities to response DTOs |
| `common.config` | Holds Spring, OpenAPI, MinIO, VNPay, CORS, and security configuration |
| `common.security` | Handles JWT authentication, current-user resolution, refresh cookies, and role checks |
| `common.exception` | Centralizes application error codes and exception responses |
| `enums` | Stores domain enums such as role, order status, and payment status |

## Domain Model

Core entities:

- `User`: application account with email, password hash, profile data, and role.
- `Session`: refresh-token session used for remember-me, refresh, and logout.
- `SanPham`: product information and optional image URL.
- `GioHang`: user cart.
- `ChiTietGioHang`: cart item linking a cart to a product and quantity.
- `DonHang`: checkout order owned by a user.
- `ChiTietDonHang`: immutable order item snapshot.
- `ThanhToan`: payment record for an order.
- `ThanhToanVNPay`: VNPay-specific payment details and transaction data.

The cart is mutable. Checkout converts the current cart contents into order and order-item records, then payment can be created for the order.

## Authentication And Authorization

The app supports two login paths:

- Email/password login through `/auth/login`.
- Google OIDC ID token login through `/auth/google`.

Successful login returns a JWT access token in the response body and writes a refresh token into an HTTP-only cookie. API requests use the access token through the `Authorization: Bearer <token>` header.

Security flow:

1. `JwtAuthenticationFilter` reads and validates the bearer token.
2. The authenticated principal is stored as `UserPrincipal`.
3. Controllers can read the current user with `@CurrentUser`.
4. `@PreAuthorize` protects authenticated endpoints.
5. `@RequiredRole(Role.ADMIN)` protects admin product mutations through `RequiredRoleInterceptor`.

Refresh tokens are stored as sessions, so they can be rotated or revoked. Logout revokes the current session and clears the refresh-token cookie.

## Product And Image Flow

Product endpoints support both JSON and multipart requests:

- JSON is used when product data has no uploaded file.
- Multipart is used when a product image is uploaded with the product data.

`MinioService` uploads product images to the configured bucket and returns a public image URL. The application stores the URL on the `SanPham` entity instead of storing image bytes in PostgreSQL.

## Cart And Checkout Flow

1. The user adds products to `/api/giohang/items`.
2. The cart service creates the cart if it does not already exist.
3. Quantity updates and removals are scoped to the authenticated user's cart.
4. Checkout through `/api/donhang/checkout` creates an order from the cart.
5. The user can list, view, and soft-delete their own orders.

## VNPay Payment Flow

1. The user checks out a cart and receives a `DonHang`.
2. The frontend calls `/api/thanhtoan/vnpay/{donHangId}`.
3. `VNPayService` validates order ownership and builds a signed VNPay payment URL.
4. The user completes payment on VNPay.
5. VNPay redirects to `/api/thanhtoan/vnpay/return`.
6. The service verifies the response signature and updates payment status.

VNPay values are configured through environment variables so sandbox and production values can be separated.

## Configuration

`application.yaml` reads values from environment variables. Important groups:

- `spring.datasource`: PostgreSQL connection.
- `jwt`: JWT signing secret.
- `google.oauth`: Google client ID.
- `spring.servlet.multipart`: upload size limits.
- `minio`: object-storage endpoint, credentials, bucket, and public URL.
- `vnpay`: VNPay merchant and payment parameters.

Use `.env.example` as a local template and keep real `.env` files out of Git.

## Error Handling

Application errors are represented by `ErrorCode` and converted by `GlobalExceptionHandler` into consistent API responses. This keeps controller code focused on request handling while service methods can fail with domain-specific exceptions.

## Data And Secret Safety

- `.env` and `.env.*` are ignored except `.env.example`.
- `minio-data/` is ignored because it contains local object-storage files.
- `target/` and IDE folders are ignored.
- Production secrets should never be added to source control.

## Future Improvements

- Add integration tests for auth, cart checkout, and VNPay return handling.
- Add database migrations with Flyway or Liquibase.
- Add admin order-management endpoints.
- Add pagination and filtering for products and order lists.
- Add centralized audit fields such as created date and updated date.
