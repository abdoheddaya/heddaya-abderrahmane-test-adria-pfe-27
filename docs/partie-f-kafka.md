# Partie F - Kafka

Le topic `bank.transactions.created` est cree avec une partition. Apres la validation de la transaction locale, un evenement `TransactionCreated` est publie en JSON, avec `accountId` comme cle Kafka. Le consumer du groupe `bank-audit` journalise la cle et le contenu, sans ecrire dans les bases bancaires.

Lancer Kafka avec `docker compose up -d kafka`, puis les services Eureka, Config, comptes et transactions. Creer un compte actif, puis une transaction valide. Capturer la reponse REST et les deux lignes de log `Published event` et `Consumed bank.transactions.created`. Le JSON doit contenir `eventId`, `eventType`, `transactionId`, `accountId`, `amount`, `currency` et `occurredAt`.

La publication est declenchee seulement apres le commit de la transaction locale (`AFTER_COMMIT`). Un echec Kafka survenu ensuite est journalise ; cette version de l'epreuve n'implemente pas de boite d'envoi transactionnelle pour garantir une livraison ulterieure.
