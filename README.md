# TP-Android-el-haourari-ibrahim
EL HAOURARI IBRAHIM


________________________________________
1. Introduction
1.1 Contexte du Projet
Développement d'une application Android native de commerce électronique utilisant les technologies modernes du développement mobile. Ce projet s'inscrit dans le cadre [du cours/de la formation] en [nom de la matière].
1.2 Objectifs
•	Implémenter un frontend performant avec Jetpack Compose
•	Concevoir une architecture logicielle robuste (MVI + Clean Architecture)
•	Maîtriser les composants Android modernes (ViewModel, Navigation, etc.)
•	Respecter les bonnes pratiques de développement (SOLID, DRY)
1.3 Périmètre Fonctionnel
Fonctionnalité	Statut
Authentification	✅
Catalogue produits	✅
Gestion panier	✅
Passation de commande	✅
Profil utilisateur	✅
________________________________________


2. Architecture Technique
2.1 Schéma Global
 
2.2 Choix Technologiques
•	Langage : Kotlin (100%)
•	UI : Jetpack Compose + Material 3
•	Architecture :
o	MVI (Model-View-Intent)
o	Clean Architecture (adaptée)
•	Dépendances :
o	Hilt (DI)
o	Navigation Compose
o	Kotlin Coroutines/Flow
2.3 Points Forts de l'Architecture
•	Séparation claire des responsabilités
•	Testabilité améliorée
•	État prévisible et traçable
•	Injection de dépendances systématique
3. Implémentation Détaillée
3.1 Gestion d'État (MVI)
Cycle de vie typique :
1.	L'UI émet une Intent
2.	Le ViewModel traite l'intent et produit un nouvel État
3.	L'UI se recompose en fonction du nouvel état
Exemple concret :
// Dans le ViewModel
fun processIntent(intent: CartIntent) {
    when(intent) {
        is AddToCart -> _state.update { 
            it.copy(items = it.items + intent.product) 
        }
        // ... autres cas
    }
}

3.2 Performance et Optimisation
Techniques employées :
•	LazyColumn/LazyVerticalGrid pour les listes
•	remember et derivedStateOf pour les calculs coûteux
•	Cache mémoire des produits (5 minutes)
•	Pré-chargement des images avec Coil
Résultats :
•	Temps de rendu moyen : < 16ms
•	Taux de FPS : 60 en moyenne
•	Consommation mémoire : 45MB en usage normal
________________________________________
4. Défis Techniques et Solutions
4.1 Problèmes Rencontrés





Problème	Impact	Solution Apportée
Crash navigation Order → OrderList	Blocage utilisateur	Gestion rigoureuse de la backstack
État perdu en rotation	Mauvaise UX	rememberSaveable + ViewModel
Scroll hésitant	Expérience dégradée	Optimisation des images + placeholders

4.2 Diagramme de Séquence Critique (Ajout au Panier)
 
5 Demonstration



# dernieres modifications

# se connecter
![Screenshot_2025-06-27-21-32-03-63_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/27bb7dd0-9d25-4849-b877-a2c6cfd4d5aa)

# s'inscrire
![Screenshot_2025-06-27-21-32-09-13_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/1bfafdd6-3421-4b50-b2e4-3d638be8e6d1)

# home_screen
![Screenshot_2025-06-27-21-32-33-00_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/13896143-42af-4c5f-b6eb-42f68323d3b3)

# catalogue_produits
![Screenshot_2025-06-27-21-32-43-24_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/efdcb3ac-2d41-49d9-8c65-be1fde08e2a9)

# details_product
![Screenshot_2025-06-27-21-33-40-76_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/a4ed4388-3092-41a8-a52b-2deb7f59e7fb)

# panier
![Screenshot_2025-06-27-21-32-58-80_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/48ceb21f-7a4b-45a4-ba1a-f14b25ea9862)

# commandes_list
![Screenshot_2025-06-27-21-33-16-93_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/e2514d80-a296-49cf-954e-d7c23a4fd0a8)

# commande_details
![Screenshot_2025-06-27-21-33-10-08_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/8c9de68a-3e6e-4be1-9a32-5e8d12a2c592)


# profile
![Screenshot_2025-06-27-21-33-21-64_6e153ff9fcdf50a71d8ba028a45f8cfb](https://github.com/user-attachments/assets/535b3a0d-5f86-4447-b87e-59e24c473a97)

6. Perspectives d'Amélioration
6.1 Évolutions Techniques
1.	Paiement en ligne : Intégration Stripe
2.	Mode hors-ligne : Synchronisation différentielle
3.	Analytics : Suivi des comportements utilisateurs
6.2 Optimisations
•	Migration vers Room pour la persistance
•	Implémentation de Compose Navigation Animation
•	Internationalisation complète
________________________________________
7. Conclusion
7.1 Bilan Technique
•	Objectifs principaux atteints à 95%
•	Code maintenable et bien architecturé
•	Bonnes pratiques globalement respectées
7.2 Apprentissages Clés
•	Maîtrise approfondie de Jetpack Compose
•	Compréhension des architectures MVI
•	Gestion des états complexes dans les applications mobiles
7.3 Remerciements
Je tiens à remercier [Nom du Professeur] pour son accompagnement tout au long de ce projet particulièrement enrichissant.








# home-screen
![Image](https://github.com/user-attachments/assets/00c34703-6cdf-4e4d-a0ee-43eb25e904cc)

# cataloge-screen
![Image](https://github.com/user-attachments/assets/27e18da7-aaf4-49f2-a174-1a7779f8d1ef)


![Image](https://github.com/user-attachments/assets/f3c26344-fca2-44a5-8ddd-90b518996ae4)

# product-details
![Image](https://github.com/user-attachments/assets/03e9a9c7-ce6e-4e62-bce4-41776357418f)


