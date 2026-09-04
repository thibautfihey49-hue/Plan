# 🌸 Bloom GPS — Plan

**Suivi GPS par SMS de données — 100% invisible, pas dans la messagerie.**

✅ Fonctionne sans internet, uniquement par réseau téléphonique.

---

## ✅ Fonctionnalités
- 📍 Position envoyée automatiquement tous les 10 mètres
- 📡 SMS de données — **invisible dans la messagerie**
- 🗺️ Carte hors connexion (OpenStreetMap)
- 📊 Vitesse en temps réel
- 🎮 Start/Stop local ET à distance
- 🔇 Aucune notification visible
- 🔑 Demande automatique de TOUTES les permissions au démarrage

---

## 📱 Utilisation
1. Entre le **numéro de l'autre téléphone**
2. Clique sur **▶️ MOI — Démarrer** → envoie ta position
3. Clique sur **📡 À DISTANCE — Démarrer** → démarre la position de l'autre
4. La position s'affiche sur la carte en temps réel

---

## 📜 HISTORIQUE DES VERSIONS

### ✅ Version actuelle — 04/09/2026
- ✅ Fix Android 14/15 : `RECEIVER_EXPORTED` dans Manifest ET dans le code
- ✅ SMS de données uniquement — pas de SMS visibles dans la messagerie
- ✅ Demande automatique de toutes les permissions
- ✅ Carte OSM complète + 2 marqueurs (moi / autre)
- ✅ Vitesse en km/h affichée en temps réel
- ✅ Start/Stop local + à distance par commande SMS cachée
- ✅ Package : `com.bloom.gps` — Nom : `Bloom GPS`
- ✅ Pas de `android:gap` — tout en `layout_margin`
- ✅ Port SMS : `50006.toShort()` corrigé
- ✅ `gradle.properties` sans guillemets — compilation propre

### ❌ Erreurs résolues avant cette version
- ~~`ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain`~~ → Corrigé
- ~~`android:gap not found`~~ → Remplacé par margin
- ~~`Unresolved reference: IntentFilter`~~ → Import ajouté
- ~~`RECEIVER_EXPORTED / RECEIVER_NOT_EXPORTED`~~ → Corrigé Android 14
- ~~`The integer literal does not conform to type Short`~~ → `50006.toShort()`
- ~~`package manquant dans Manifest`~~ → Ajouté `com.bloom.gps`
- ~~`"-Xmx64m" ClassNotFoundException`~~ → `gradle.properties` corrigé sans guillemets

---

> 💡 **Cette version est stable et fonctionnelle. Plus rien ne disparaît.**
