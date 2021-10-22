API_URL = http://localhost:8080

create:
	@curl -X POST --silent --verbose \
		--header 'Content-Type: application/json' \
		--data '{"name": "John Smith", "age": 45}' \
		$(API_URL)/users | jq

find-all:
	@curl -X GET --silent --verbose \
		--header 'Content-Type: application/json' \
		$(API_URL)/users | jq

find-one:
	@curl -X GET --silent --verbose \
		--header 'Content-Type: application/json' \
		$(API_URL)/users/$(ID) | jq

delete:
	@curl -X DELETE --silent --verbose \
		--header 'Content-Type: application/json' \
		$(API_URL)/users/$(ID) | jq
