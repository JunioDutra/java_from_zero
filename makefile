build-app:
	docker compose build

run-app:
	docker compose up --build -d

run-app-native:
	docker compose up --build -d

run-simple-test:
	ab -n 1000 -c 100 http://localhost:8080/user/

build-local-native-image-to-alpine:
	docker run --rm --workdir /opt --name builder -it -v ./build/jar/:/opt ghcr.io/graalvm/native-image-community:21 --static -jar /opt/App.jar \
	--no-fallback --strict-image-heap -march=native --report-unsupported-elements-at-runtime

run-in-alpine:
	docker run --rm --workdir /opt --name runner -it -v ./build/jar/App:/opt/App alpine:latest ./App

run-db:
	docker compose up db --build -d

clean-db:
	docker compose down
	docker volume rm java-chall_postgres_data

run-test-gatling:
	docker compose down; \
	docker volume rm java-chall_postgres_data; \
	docker compose up --build --force-recreate -d; \
	./executar-teste-local.sh