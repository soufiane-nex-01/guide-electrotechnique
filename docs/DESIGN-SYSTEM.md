# Système visuel

## Direction

Bibliothèque technique sombre : lisibilité, hiérarchie, repères de domaine et rythme compact. Le bleu identifie les actions. Le rouge est réservé aux alertes. L’interface ne dépend d’aucune ressource distante.

## Couleurs

| Jeton | Valeur | Usage |
|---|---|---|
| `--bg` | `#05070A` | Fond général |
| `--bg-secondary` | `#0B0F14` | Navigation et sous-surfaces |
| `--panel` | `#10151C` | Panneaux |
| `--elevated` | `#131A22` | Surfaces élevées |
| `--card` | `#0D131A` | Fiches et domaines |
| `--blue` | `#00A8FF` | Actions, sélection, focus |
| `--blue-bright` | `#00C2FF` | Survol et focus |
| `--red` | `#B00020` | Fond et bordure d’alerte |
| `--text` | `#F5F5F5` | Texte principal |
| `--secondary` | `#AAB2BD` | Texte secondaire |
| `--muted` | `#8693A2` | Métadonnées, ajustées pour la lisibilité |

Le texte des boutons bleus est sombre pour conserver un contraste élevé. Le texte des alertes est rose clair sur une surface rouge sombre ; le rouge sombre n’est pas utilisé seul pour de petits textes sur fond noir.

## Typographie

Inter variable local, puis Segoe UI et les polices système. Titres de page de 28 à 34 px, titres de section de 18 à 21 px, paragraphes de lecture de 16 px (19 px en mode grande taille). Les informations de navigation et de catalogue sont plus compactes. Le contenu existant est français ; aucune traduction arabe automatique n’est présentée comme une source originale.

## Géométrie et composants

Espacements : 4, 8, 12, 16, 24, 32, 48 px. Rayon de carte : 12 px. Boutons et champs : 7–8 px. Bordures : blanc à 7,5 % ; survol actif bleu à 35 %. Icônes Lucide monochromes de 16 à 24 px.

Boutons : principal bleu, secondaire bordé, discret transparent, action de suppression rouge sombre. États : repos, survol, appui, désactivé et focus clavier. Composants partagés : navigation, fiche, domaine, badge, recherche globale, filtres, onglets, panneau de lecture, lecteur, documents, états vide/erreur/chargement et notification.

## Mouvement et adaptation

Transitions de 180 ms et entrée de page de 220 ms. Les cartes se déplacent au plus de 2 px. Aucun effet animé permanent décoratif. Les squelettes ne sont visibles que pendant un chargement. Respect de `prefers-reduced-motion` et préférence locale de réduction du mouvement.

Navigation latérale de 236 px sur grand écran, puis tiroir sous 800 px. Grilles de quatre/trois/deux colonnes selon l’espace ; liste verticale sur mobile. Le lecteur Flash conserve le rapport 4:3 de sa scène originale. Le mode lecture, la navigation et les listes s’adaptent à la largeur de l’écran.

## Accessibilité

HTML sémantique, lien d’évitement, libellés de champs et boutons, focus bleu explicite, icônes décoratives masquées aux technologies d’assistance, états sélectionnés annoncés, dialogue de recherche natif, navigation clavier des onglets et résultats. Les activités Flash émulées ont leurs propres limites d’accessibilité ; la lecture textuelle est disponible séparément.

## Apparence — Clair / Sombre (1.1.0)

Le mode sombre reste le défaut. Le choix explicite est enregistré avec les préférences existantes sous `electrotechnique.local.v1`, propriété `theme` (`light` ou `dark`). `theme.js` applique ce choix avant la feuille de style. Les composants utilisent des couleurs sémantiques pour leurs surfaces, textes, contours, états, chargements, alertes et recherche.

Le mode clair utilise un fond #F3F6FA, des cartes blanches, du texte #152433 et un bleu #006CAF pour conserver le contraste. Le réglage est un groupe de boutons radio natifs, avec aperçu décoratif, focus visible et coche distincte de la couleur. Android conserve également le choix pour les barres système, les dialogues natifs et les commandes PDF. Les documents et animations d’origine ne sont pas recolorés.

## Lexique bilingue

La navigation reste française. Les termes français sont prioritaires, les traductions arabes sont balisées lang=ar et dir=rtl. Les définitions utilisent des panneaux details/summary natifs, accessibles au clavier. Les surfaces suivent les thèmes Clair et Sombre. Les fontes arabes du système évitent un téléchargement de police supplémentaire. Les vues mobiles n’utilisent ni flou de fond ni shimmer continu.

## Parcours et atelier

Les nouvelles surfaces réutilisent les couleurs sémantiques Clair/Sombre, les bordures de 1 px et les rayons de 8 à 12 px. Deux colonnes sur grand écran, une colonne sur petit écran ; navigation sans surcharge par deux entrées seulement. Les quiz utilisent fieldset/legend, des boutons radio natifs, un focus visible et des résultats textuels annoncés. La réussite est indiquée par un texte et une icône, sans dépendre de la couleur. Les erreurs de quiz restent neutres ; le rouge est réservé aux risques et actions destructives. La grande taille des textes concerne également les parcours. Aucun effet continu ni bibliothèque visuelle supplémentaire.

## Ateliers natifs et langues

Les 41 ateliers utilisent les tokens sémantiques existants, les bordures fines et les surfaces Clair/Sombre. Contrôles de 40–42 px, labels explicites, erreurs textuelles, état actif souligné et nommé. Les SVG changent uniquement lors d’une action ; les chronologies sont avancées manuellement. Deux colonnes sur écran large, une colonne sur mobile ; les tables ont un défilement horizontal limité. Les courbes et formules restent en LTR dans la page arabe RTL. Les couleurs de la résistance correspondent aux bandes physiques du composant. Les autres diagrammes utilisent le bleu et le gris ; le rouge marque le risque.
