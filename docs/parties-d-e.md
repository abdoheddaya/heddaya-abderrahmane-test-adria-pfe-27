# Parties D et E - démarrage et preuves

## Démarrage local

Démarrer dans cet ordre : DiscoveryServiceApplication (8761), ConfigServiceApplication (8888), AccountServiceApplication (8081), TransactionServiceApplication (8082), GatewayServiceApplication (8090) et AssistantServiceApplication (8083). Vérifier que `qwen3:4b` est présent avec `ollama list`; sinon lancer `ollama pull qwen3:4b`. Les bases H2 en mémoire sont réinitialisées à chaque redémarrage.

## Captures à conserver

- Eureka : http://localhost:8761 avec les cinq services enregistrés.
- Config Server : http://localhost:8888/transaction-service/default avec `bank.transaction.max-amount=1000`.
- Gateway : réponse de `Invoke-RestMethod http://localhost:8090/api/accounts`.
- Plafond : transaction de 1001 EUR refusée avec HTTP 400.
- Ollama : sortie de `ollama list` montrant `qwen3:4b`.
- Chatbot : requête POST sur http://localhost:8083/api/chat contenant `{"message":"Utilise les outils MCP pour consulter le compte <UUID> et donne son solde en EUR."}` et réponse avec le solde exact.

## Vérification effectuée le 30 octobre 2026

Tous les modules compilent avec Maven (`-DskipTests package`). Config Server sert le plafond 1000. Eureka montre les cinq services. Gateway a routé une transaction de 100 EUR : le solde est passé de 500 à 400 EUR. Une transaction de 1001 EUR a renvoyé HTTP 400. L'assistant local connecté aux deux serveurs MCP a répondu avec le solde exact d'un compte.

## Limites actuelles

Keycloak n'est pas intégré. Les points MCP HTTP sont destinés aux tests locaux tant qu'ils ne sont pas protégés. Le modèle Ollama doit être installé et lancé. Kafka est en cours et la partie F ne doit pas être déclarée terminée.
