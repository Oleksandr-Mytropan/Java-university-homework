# Cvičení 06 - realistické jednotkové testování

Cvičení navazuje na třídy vytvořené v minulých cvičeních, k dispozici je `BGenericList` pro uchovávání dat a `MemoryLoggerWithHandlers` pro logování událostí vzniklých při činnosti aplikace.

V rámci tohoto cvičení si vyzkoušíte znovu vytvořit testy, které již bylo Vaším úkolem vytvořit v předchozích cvičeních. Nyní však s užitím frameworku **JUnit 5** a **Mockito**. JUnit je jedním z více produkčně užívaných testovacích frameworků, jeho testy mají podobnou strukturu jako testy, které jsme vytvářeli v minulosti, ale je zde několik odlišností:

- testy se vytvářejí do tříd, které jsou organizačně umístěny v `Test Packages`, fyzicky ve složce `src/test/`
- jeden test odpovídá jedné metodě, která je anotována `@Test` (testy jsou automaticky vyhledány pomocí reflexe)
- v rámci životního cyklu testů může třída definovat metody, které jsou provedeny před/po samotném testu pro přípravu/úklid testovacího prostředí
- pro spouštění testů build systém Maven implementuje specifické cíle a pluginy

## MemoryLoggerWithHandlers

Pro třídu `MemoryLoggerWithHandlers` (v souboru `MemoryLoggerWithHandlersTest.java`):

- cílem je ověřit odbobné situace jako v předcházejích cvičeních, nyní s JUnit a Mockito
- v připraveném souboru jsou vytvořeny všechny nezbytné `import` statements, realizace testů je na Vás

1. **Test 01** - stub + spy
    - vytvořte mock pro `DateTimeProvider`, který poskytne zvolené datum
    - vytvořte mock pro `LogMessageHandler`
    - vytvořte `MemoryLoggerWithHandlers`, přidejte mockovaný handler se severity level `Debug`
    - zavolejte `logMessage(LogMessageSeverity.Error, "message")`
    - ověřte, že poslední zalogovaná zpráva má odpovídající datum
    - ověřte, že poslední zalogovaná zpráva má odpovídající severity level
    - ověřte, že poslední zalogovaná zpráva má odpovídající text
    - ověřte, že u mockovaného handleru došlo k zavolání `handle()` s objektem `logger.getLastLogMessage()`

2. **Test 02** - stub s více výstupy
    - vytvořte mock pro `DateTimeProvider`, který poskytne dvě různá zvolená data
    - vytvořte `MemoryLoggerWithHandlers`
    - zavolejte `logMessage(LogMessageSeverity.Error, "message 1")`
    - zavolejte `logMessage(LogMessageSeverity.Error, "message 2")`
    - ověřte, že první zalogovaná zpráva má datum odpovídající prvnímu zvolenému datu
    - ověřte, že druhá zalogovaná zpráva má datum odpovídající druhému zvolenému datu

3. **Test 03** - volaný spy + nevolaný spy
    - vytvořte mock pro `DateTimeProvider`, který poskytne zvolené datum
    - vytvořte mock 1 pro `LogMessageHandler`
    - vytvořte mock 2 pro `LogMessageHandler`
    - vytvořte `MemoryLoggerWithHandlers`
    - přidejte mock 1 jako handler se severity level `Warning`
    - přidejte mock 2 jako handler se severity level `Error`
    - zavolejte `logMessage(LogMessageSeverity.Warning, "message")`
    - ověřte, že u mockovaného handleru 1 došlo k zavolání `handle()` s libovolným objektem typu `LogMessage` v parametru
    - ověřte, že u mockovaného handleru 2 nedošlo k zavolání `handle()`

4. **Test 04** - volaný spy + volaný spy
    - vytvořte mock pro `DateTimeProvider`, který poskytne zvolené datum
    - vytvořte mock 1 pro `LogMessageHandler`
    - vytvořte mock 2 pro `LogMessageHandler`
    - vytvořte `MemoryLoggerWithHandlers`
    - přidejte mock 1 jako handler se severity level `Warning`
    - přidejte mock 2 jako handler se severity level `Error`
    - zavolejte `logMessage(LogMessageSeverity.Error, "message")`
    - ověřte, že u mockovaného handleru 1 došlo k zavolání `handle()` s libovolným objektem typu `LogMessage` v parametru
    - ověřte, že u mockovaného handleru 2 došlo k zavolání `handle()` s libovolným objektem typu `LogMessage` v parametru

## BGenericList

Pro třídu `BGenericList` (soubor s testy vytvořte):

- vytvářejte testy pro typ `BGenericList<String>`, instanci si uložte jako atribut testovací třídy
- všechny níže popsané testy využívají naplněný seznam, který obsahuje prvky `"one", "two", "three", "four"`, inicializaci proveďte pro každý test pomocí metody s `@BeforeEach`

1. **Test 01** - `get(0)` vrací `"one"`
2. **Test 02** - `size()` vrací `4`
3. **Test 03** - `get(-1)` vyvolává výjimku typu `IllegalArgumentException`
4. **Test 04** - `get(5)` vyvolává výjimku typu `IllegalArgumentException`
5. **Test 05** - parametrický test pro `get(0 .. 4)` vrací `"one" .. "four"`, realizujte jako parametrický test se dvěma parametry (index, očekávaný výsledek), využijte `@ParametrizedTest` a `@CsvSource`
6. **Test 06** - `add("five")`, po provedení musí být `size() == 5` a `get(4) ~= "five"`
7. **Test 07** - `asReadOnly()` vrací kopii listu, která je neměnitelná, ověřte, že vrácený objekt je instancí třídy `BGenericList.BReadOnlyList`, využijte `assertInstanceOf(..)`
8. **Test 08** - `asReadOnly()` obsahuje stejná data jako původní list, ověřte pomocí `assertIterableEquals(..)`, pro ověření zkonstruujte list s očekávanými prvky
9. **Test 09** - variace testu 08, po získání `asReadOnly()` kopie listu, do původního listu přidejte další prvek `add("five")` a pomocí `assertIterableEquals(..)` ověřte, že v read-only kopii nedošlo ke změně a stále obsahuje pouze 4 původní prvky
10. **Test 10** - ověřte, že `asReadOnly()` skutečně vrací neměnitelný seznam - každá z operací `add(..)`, `set(..)`, `remove(..)` musí vyvolat výjimku `UnsupportedOperationException`
