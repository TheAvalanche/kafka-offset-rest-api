# kafka-offset-rest-api

Small REST service for Kafka consumer lag monitoring

## API
### /
Returns 'OK' for health check
### /lags
Returns current lag for each topic and consumer. If lag is > 0, that means, some messages might be stuck or consumer is down.
### /lags/filtered
Returns current non-zero lag for each topic and consumer. 

## How to run?
```
gradlew clean build
docker build -t kafka-rest .
```
