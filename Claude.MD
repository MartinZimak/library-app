# CLAUDE.md — Knihovna (učební projekt)

## O projektu
Aplikace pro správu knihovny (evidence knih, čtenářů a výpůjček). Vzniká jako **učební projekt** — hlavním cílem je, aby se autor naučil backend v Kotlinu, ne dodat hotový produkt co nejrychleji.

## Jak se mnou pracovat (důležité)
Tohle je učební projekt, drž se prosím těchto zásad:
- **Vysvětluj, nejen generuj.** U nového kódu krátce vysvětli, co dělá a proč — hlavně kotlinovské a springové konstrukce, které jsou nové.
- **Postupuj po malých krocích.** Neimplementuj celou feature ani víc vrstev najednou bez vyžádání. Radši jeden endpoint / jedna třída, ať to autor stihne pochopit a sám napsat.
- **Neutíkej dopředu a neover-engineeruj** — žádné předčasné abstrakce, návrhové vzory navíc ani knihovny, které zatím nejsou potřeba.
- **Preferuj srozumitelnost před chytrostí.** Cílem je učení.
- **U rozhodnutí** (název, struktura, přístup) nabídni varianty a vysvětli kompromisy místo tichého rozhodnutí za autora.
- Když něco není jasné, zeptej se místo hádání.

## Background autora
Zkušený backendista z **PHP** (framework + Doctrine ORM, běh na **OpenSwoole**). Z toho plyne:
- Backendové principy (REST, routing, ORM, DI, vrstvení) **už zná** — nevysvětluj základy do hloubky.
- Koncept **dlouho běžícího procesu** (sdílený stav, connection pooly, thread-safety) zná ze Swoole; na JVM jen platí přísněji (skutečný paralelismus napříč vlákny).
- **Novinka je jazyk Kotlin a JVM/Spring ekosystém.** Sem směřuj vysvětlování. Analogie k PHP / Doctrine / Composeru / Swoole pomáhají.

## Stack
- Jazyk: **Kotlin**
- Framework: **Spring Boot**
- Build: **Gradle (Kotlin DSL)** — `build.gradle.kts`
- Persistence: **Spring Data JPA** (Hibernate)
- DB: **H2** (in-memory) pro vývoj teď; **PostgreSQL** přidáme později
- JDK: **26+** (LTS)
- Závislosti: Spring Web, Spring Data JPA, H2, Validation

## Konvence
- Architektura: **controller → service → repository**.
- Kotlin: preferuj `val` před `var`, `data class` pro entity a DTO, využívej null safety (`String` vs `String?`); nepoužívej `!!`.
- **DTO odděl od JPA entit** — nevracej entity přímo z controlleru.
- Identifikátory v kódu **anglicky** (Book, User, Loan…) kvůli souladu s tutoriály a knihovnami; texty a komentáře můžou být česky. (Autor klidně změní, pokud chce vše česky.)
- REST: množné podstatné jméno v cestě (`/books`, `/users`), správné HTTP metody a status kódy.
- **Postman kolekce:** REST API je zrcadleno v `postman/library.postman_collection.json` (složka per resource, sdílená proměnná `{{baseUrl}}`). **Pokaždé, když vznikne nový endpoint nebo se nějaký změní (cesta, metoda, tělo, status), je nutné kolekci aktualizovat** ve stejném kroku.

## Doménový model
- **Book (Kniha):** titul, autor, ISBN, dostupnost
- **Author (Autor):**
- **User (Čtenář):** jméno, e-mail
- **Loan (Výpůjčka):** kniha, čtenář, datum vypůjčení, termín vrácení, datum vrácení

## Roadmapa (kde jsme)
Projekt je **čerstvě vygenerovaný ze start.spring.io**, zatím bez doménového kódu. Stavíme postupně:
1. **CRUD pro knihy** ← další krok
2. CRUD pro čtenáře
3. Půjčování a vracení (business logika: kontrola dostupnosti, termíny vrácení)
4. Doladění: validace vstupů, ošetření chyb, vyhledávání, hlídání zpožděných výpůjček
5. Testy průběžně (jednotkové na logiku, integrační na endpointy)

## Spuštění
- Build a běh: `./gradlew bootRun`
- H2 konzole na `/h2-console` (po zapnutí přes `spring.h2.console.enabled=true` v `application.properties`)