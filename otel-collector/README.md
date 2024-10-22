# OpenTelemetry Collector

Collector used in the demo for showcasing how to use the opentelemetry collector to connect to your own APM systems

## Usage

Start up the collector in your terminal:

```bash
docker compose up
```

By default, it will send the traces and metrics to the console and to an file. Extra `exporters` can be added in the `otel-collector-config.yaml` to connect to your own APM tools like Datadog, Elastic APM, Dynatrace, ...

### Creating test metrics and traces

To send test metrics and traces, one can use the `telemetrygen` from OpenTelemetry: https://github.com/open-telemetry/opentelemetry-collector-contrib/tree/main/cmd/telemetrygen