provision:
	@docker-compose up --detach

destroy:
	@docker-compose down --volumes

enter-database:
	@docker exec --interactive --tty \
		infrastructure-database-1 mysql -u root -p
