# adaptTo OpenTelemetry Demo

This project is used as reference project for the adaptTo Observability presentation of 2024

```text
127.0.0.1 aem-author product-service signoz
```

## Installation

Included is a Spring-based application and an AEM-based project to showcase on how OpenTelemetry can help you in
connecting the dots between the different microservices in your platform.

Set up the OpenTelemetry Collector

```bash
docker compose -f generic-otel-collector/docker-compose.yaml up -d
```

Start the spring application:

```bash
mvn clean install spring-boot:run -f product-service/pom.xml
```

Place the cq-quickstart or aem-sdk jar in the `aem` folder and start AEM using the startup script that sets the params
to connect with the OpenTelemetry collector:

```bash
bash aem/aem-start.sh
```

Deploy the AEM project to your locally running AEM:

```bash
mvn clean install -PautoInstallSinglePackage -f aem-site/pom.xml
```

### Connecting to an APM tool

The most basic setup sends the traces and metrics to a output file in `generic-otel-collector/output`. One can connect
to their own APM toolings by updating exporters in `generic-otel-collector/otel-collector-config.yaml`

An example of an APM tool that can run self-hosted in linux and macos on docker is Signoz, a simple Signoz setup has
been added and can be run by using the following command **after bringing down the generic otel collector** :

```bash
docker compose -f signoz/docker-compose.yaml up -d
```

Alternatively, you can use the install script from their site if not all toolings are available on your local
machine: https://signoz.io/docs/install/docker/#install-signoz-using-the-install-script

Navigate to http://signoz:3301 to log into Signoz

## Usage

### Traces

To create a simple trace, connecting to only one service, one can use the Spring boot application:

http://product-service:8080/product/simple

To see how a errors would look like, one can use a malfunctioning product:

http://product-service:8080/product/uncaught-error

http://product-service:8080/product/caught-error

Calling a backend, in our example AEM, can be done with one of the following products:

http://product-service:8080/product/backend

http://product-service:8080/product/backend-error

**TODO:**

- Add test urls to README
- Update README flow

**Demo Flow**

- Show opentelemetry osgi wrappers and tell about the difficulties (uber jar vs single jars, netty vs okhttp, service loader, feature model it tests)
- Show aemaacs opentelemetry instrumentation (OpenTelemetryFactor, Filters, opentelemetry-okhttp-exporter package vs minimal)
- Show this project (AEM request of simple product, AEM request of error product, AEM request of backend product, Turn off spring boot application. Show in dashboards)
- Show this project (Enable component filter, show in dashboards)
