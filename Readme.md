## OAuth2 and OIDC
- OAuth2 is an authorization framework that enables applications to obtain limited access to user accounts on an HTTP service, such as Facebook, GitHub, and DigitalOcean. It works by delegating user authentication to the service that hosts the user account, and authorizing third-party applications to access the user account. OAuth2 provides authorization flows for web and desktop applications, and mobile devices.
### useful endpoints
- to get the configuration of the realm with all the endpoints `/realms/ebank-realm/.well-known/openid-configuration`

### Useful props
These two properties are crucial for configuring a Spring Boot application to work with OAuth 2.0 Resource Server authentication using JSON Web Tokens (JWT), specifically with Keycloak in this case. Let me break down the purpose of each property:

1. `spring.security.oauth2.resourceserver.jwt.issuer-uri`:
    - This property specifies the issuer of the JWT tokens.
    - In this case, it points to the Keycloak realm (ebank-realm) running on localhost:8080.
    - The issuer URI is used to validate the token's origin and ensure it was created by a trusted identity provider (Keycloak).
    - It helps in verifying the token's authenticity by checking that the token was issued by the expected authorization server.

2. `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`:
    - This property provides the URI to the JSON Web Key Set (JWKS) endpoint.
    - The JWKS is a set of public keys used to verify the signature of the JWT tokens.
    - When a resource server receives a JWT, it needs to verify the token's signature to ensure it hasn't been tampered with.
    - This URI allows the Spring Security to fetch the public keys dynamically, which can be used to validate the token's signature.

Here's a practical example of how these work together:

1. When a client authenticates with Keycloak, it receives a JWT token.
2. When this token is used to access a resource server (your Spring Boot application):
    - The server checks the issuer (issuer-uri) to confirm the token came from the expected Keycloak realm.
    - It then uses the JWKS endpoint (jwk-set-uri) to retrieve the public keys needed to verify the token's signature.

This configuration ensures:
- Token origin authentication
- Token signature validation
- Secure communication between the client, authorization server (Keycloak), and resource server (your application)

Without these properties, your Spring Boot application wouldn't know how to validate the JWT tokens received from Keycloak, potentially leaving your application vulnerable to unauthorized access.


### Keycloak roles not being recognized by Spring Security
When using Keycloak with Spring Security, you might encounter issues where the roles assigned to users in Keycloak are not being recognized by Spring Security. This is happening due to the way roles are mapped between Keycloak and Spring Security.
For that the `JwtAuthConverter` is used to customize the extraction of roles from the JWT token. By default, Spring Security only considers roles present in the `scope` claim of the JWT. However, Keycloak adds roles in the `realm_access` and `resource_access` claims.

The `JwtAuthConverter` allows you to extract these additional roles from the `realm_access` claim and include them in the authorities granted to the authenticated user. This ensures that all relevant roles defined in Keycloak are considered during authorization checks in your Spring Boot application.

Here is a brief explanation of the `JwtAuthConverter`:

- It implements the `Converter<Jwt, AbstractAuthenticationToken>` interface to convert a `Jwt` token into an `AbstractAuthenticationToken`.
- It uses the `JwtGrantedAuthoritiesConverter` to extract the default authorities.
- It extracts additional roles from the `realm_access` claim and combines them with the default authorities.
- It returns a `JwtAuthenticationToken` with the combined authorities and the preferred username.

This customization ensures that all roles defined in Keycloak are taken into account for authorization decisions in your application.

## Test the application
To test the Boot application with Keycloak, follow these steps:

### Prerequisites
1. **Keycloak Server**: Ensure you have a running Keycloak server with a realm and client configured.
2. **Spring Boot Application**: Your Spring Boot application should be configured to use Keycloak for authentication and authorization.

### Step-by-Step Guide

#### 1. Start Keycloak Server
Make sure your Keycloak server is running. You can start it using Docker:

```sh
docker run -p 8080:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.0.6 start-dev
```

#### 2. Configure Keycloak Realm and Client
1. **Create a Realm**: Go to the Keycloak admin console and create a new realm (e.g., `ebank-realm`).
2. **Create a Client**: In the new realm, create a client (e.g., `ebank-client`) with the following settings:
   - **Client ID**: `ebank-client`
   - **Valid Redirect URIs**: `http://localhost:8080/*` or just leave it empty

3. **Create Roles and Users**: Define roles and create users with those roles in the realm.

#### 3. Configure Spring Boot Application
Ensure your `application.properties` or `application.yml` is configured with Keycloak properties:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/ebank-realm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/ebank-realm/protocol/openid-connect/certs
```

#### 4. Obtain Access Token
Use the following HTTP request to obtain an access token:

```http
POST http://localhost:8080/realms/ebank-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&client_id=ebank-client&username=user1&password=12345612
```

#### 5. Test API Endpoints
Use the obtained access token to test your API endpoints. For example:

```http
GET http://localhost:8087/api/customers/
Authorization: Bearer <access_token>
```

Replace `<access_token>` with the actual token received from the previous step."# keycloak-OIDC-Demo" 
