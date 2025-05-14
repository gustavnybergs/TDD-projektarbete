# Refaktorering av Error Handling och Kod-kvalitet

## Sammanfattning av identifierade förbättringsområden och lösningar

Som en del av vår TDD-process identifierade vi fyra huvudsakliga 
områden där vår kodbas kunde förbättras. Dessa problem uppstod naturligt 
under utvecklingen när två utvecklare arbetat från olika håll och sedan 
mergat sina bidrag. Här är de problem vi upptäckte och hur vi löste dem:

---

## Problem 1: Error Handling var inkonsistent

### Vad vi upptäckte:
Vårt system hade tre olika sätt att hantera fel, vilket skapade förvirring och inkonsistent kod:
- Vissa metoder kastade `Exceptions` (t.ex. `withdraw()`)
- Andra returnerade `boolean` (t.ex. `accountExists()`)
- Ytterligare andra använde `Enum` (t.ex. `AuthenticationResult`)

### Varför detta var problematiskt:
```java
// Förvirrande för utvecklare - olika felhantering:
try {
    boolean success = accountService.withdraw(...);  // Boolean + risk för exception!
    if (!success) {
        // Vad gick fel? Ingen information tillgänglig
    }
} catch (IllegalArgumentException e) {
    // Detaljerat felmeddelande här
}
```

### Vad vi gjorde:
**Skapade ett enhetligt Result Pattern:**
- `TransactionResult` för alla transaktioner med pengar, 
deposit, withdraw etc. Gör det även enklare att lägga till nya funktioner tex transfer
- `OperationResult` Bara kontroller utan saldoändring, utskrifter av nytt saldo tex
- `ErrorCode` enum för standardiserade felkoder

**Resultat:**
```java
// Nu konsistent genom hela systemet:
TransactionResult result = accountService.withdraw(...);
if (result.isSuccess()) {
    showSuccess(result.getNewBalance());
} else {
    showError(result.getMessage());  // Alltid detaljerad information
}
```

**Fördelar:**
- Förutsägbar felhantering
- Detaljerade felmeddelanden för användare
- Enklare testning
- Inga överraskande exceptions

---

## Problem 2: Transaction Design var inkonsistent

### Vad vi upptäckte:
Liknande operationer var placerade på olika ställen i vårt system:
- `DepositService` hanterade insättningar
- `AccountService` hanterade uttag
- Ingen tydlig plats för framtida `transfer()`-funktionalitet

### Varför detta var problematiskt:
```java
// Förvirrande för utvecklare:
depositService.deposit(...);     // Insättningar här
accountService.withdraw(...);    // Men uttag här?
// Var skulle transfer() hamna?
```

### Vad vi gjorde:
**Konsoliderade alla transaktioner till `AccountService`:**
- Flyttade `deposit()` från `DepositService` till `AccountService`
- Behöll `withdraw()` där den redan var
- Skapade en naturlig plats för framtida `transfer()`

**Resultat:**
```java
// Nu konsistent - alla account-operationer på samma ställe:
public class AccountService {
    public TransactionResult withdraw(...);
    public TransactionResult deposit(...);
    public TransactionResult transfer(...);  // Framtida addition
    // Alla andra account-relaterade metoder...
}
```

**Fördelar:**
- Enklare att hitta funktionalitet
- Konsistent design pattern
- Naturlig plats för nya features
- Mindre cognitive load för utvecklare

---

## Problem 3: Blandad språkkonvention

### Vad vi upptäckte:
Vår kodbas innehöll en blandning av svenska och engelska namn:
- Klasser: `ConsoleMeny` (svenska) vs `AccountService` (engelska)
- Metoder: `sättIn()` (svenska) vs `withdraw()` (engelska)
- Variabler: `sedlar` (svenska) vs `amount` (engelska)

### Varför detta uppstod:
Detta var ett naturligt resultat av att vi arbetade parallellt - en använde svenska, den andra engelska. Vid merge behöll vi båda approaches för att undvika konflikter.

### Vad vi gjorde:
**Standardiserade på engelska för kod, svenska för UI:**
- Ändrade alla metod- och klassnamn till engelska
- Behöll svenska meddelanden för användargränssnittet

**Fördelar:**
- Professionell engelska kod (industry standard)
- Användarvänlig svenska för slutanvändare
- Konsistens genom hela systemet
- Enklare för nya utvecklare

---

## Problem 4: Configuration var hårdkodad

### Vad vi upptäckte:
Konfigurerbara värden var spridda genom koden som "magic numbers":
- `MAX_ATTEMPTS = 3` hårdkodad i handlers
- Sedelvalörer `{100, 200, 500}` hårdkodade i counters
- Ingen central plats för konfiguration

### Varför detta var problematiskt:
```java
// Samma värde duplicerat på flera ställen:
public class AuthenticationHandler {
    final int MAX_ATTEMPTS = 3;  // Hårdkodad
}
public class SimulatedNoteCounter {
    if (valör != 100 && valör != 200 && valör != 500) {  // Hårdkodade värden
}
```

### Vad vi gjorde:
**Centraliserade all konfiguration i `BankConstants`:**
```java
public class BankConstants {
    public static final int MAX_AUTHENTICATION_ATTEMPTS = 3;
    public static final int[] VALID_DENOMINATIONS = {100, 200, 500};
    public static final String CURRENCY_SYMBOL = "kr";
    // Alla andra konstanter...
}
```

**Ersatte alla hårdkodade värden:**
```java
// Nu använder vi konstanter överallt:
while (attempts < BankConstants.MAX_AUTHENTICATION_ATTEMPTS) {
    // ...
}
for (int denomination : BankConstants.VALID_DENOMINATIONS) {
    // ...
}
```

**Fördelar:**
- Enkel att ändra konfiguration på ett ställe
- Ingen duplicering av värden
- Lättare att lägga till nya sedelvalörer
- Miljöspecifik konfiguration möjlig i framtiden

---

## Reflektion kring TDD-processen

Dessa problem illustrerar vikten av kontinuerlig refaktorering i TDD:

1. **Red-Green-Refactor cycle:** Vi fokuserade först på att få testerna att passera (Green), sedan förbättrade vi designen (Refactor)

2. **Emergent Design:** Problem uppstod naturligt när två kodbaser mergades - detta är normalt och förväntad del av utvecklingsprocessen

3. **Test-driven solutions:** Våra tester hjälpte oss identifiera inkonsistenser och gav trygghet under refaktorering

4. **Pragmatisk approach:** Vi löste problem i prioritetsordning - från enklast till svårast

Genom att systematiskt addressera dessa områden har vi förbättrat kodkvaliteten avsevärt medan vi behållit full funktionalitet och testcoverage.