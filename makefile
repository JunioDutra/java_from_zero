build-app:
	docker compose build

run-app:
	docker compose up --build

run-simple-test:
	ab -n 10000 -c 10 http://localhost:9999/clientes/1/extrato