# adaptTo OpenTelemetry Demo

This project is used as reference project for the adaptTo Observability presentation of 2024.

## Installation

Included is a SpringBoot service that handles product data api calls and an AEM project to showcase on how OpenTelemetry can help you in
connecting the dots between the different microservices in your platform.

To get started you can start the OpenTelemetry collector. 
There are two variants, a generic one that will log all the telemetry data to a logfile and a second one which uses the datadog exporter.

To startup the collector you can use docker compose, if you need the generic one you should drop `otel-collector-datadog` from the command.

```bash
docker compose -f otel-collector/docker-compose.yaml up otel-collector-datadog
```

When the collector is setup you can boot the product service

```bash
mvn clean install spring-boot:run -f product-service/pom.xml
```

Place the AEMaaCS SDK jar in the `aem` folder and start AEM using the startup script that sets the params
to connect with the OpenTelemetry collector. The script assumes the jar filename matches the pattern `aem-*.jar`.

```bash
bash aem/aem-start.sh
```

Once all services are running you can deploy the demo project to AEM:

```bash
mvn clean install -PautoInstallSinglePackage -f aem-site/pom.xml
```
## Usage

### Traces

To create a simple trace, connecting to only one service, one can use the Spring boot application:

http://localhost:8080/product/simple

To see how a errors would look like, one can use a malfunctioning product:

http://localhost:8080/product/uncaught-error

http://localhost:8080/product/caught-error

Calling a backend, in our example AEM, can be done with one of the following products:

http://localhost:8080/product/backend

http://localhost:8080/product/backend-error

### Connecting to an APM tool

The most basic setup sends the traces and metrics to a output file in `otel-collector/output`. One can connect
to their own APM toolings by updating exporters in `otel-collector/otel-collector-config.yaml`

An example of an APM tool that can run self-hosted in linux and macOS on Docker is SigNoz, a simple Signoz setup has
been added and can be run by using the following command **after bringing down the generic otel collector** :

```bash
docker compose -f signoz/docker-compose.yaml up -d
```

Alternatively, you can use the install script from their site if not all toolings are available on your local
machine: https://signoz.io/docs/install/docker/#install-signoz-using-the-install-script

Navigate to http://localhost:3301 to log into SigNoz

**Demo Flow**

- Show opentelemetry osgi wrappers and tell about the difficulties (uber jar vs single jars, netty vs okhttp, service loader, feature model it tests)
- Show aemaacs opentelemetry instrumentation (OpenTelemetryFactor, Filters, opentelemetry-okhttp-exporter package vs minimal)
- Show this project (AEM request of simple product, AEM request of error product, AEM request of backend product, Turn off spring boot application. Show in dashboards)
- Show this project (Enable component filter, show in dashboards)
