#!/bin/bash
.DEFAULT_GOAL := dev
SHELL:=/bin/bash

dev:
	superset runserver -d

build:
	. ./bin/bootstrap.sh $(shell pwd)

deploy:
	. ./bin/deploy_testing.sh

clean:
	docker stop superset-db
	rm -rf mysql_data

demo:
	docker build -f bin/Dockerfile_supersetdemo -t superset:demo bin
	docker run -d -p 8099:8099 --name superset-demo superset:demo

sitemap:
	tree -L 4 -I "*.pyc|node_modules|dist" superset/ > sitemap.log
