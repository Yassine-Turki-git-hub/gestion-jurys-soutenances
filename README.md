# Gestion des Jurys et Soutenances 📚

Une application basée sur une architecture microservices pour la gestion complète des soutenances académiques.

## 📋 Vue d'ensemble

Cette plateforme permet de gérer de manière efficace et organisée l'ensemble du processus des soutenances, incluant la planification, l'attribution des jurys, l'évaluation et la publication des résultats.

## 🏗️ Architecture Microservices

Le projet est basé sur une architecture microservices avec les services suivants :

### Services Métier

| Service | Port | Description |
|---------|------|-------------|
| **API Gateway** | `8080` | Point d'entrée unique pour tous les services avec authentification JWT |
| **Service Utilisateurs** | `8081` | Gestion des utilisateurs, authentification et autorisations |
| **Service Planification** | `8082` | Planification et calendrier des soutenances |
| **Service Soutenance** | `8083` | Gestion des données des soutenances |
| **Service Jury** | `8084` | Gestion des jurys et évaluation des soutenances |
| **Service Résultats** | `8085` | Compilation et publication des résultats |

## 🔧 Stack Technique

- **Langage** : Java 17
- **Framework** : Spring Boot 3.2.5 / 4.0.6
- **Gestion de dépendances** : Maven
- **Communication inter-services** : OpenFeign
- **Passerelle API** : Spring Cloud Gateway
- **Authentification** : JWT (jjwt)
- **Base de données** : H2 (développement)
- **Message Broker** : RabbitMQ (AMQP)
- **Documentation API** : Springdoc OpenAPI/Swagger
- **ORM** : Spring Data JPA
- **Outils** : Lombok, Spring DevTools

## 📦 Structure du Projet

```
gestion-jurys-soutenances/
├── api-gateway/                    # Passerelle API - point d'entrée
├── service-utilisateurs/           # Gestion des utilisateurs et authentification
├── service-planification/          # Planification des soutenances
├── service-soutenance/             # Gestion des soutenances
├── service-jury/                   # Gestion des jurys
├── service-resultats/              # Gestion des résultats
└── README.md
```

## 🚀 Démarrage Rapide

### Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- RabbitMQ (optionnel pour développement local)

### Installation

1. **Cloner le repository**
```bash
git clone https://github.com/Yassine-Turki-git-hub/gestion-jurys-soutenances.git
cd gestion-jurys-soutenances
```

2. **Compiler le projet**
```bash
mvn clean install
```

3. **Démarrer les services**

Chaque service peut être démarré indépendamment :

```bash
# Service Utilisateurs
cd service-utilisateurs/service-utilisateurs
mvn spring-boot:run

# Service Planification
cd service-planification
mvn spring-boot:run

# Service Soutenance
cd service-soutenance
mvn spring-boot:run

# Service Jury
cd service-jury
mvn spring-boot:run

# Service Résultats
cd service-resultats
mvn spring-boot:run

# API Gateway (à démarrer en dernier)
cd api-gateway
mvn spring-boot:run
```

## 🔐 Authentification

Le système utilise JWT pour l'authentification :

- Les utilisateurs s'authentifient auprès du **Service Utilisateurs**
- L'**API Gateway** valide les tokens JWT pour chaque requête
- Les tokens contiennent les informations de rôle et les permissions

## 📡 Communication entre Services

- **Synchrone** : OpenFeign pour les appels REST directs
- **Asynchrone** : RabbitMQ/AMQP pour la communication événementielle

## 📚 Documentation API

Chaque service expose sa documentation Swagger/OpenAPI :

- Service Utilisateurs : `http://localhost:8081/swagger-ui.html`
- Service Planification : `http://localhost:8082/swagger-ui.html`
- Service Soutenance : `http://localhost:8083/swagger-ui.html`
- Service Jury : `http://localhost:8084/swagger-ui.html`
- Service Résultats : `http://localhost:8085/swagger-ui.html`

## 🧪 Tests

Les tests unitaires et d'intégration peuvent être exécutés avec :

```bash
mvn test
```

Pour exécuter les tests d'un service spécifique :

```bash
cd <nom-du-service>
mvn test
```

## 📋 Fonctionnalités Principales

### Service Planification
- Création et gestion du calendrier des soutenances
- Attribution des salles et des créneaux horaires
- Gestion des conflits d'horaire

### Service Jury
- Composition des jurys
- Enregistrement des évaluations et des notes
- Suivi des présences

### Service Soutenance
- Enregistrement des informations des soutenances
- Gestion des étudiants et projets
- Suivi du statut des soutenances

### Service Résultats
- Agrégation des notes et résultats
- Génération de rapports
- Publication des résultats finaux

## 🛠️ Configuration

Chaque service peut être configuré via les fichiers `application.properties` ou `application.yml` :

```properties
# Exemple : Service sur port personnalisé
server.port=8081

# Configuration JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# Configuration de la base de données
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

## 🚨 Dépannage

### Les services ne communiquent pas
- Vérifiez que tous les services sont démarrés
- Vérifiez les logs pour les erreurs de connexion OpenFeign
- Assurez-vous que l'API Gateway est démarrée en dernier

### Erreurs JWT
- Vérifiez que le secret JWT est cohérent entre les services
- Vérifiez l'expiration du token
- Vérifiez les logs du Service Utilisateurs

### Problèmes de base de données
- H2 utilise une base de données en mémoire par défaut (non persistante)
- Pour la persistance, configurez une vraie base de données (PostgreSQL, MySQL, etc.)

## 📝 Licence

Pas de licence spécifiée. À adapter selon vos besoins.

## 👤 Auteur

Yassine Turki - [GitHub Profile](https://github.com/Yassine-Turki-git-hub)

## 📞 Support

Pour toute question ou problème, veuillez créer une issue dans le repository GitHub.

---

**Dernière mise à jour** : 2026-07-03  
**Version du projet** : 0.0.1-SNAPSHOT
