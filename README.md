# EasySell

Jednoduchá Android aplikace pro rychlou správu sortimentu a tvorbu objednávek.

EasySell je navržený především pro situace, kde je potřeba během několika sekund vybrat produkty, vytvořit objednávku, uložit ji a později se k ní vrátit v historii.

## ✨ Funkce

### 🏠 Home

Domovská obrazovka slouží jako rychlý vstup do nové objednávky.

* Trvale uložený název objednávky
* Název lze kdykoliv změnit
* Jedním tlačítkem lze okamžitě vytvořit novou objednávku
* Poslední použitý název zůstává zachovaný i po restartu aplikace

### 🧾 Nová objednávka

Obrazovka pro rychlé sestavení objednávky je rozdělena na dvě horizontální části:

* **Horní část** – aktuální objednávka a její souhrn
* **Dolní část** – seznam dostupného sortimentu

U každého produktu lze jednoduše měnit množství.

Aplikace průběžně počítá:

* množství jednotlivých položek
* cenu jednotlivých položek
* celkovou cenu objednávky

Objednávka je při uložení označena unikátním ID vytvořeným z názvu objednávky a náhodného identifikátoru.

Příklad:

```text
firemni-vecirek-a81f24c7
```

### 📋 Historie

Historie obsahuje všechny uložené objednávky seřazené od nejnovější.

V seznamu jsou zobrazeny základní informace:

* název objednávky
* datum a čas vytvoření
* celková cena

Po otevření objednávky je k dispozici kompletní detail včetně:

* ID objednávky
* všech položek
* množství
* ceny za kus
* ceny za položku
* celkové ceny

Objednávku lze také odstranit. Před odstraněním aplikace zobrazí potvrzovací dialog.

### 🛒 Sortiment

Sekce GOODS slouží ke správě produktů.

Podporuje:

* přidání produktu
* úpravu produktu
* odstranění produktu
* změnu ceny
* změnu kategorie
* filtrování podle kategorie

Produkty jsou barevně odlišeny podle kategorie pro rychlejší orientaci.

## 🏗️ Architektura

Projekt používá moderní Android stack postavený na Kotlinu a Jetpack Compose.

```text
UI (Jetpack Compose)
        │
        ▼
ViewModel
        │
        ▼
Repositories
        │
        ├── ProductRepository
        │
        └── OrderRepository
        │
        ▼
Room Database
```

### Použité technologie

* **Kotlin**
* **Jetpack Compose**
* **Material 3**
* **AndroidX**
* **Room**
* **KSP**
* **DataStore**
* **Coroutines / Flow**
* **ViewModel**

## 💾 Datový model

Produkty jsou uloženy v tabulce:

```text
products_table
```

Objednávky jsou rozdělené do dvou tabulek:

```text
orders
order_items
```

### Orders

Obsahuje základní údaje objednávky:

```text
id
name
createdAt
total
```

### Order Items

Obsahuje jednotlivé položky:

```text
id
orderId
productId
productName
unitPrice
quantity
```

Název produktu a jeho cena se ukládají přímo do položky objednávky.

Díky tomu zůstává historie objednávky správná i v případě, že je produkt později upraven nebo jeho cena změněna.

## 🌱 Seed data

Při prvním vytvoření databáze jsou automaticky vložena výchozí data sortimentu.

Seedovací data jsou definována v:

```text
data/SeedData.kt
```

Seed se provádí pouze při prvním vytvoření databáze.

Při vývoji lze databázi resetovat například vymazáním dat aplikace nebo odinstalováním aplikace z testovacího zařízení.

## 📁 Struktura projektu

```text
app/
└── src/
    └── main/
        └── java/
            └── com/example/easysell/
                │
                ├── MainActivity.kt
                │
                ├── data/
                │   ├── ProductRepository.kt
                │   ├── OrderRepository.kt
                │   ├── UserPreferencesRepository.kt
                │   └── SeedData.kt
                │
                ├── data/local/
                │   ├── Product.kt
                │   ├── ProductDao.kt
                │   ├── OrderEntity.kt
                │   ├── OrderItemEntity.kt
                │   ├── OrderWithItems.kt
                │   ├── OrderDao.kt
                │   └── ProductDatabase.kt
                │
                └── ui/
                    ├── EasySellViewModel.kt
                    │
                    ├── screens/
                    │   ├── HomeScreen.kt
                    │   ├── NewOrderScreen.kt
                    │   ├── HistoryScreen.kt
                    │   ├── GoodsScreen.kt
                    │   └── CategoryColors.kt
                    │
                    └── theme/
```

## 🚀 Spuštění projektu

Projekt lze otevřít přímo v Android Studiu.

Po synchronizaci Gradle lze aplikaci spustit na:

* Android Emulatoru
* fyzickém Android zařízení

Standardní build:

```bash
./gradlew assembleDebug
```

Instalace debug APK:

```bash
./gradlew installDebug
```

## 🧪 Vývoj

Při práci s databází je potřeba myslet na Room migrations.

Aktuální databázová verze je:

```text
2
```

Při změně databázového schématu je nutné:

1. zvýšit verzi databáze
2. přidat odpovídající migraci
3. otestovat existující databázi

## 🎨 Kategorie produktů

Sortiment používá následující kategorie:

```text
FOOD
DRINK
COFFEE
TEA
DESERT
SWEET
MISC
```

Každá kategorie má vlastní barevné schéma používané v UI.

## 🔐 Identifikace objednávek

Každá objednávka má unikátní textové ID generované z názvu.

Například:

```text
Název:
Firemní večírek

ID:
firemni-vecirek-81bc40fa
```

ID slouží jako jednoznačný identifikátor objednávky napříč celou aplikací.

## 🛠️ Stav projektu

Projekt je ve fázi aktivního vývoje.

Aktuálně jsou implementovány:

* [x] správa sortimentu
* [x] přidání produktu
* [x] editace produktu
* [x] odstranění produktu
* [x] filtrování sortimentu
* [x] barevné rozlišení kategorií
* [x] vytvoření objednávky
* [x] výpočet ceny objednávky
* [x] ukládání objednávek
* [x] historie objednávek
* [x] detail objednávky
* [x] odstranění objednávky s potvrzením
* [x] persistentní název objednávky
* [x] seed dat
* [x] Room persistence

## 📄 Licence

Projekt je určen především pro vlastní vývoj a experimentování.

Licence projektu se řídí nastavením tohoto GitHub repozitáře.
