.PHONY: setup

setup: 
	cp .env.example .env
	make start
start:
	docker compose -f docker-compose.yml up

stop:
	docker compose -f docker-compose.yml down
