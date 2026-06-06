# 🛠️ Locator Tools - Extracteur de Locators avec IA

DESCRIPTION
-----------
"locator-tools" est un outil d'automatisation en Java conçu pour simplifier
la vie des ingénieurs QA. Il utilise Playwright pour inspecter et extraire
les éléments du DOM d'une page web, puis s'appuie sur la puissance des
modèles de langage (LLM) comme Qwen2.5-Coder (via Ollama) ou Claude 3.5 Sonnet
(via Anthropic) pour générer automatiquement des sélecteurs (Locators Playwright
ou XPath) robustes, stables et optimisés.


## 📋 PREREQUIS
---------
Avant de déployer et de lancer le projet, assure-toi d'avoir installé :
* Java JDK 17 ou une version supérieure.
* Maven (pour la gestion et la restauration des dépendances).
* Un IDE de développement (ex: Visual Studio Code ou IntelliJ IDEA).
* Ollama installé et configuré (pour utiliser l'IA en local et gratuitement).


## 🚀 INSTALLATION ET CONFIGURATION
-----------------------------

1. Récupérer le projet :
   Ouvre ton terminal et clone le dépôt dans ton espace de travail :
   
   ```bash
   git clone https://github.com/HenryElvis/Locator-Tools.git
   cd locator-tools
   ```
   
3. Configurer Ollama (IA en local) :
- Télécharge et installe Ollama pour ton système depuis ollama.com
- Lance une console et récupère le modèle LLM spécialisé pour le code :
      ollama pull qwen2.5-coder:7b
- Assure-toi que le service Ollama tourne en arrière-plan et qu'il est listé :
      ollama list

5. Configurer le fichier .env :
- Crée un fichier nommé exactement ".env" à la racine du sous-dossier
   "locator-tools" (juste à côté du fichier pom.xml).
- Ajoute le bloc de configuration suivant à l'intérieur :

   # Configuration Anthropic Claude (Si useClaude = true dans le code)
   - ANTHROPIC_API_KEY=votre_cle_api_ici
   - CLAUDE_MODEL=claude-3-5-sonnet-latest

   # Configuration Ollama Local (Si useClaude = false)
   OLLAMA_MODEL=qwen2.5-coder:7b

   # Paramètres globaux de l'IA
   TEMPERATURE=0.0

UTILISATION ET FONCTIONNEMENT
-----------------------------

1. Lance l'application principale via ton IDE (en exécutant la classe App.java)

2. Interagis avec l'application dans la console :
   * URL du site : Saisis l'URL complète (Ex: https://www.saucedemo.com/).
     Si tu laisses vide, le script prendra SauceDemo par défaut.
   * Type de locator : Indique la balise HTML à cibler (button, input, a, etc.).
   * Format type : Choisis le format de sortie en tapant "locator" (pour la
     syntaxe native Playwright Java) ou "xpath".

3. Le workflow automatisé se lance :
   * Une instance de navigateur Chromium s'ouvre en mode visible (headless false).
   * Playwright extrait le code source "outerHTML" brut des éléments.
   * Le prompt d'ingénierie QA formatte la demande et l'envoie à l'IA.
   * Les chemins optimisés et épurés s'affichent directement dans le terminal.


ARCHITECTURE DU PROJET
----------------------
* App.java : Point d'entrée de l'outil, orchestre les entrées utilisateur
  (Scanner) et gère la boucle d'affichage des résultats de l'IA.
* PlaywrightService.java : Gère le cycle de vie du navigateur, la navigation
  synchrone et l'évaluation JavaScript des éléments du DOM.
* LocatorGenerator.java : Gère le chargement dynamique du fichier de
  configuration .env via Dotenv. Construit les clients d'IA LangChain4j et
  structure les prompts de consignes stricts envoyés aux LLM.


DÉPANNAGE (TROUBLESHOOTING)
---------------------------

🔴 Erreur : "modelName cannot be null or blank" ou "String.trim() is null"
   * Cause 1 : L'application ne trouve pas ton fichier .env en raison d'un
     décalage de dossier d'exécution dans ton IDE.
   * Résolution : Le code intègre une détection dynamique de chemin absolu.
     Assure-toi simplement que ton fichier se nomme exactement .env (et non
     .env.txt sous Windows) et qu'il est situé au même niveau que le pom.xml.
   
   * Cause 2 (Variables d'environnement système) : Si tu décides d'utiliser
     les variables d'environnement Windows/macOS au lieu du fichier .env,
     ton IDE peut ne pas les détecter immédiatement.
   * Résolution : Après avoir créé ou modifié une variable d'environnement sur
     ton système, TU DOIS IMPÉRATIVEMENT REDÉMARRER COMPLÈTEMENT TON ÉDITEUR
     (VS Code ou IntelliJ) pour qu'il recharge le nouveau contexte.

🔴 Le script se bloque ou crash lors de l'appel à Ollama
   * Cause : Au premier appel, si le modèle n'a jamais été exécuté, Ollama doit
     charger les ~4.7 Go du modèle depuis le disque vers la mémoire RAM. Si la
     machine met trop de temps, le connecteur Java peut expirer (Timeout).
   * Résolution : Avant de lancer le script Java, force le chargement du modèle
     manuellement en arrière-plan en ouvrant un terminal classique et en tapant :
     
     ```bash
     ollama run qwen2.5-coder:7b
     ```

     Une fois que le prompt ">>>" s'affiche (modèle chaud en RAM), laisse ce
     terminal ouvert et lance ton script Java. Les requêtes répondront alors
     instantanément.

     Tu peux vérifier si sur le http://localhost:11434/ il y a bien "Ollama is running"

🔴 L'IA renvoie du XPath alors que j'ai demandé le format "locator"
   * Cause : Les petits modèles locaux ont tendance à copier les structures
     basiques de l'invite de commande si les exemples manquent de clarté.
   * Résolution : Le prompt a été sectorisé de manière dynamique. En mode
     "locator", il injecte des exemples stricts utilisant exclusivement l'API
     Java de Playwright (page.getByRole, page.locator), forçant le modèle à
     respecter scrupuleusement la syntaxe Java.
     Il est possible d'affiner le résultats avec les exemples dans la méthode Prompt().
========================================================================
