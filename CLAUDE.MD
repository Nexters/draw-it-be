# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot API for a drawing-based rolling paper service called "Draw It". Users can create rolling papers, share links for others to contribute drawings, and export the final composition as an image. The service supports OAuth authentication (Kakao, Facebook, Google) and AWS S3 for image storage.

## Build Commands

### Development
- `./gradlew bootRun` - Run the application locally
- `./gradlew test` - Run all tests
- `./gradlew check` - Run all verification tasks (tests + static analysis)
- `docker-compose up` - Start MySQL database for local development

### Docker
- `./gradlew jib` - Build and create Docker image using Jib
- `docker-compose -f docker-compose-deploy.yml up` - Deploy with production configuration

## Architecture

### Vertical Slice Architecture
- Each feature is organized as a vertical slice containing all layers
- Controllers and event listeners are co-located with application logic
- Repository interfaces are defined in domain layer with implementations in infrastructure layer
- JpaRepository is not used directly as the repository interface

### Module Structure
```
src/main/kotlin/com/draw/it/api/
├── auth/           # JWT and OAuth authentication
├── user/           # User management
├── project/        # Rolling paper projects
├── doodle/         # Drawing/doodle management
├── completedproject/ # Generated realistic images
├── common/         # Shared DTOs and exceptions
├── global/         # Configuration and filters
└── external/       # External service integrations
```

### Repository Pattern
- Domain repositories are interfaces (e.g., `ProjectRepository`)
- Infrastructure implementations use JPA repositories (e.g., `ProjectRepositoryImpl` uses `ProjectJpaRepository`)
- Soft deletes are implemented using `@SQLDelete` and `@SQLRestriction`

## Testing

### Test Structure
- Use `@IntegrationTest` annotation for application layer tests
- Integration tests use real repository implementations (no mocking)
- Test files include `.approved.txt` files for approval testing
- Test data setup uses SQL files in `src/test/resources/`

### Running Tests
- `./gradlew test` - All tests
- `./gradlew test --tests "ClassName"` - Specific test class
- `./gradlew test --tests "ClassName.methodName"` - Specific test method

## Code Guidelines

### Kotlin Style
- Use Kotlin idioms and data classes
- Leverage companion objects for factory methods
- Use extension functions where appropriate
- Follow Kotlin naming conventions

### Entity Design
- All entities extend `BaseEntity` for audit fields
- Use `@Comment` annotations for database documentation
- Implement soft deletes with `@SQLDelete` and `@SQLRestriction`
- Domain validation through entity methods (e.g., `validateOwnership`)

### Development Workflow
1. Read functional specification (`functional-specification.md`)
2. Write integration tests using `@IntegrationTest`
3. Implement minimal code to pass tests
4. Follow TDD approach for each feature

## Key Dependencies
- Spring Boot 3.4.1 with Kotlin
- Spring Security with JWT authentication
- Spring Data JPA with MySQL
- AWS SDK for S3 integration
- SpringDoc OpenAPI for API documentation
- MockK for testing

### 아키텍처
1. Vertical Slice Architecture 형태로 개발한다
   - Controller, EventListener 등은 Application Layer 의 코드에 함꼐 작성된다
   - Repository 는 인터페이스를 두고 구현체를 Infrastructure Layer 에 작성한다
   - JpaRepository 를 Repository Interface 로서 직접적으로 사용하지 않는다
2. 각 기능별로 모듈을 나누어 개발한다
   - 각 모듈은 독립적으로 개발되며, 다른 모듈과의 의존성을 최소화한다
   - 모듈 간의 통신은 Application Service 의 인터페이스를 통하거나, Event 로 이루어진다.

### 테스트 작성 가이드라인
1. 각 기능에 대한 테스트 케이스를 작성한다
   - 테스트는 JUnit5 를 사용하여 작성한다
   - 각 테스트는 독립적으로 실행 가능해야 하며, 다른 테스트에 영향을 주지 않아야 한다
2. Application Layer 의 코드에 대한 테스트는 Integration Test 로 작성한다
   - Repository 의 구현체를 Mocking 하지 않고 실제 구현체를 사용하여 테스트한다
   - 각 테스트는 필요한 데이터베이스 상태를 설정하고, 테스트 후에는 데이터베이스 상태를 초기화한다

### 코드 작성 가이드라인
1. Kotlin 스러운 코드를 작성한다

### 작업절차
1. 요구사항에 대한 이해
   - 기능 명세서를 읽고, 요구사항을 파악한다.
   - 필요한 경우, 추가적인 질문을 통해 요구사항을 명확히 한다.
2. 테스트 작성
   - 각 기능에 대한 테스트 케이스를 작성한다.
   - 테스트 작성 방식은 '테스트 작성 가이드라인'에 따른다.
3. 테스트 케이스를 통과하기 위한 코드를 작성한다.
    - 각 테스트 케이스에 대해 최소한의 코드를 작성하여 테스트를 통과하도록 한다.