COMPOSE_FILE=./resource/docker-compose.yml
PROJECT=java-chall

build-app:
	docker compose -p $(PROJECT) -f $(COMPOSE_FILE) build

run-app:
	docker compose -p $(PROJECT) -f $(COMPOSE_FILE) up --build --force-recreate -d

run-app-java:
	docker compose -p $(PROJECT) -f $(COMPOSE_FILE) --profile java up --build app-java nginx

run-simple-test:
	ab -n 1000 -c 100 http://localhost:8080/user/

build-local-native-image-to-alpine:
	docker run --rm --workdir /opt --name builder -it -v ./build/jar/:/opt ghcr.io/graalvm/native-image-community:21 --static -jar /opt/App.jar \
	--no-fallback --strict-image-heap -march=native --report-unsupported-elements-at-runtime

run-in-alpine:
	docker run --rm --workdir /opt --name runner -it -v ./build/jar/App:/opt/App alpine:latest ./App

run-db:
	docker compose -p $(PROJECT) -f $(COMPOSE_FILE) up db --build -d

clean-db:
	docker compose -p $(PROJECT) -f $(COMPOSE_FILE) down
	docker volume rm java-chall_postgres_data

run-test-gatling:
	docker compose -p $(PROJECT) -f $(COMPOSE_FILE) down; \
	docker volume rm java-chall_postgres_data; \
	docker compose -p $(PROJECT) -f $(COMPOSE_FILE) up --build --force-recreate -d; \
	./test/executar-teste-local.sh

publish:
	docker tag java-chall-app ghcr.io/juniodutra/java_from_zero:latest
	docker push ghcr.io/juniodutra/java_from_zero:latest