# 📘 Project Management Tool (MPMT)

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)  
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)](https://spring.io/projects/spring-boot)  
[![Angular](https://img.shields.io/badge/Angular-17-red)](https://angular.io/)  
[![Docker](https://img.shields.io/badge/Docker-✓-blue)](https://www.docker.com/)  
[![CI/CD](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-blue)](https://github.com/features/actions)

---

## 📖 Contexte
Ce projet est réalisé dans le cadre de l’**étude de cas Project Management Tool (PMT)** (Bloc *Intégration, industrialisation et déploiement de logiciel*).

**MPMT** est une plateforme collaborative de gestion de projets, permettant de :
- gérer des projets et leurs membres,
- créer et assigner des tâches,
- suivre l’avancement via un tableau de bord, 
- notifier les membres par e-mail,
- conserver un historique des modifications.

---

## 🚀 Technologies utilisées
- **Frontend** : Angular 17, TypeScript, TailwindCSS, Jest.
- **Backend** : Java 21, Spring Boot 3.5, Spring Data JPA, Spring Validation, Spring Mail, JUnit 5, Mockito, JaCoCo.
- **Base de données** : PostgreSQL 16.
- **Outils** : Docker, GitHub Actions.

---

## 📂 Architecture du projet
```
MPMT/
 ├── client/                # Frontend Angular
 ├── server/                # Backend Spring Boot
 ├── sql/                   # Scripts SQL (schema, reset, demo)
 ├── docs/                  # Documentation + rapports de couverture
 │   └── coverage/          # Rapports JaCoCo & Jest
 │   └── design/            # Schémas MCD/UML
 │   └── schema.sql         # Schema de la base de données
 ├── docker-compose.yml     # Orchestration des services
 ├── README.md              # Documentation principale
```

---

## 🗄️ Base de données
- **Schéma** (MCD/UML → `docs/design/`).
- **Scripts fournis** :
    - `sql/schema.sql` → création complète de la base.
    - `sql/reset.sql` → purge des données (TRUNCATE).
    - `sql/demo-data.sql` → jeu de données de démonstration.

---

## 🧑‍💻 Démarrage rapide

### 1. Cloner le projet
```bash
git clone https://github.com/<ton-user>/MPMT.git
cd MPMT
```

### 2. Lancer en mode **Docker**
```bash
docker compose up --build -d
```

- 🌐 Frontend : http://localhost:8080
- ⚙️ Backend API : http://localhost:8081/api
- 📬 Mailhog (mails de test) : http://localhost:8025

### 3. Charger des données de démo
```bash
docker exec -i mpmt-postgres psql -U postgres -d mpmt < sql/reset.sql
docker exec -i mpmt-postgres psql -U postgres -d mpmt < sql/demo-data.sql
```

### 4. Comptes démo
| Rôle  | Email             | Mot de passe |
|-------|-------------------|--------------|
| Admin | admin@demo.local  | Admin123!    |
| Alice | alice@demo.local  | Demo123!     |
| Bob   | bob@demo.local    | Demo123!     |

---

## ✅ Tests & couverture

### Backend (JUnit, Mockito, JaCoCo)
```bash
cd server
mvn clean test jacoco:report
```
📊 Rapport : `docs/coverage/backend/index.html`  
Couverture obtenue : **76%**, **57%** (≥60% attendu).

### Frontend (Jest)
```bash
cd client
npm test -- --coverage
```
📊 Rapport : `docs/coverage/frontend/index.html`  
Couverture obtenue : **97%**, **74%**.

---

## ⚙️ CI/CD

Une pipeline **GitHub Actions** est configurée (`.github/workflows/ci.yml`) :
- ⚙️ Build & tests backend → JaCoCo.
- ⚙️ Build & tests frontend → Jest.
- 📂 Upload des rapports comme artefacts.
- 🐳 Build & push des images Docker vers Docker Hub.

---

## Déploiement Docker

### Images disponibles
- **Backend** : `gaelem/mpmt-backend:latest`
- **Frontend** : `gaelem/mpmt-frontend:latest`

### Procédure
```bash
docker pull gaelem/mpmt-backend:latest
docker pull gaelem/mpmt-frontend:latest
docker compose up -d
```

---

## Livrables
- ✅ Schéma de la base de données (MCD/UML, `docs/design`)
- ✅ Scripts SQL (`schema.sql`, `reset.sql`, `demo-data.sql`)
- ✅ Repository GitHub avec frontend & backend
- ✅ Rapports de couverture (JaCoCo + Jest)
- ✅ Dockerfiles (frontend & backend)
- ✅ docker-compose.yml
- ✅ CI/CD (.github/workflows/ci.yml)
- ✅ README.md (ce fichier)

---

## Auteur
**Gaël El Mokhtari** dans le cadre de l’étude de cas *PMT*.  
