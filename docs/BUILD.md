# Construire et exécuter depuis les sources

## Aperçu Web local

Python 3 suffit pour ouvrir la variante ordinateur dans un navigateur moderne :

```powershell
python -m http.server 8876 --bind 127.0.0.1 --directory desktop/app
```

Ouvrir `http://127.0.0.1:8876/`. Garder le terminal ouvert ; `Ctrl+C` arrête le serveur. Utiliser HTTP local plutôt que `file://`, car les données sont chargées avec `fetch`.

## Lanceur Windows

Windows 10/11 avec .NET Framework 4.x :

```powershell
powershell -NoProfile -File scripts/build-windows.ps1
.\desktop\Guide-Electrotechnique.exe
```

Le lanceur et le dossier `app` doivent rester côte à côte. Le serveur écoute seulement l’interface locale. Le menu de la zone de notification permet de quitter.

## APK Android

Sur Windows, installer Python 3, JDK 17 et Android SDK avec Build Tools 36 et la plateforme API 35. Aucun Gradle ni téléchargement de dépendances n’est utilisé pendant le build. Le script sélectionne les outils installés les plus récents : utiliser un SDK dédié si plusieurs versions sont présentes.

```powershell
python android/source/build.py --sdk "C:/path/to/Android/Sdk" --jdk "C:/path/to/jdk-17" --output "android/build" --work "android/build/work"
```

Résultat : `android/build/Guide-Electrotechnique-Android.apk`. Le nom de paquet est `com.electrotechnique.guide`, version `1.4.0`, code `5`, minimum Android 9 / API 28, cible API 35.

La première compilation crée une clé de signature locale dans `android/build/signature-privee`. Conserver cette clé et son fichier de mot de passe hors du dépôt. Une clé différente ne peut pas mettre à jour une installation portant l’ancienne signature. Le dépôt et les ZIP publics ne contiennent pas la clé de la livraison officielle.

Le code Java gère WebView, les actifs locaux et le lecteur PDF. La variante Android conserve ses liens et styles spécifiques ; ne pas écraser `android/source/assets/web/app.js` ou `index.html` avec les versions ordinateur.

## Tests numériques reproductibles

Avec Node.js installé, depuis la racine :

```powershell
node tests/models.mjs
```

Ce fichier reproduit les 323 contrôles numériques des modèles et de l’évaluation. Les rapports des autres scénarios de la livraison se trouvent dans `docs/verification`. Leurs résultats historiques ne constituent pas un test effectué automatiquement sur votre machine. Lancer ensuite une validation visuelle et des interactions sur un appareil réel avant de diffuser une modification.

## Livraison 1.4.0

APK signé : `48 653 523` octets.

SHA-256 : `0032879ef1c4b0c10010949cc879f5caa85c3c4fac5b8ad8c11ffa9f5b86f3ff`.

Les modèles de traduction, outils temporaires, caches, mots de passe et clés de signature ne font pas partie des sources publiées.
