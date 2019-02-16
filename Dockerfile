FROM adoptopenjdk/openjdk10:latest
VOLUME /tmp
ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
RUN echo "127.0.0.1 $HOSTNAME" >> /etc/hosts
ENTRYPOINT java -DAPPID=$APPID -cp app:app/lib/* io.whatstheweatherlike.weather_service.WeatherServiceApplication