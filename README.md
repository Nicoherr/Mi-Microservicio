# Product Service Microservice (Base DevOps Project)

Microservicio base desarrollado en **Kotlin** con **Spring Boot 3** para la gestión de catálogo de productos. Diseñado como plantilla de grado producción para pipelines de Ingeniería DevOps (CI/CD, Calidad de Código, Contenerización, Orquestación y Observabilidad).

---

## 🏗 Arquitectura del Proyecto

```
product-service/
├── .github/
│   └── workflows/
│       └── ci-cd-pipeline.yml         # Pipeline CI/CD automatizado en GitHub Actions
├── k8s/
│   ├── deployment.yaml                # Manifiesto Deployment con Probes y Límites de Recursos
│   └── service.yaml                   # Manifiesto Service (ClusterIP)
├── src/
│   ├── main/
│   │   ├── kotlin/com/example/productservice/
│   │   │   ├── controller/            # Capa de Entrada (REST API)
│   │   │   ├── dto/                   # Data Transfer Objects & Validaciones
│   │   │   ├── exception/             # Manejo Global de Excepciones HTTP
│   │   │   ├── metrics/               # Métricas personalizadas Micrometer / Prometheus
│   │   │   ├── model/                 # Entidades JPA / Dominio
│   │   │   ├── repository/            # Capa de Acceso a Datos (Spring Data JPA)
│   │   │   ├── service/               # Capa de Lógica de Negocio
│   │   │   └── ProductServiceApplication.kt
│   │   └── resources/
│   │       ├── application.yml        # Configuración Spring Boot & Actuator
│   │       └── logback-spring.xml     # Logging Estructurado JSON
│   └── test/
│       └── kotlin/com/example/productservice/
│           └── service/
│               └── ProductServiceTest.kt  # Pruebas Unitarias (JUnit 5 + MockK)
├── .gitignore
├── Dockerfile                         # Multi-stage build optimizado (non-root)
├── docker-compose.yml                 # Entorno local (App + Prometheus)
├── build.gradle.kts                   # Dependencias y plugin JaCoCo
├── prometheus.yml                     # Configuración de Scraping para Prometheus
├── README.md                          # Documentación del proyecto
├── settings.gradle.kts
└── sonar-project.properties           # Parámetros para SonarQube & Quality Gate
```

---

## 🌿 Estrategia de Ramificación (GitFlow)

El flujo de trabajo de control de versiones sigue una adaptación de **GitFlow**:

- **`main`**: Rama de código listo para producción. Protegida. Solo recibe merges vía Pull Request desde `develop` o `hotfix/*`.
- **`develop`**: Rama principal de integración continua. Todo desarrollo completado se integra aquí.
- **`feature/*`**: Ramas de características individuales (`feature/add-category-field`). Nacen de `develop` y se fusionan mediante PR a `develop`.
- **`bugfix/*` / `hotfix/*`**: Ramas para corrección de errores.

---

## 📝 Convenciones de Commits (Conventional Commits)

Formatos de mensaje de commit requeridos:
- `feat: <descripción>` -> Nueva funcionalidad.
- `fix: <descripción>` -> Corrección de un error.
- `test: <descripción>` -> Añadir o modificar pruebas.
- `docs: <descripción>` -> Cambios en documentación.
- `ci: <descripción>` -> Cambios en configuración de CI/CD (GitHub Actions, Docker, K8s).
- `refactor: <descripción>` -> Refactorización de código sin cambio de comportamiento.

Ejemplo: `feat(product): agregar metrica personalizada para productos creados`

---

## 🚀 Pipeline CI/CD (Arquitectura)

El pipeline configurado en `.github/workflows/ci-cd-pipeline.yml` se ejecuta en `push` a `develop` y `pull_request` a `main`:

1. **Build & Test Unitarios**: Compila y ejecuta los tests unitarios en Kotlin con JUnit 5 y MockK.
2. **Reporte JaCoCo**: Genera métricas de cobertura de código (`jacocoTestReport.xml`).
3. **Auditoría SonarQube**: Escanea calidad del código y detiene el build si falla el Quality Gate.
4. **Build Imagen Docker**: Construye la imagen multi-stage utilizando la app previamente probada.

---

## 📊 Cobertura y Auditoría de Calidad (JaCoCo & SonarQube)

Para ejecutar los tests y generar el reporte de cobertura JaCoCo localmente:
```bash
./gradlew clean test jacocoTestReport
```
El reporte XML se genera en: `build/reports/jacoco/test/jacocoTestReport.xml`.

Para ejecutar el análisis local con SonarQube:
```bash
./gradlew sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=TU_TOKEN
```

---

## 🐳 Despliegue Local con Docker & Compose

Levantar el microservicio junto con Prometheus en local:
```bash
docker-compose up --build -d
```
Endpoints locales disponibles:
- **API REST**: `http://localhost:8080/api/v1/products`
- **Consola H2**: `http://localhost:8080/h2-console`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Métricas Prometheus**: `http://localhost:8080/actuator/prometheus`
- **Prometheus Dashboard**: `http://localhost:9090`

---

## ☸️ Orquestación con Kubernetes

Para desplegar en un clúster de Kubernetes (Minikube / Kind / EKS / GKE):
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```
