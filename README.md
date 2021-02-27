### Uwagi

Kod sformatowano z użyciem google-java-format.

Ze względu na nieścisłości/wątpliwości przyjęto następujące założenia:

* pobieranie danych **na żądanie** powoduje nadpisanie wszystkich postów
* pobieranie danych **cykliczne** aktualizuje wpisy, ale nigdy nie tworzy nowych
* cykliczne pobieranie następuje raz dziennie o godzinie 14:00

Godzinę cyklicznego pobierania można zmienić, modyfikując wyrażenie CRON w application.properties (zmieniając wartości 0 0 14 na pożądaną sekundę/minutę/godzinę):  
`scheduling.post-update.cron=0 0 14 * * *`

### Instrukcja uruchomienia

Dla uproszczenia wykorzystano bazę danych H2.  
Można wykorzystać bazę bez instalacji, za pośrednictwem Spring Boot:
* in-memory (nietrwała), definiując URL wg wzoru: `jdbc:h2:mem:nazwa-bazy`
* file-based, definiując URL wg wzoru: `jdbc:h2:file:~/nazwa-bazy`  
**Alternatywnie**  
H2 należy zainstalować (http://www.h2database.com/html/main.html)
i za pośrednictwem H2 Database Engine utworzyć nową bazę i uruchomić ją,
logując się w H2 console.

##### Należy zdefiniować dane do połączenia z bazą, tworząc zmienne środowiskowe:

* APZUMI_DATASOURCE_URL
* APZUMI_DATASOURCE_USERNAME
* APZUMI_DATASOURCE_PASSWORD

lub dla uproszczenia ręcznie modyfikując odpowiednie właściwości w pliku application.properties:

* spring.datasource.url
* spring.datasource.username
* spring.datasource.password

###### Przykładowe wartości dla zmiennych środowiskowych/właściwości:

`spring.datasource.url=jdbc:h2:tcp://localhost/~/apzumi-db`  
`spring.datasource.username=sa`  
`spring.datasource.password=sa`

#### Aplikację można uruchomić z poziomu katalogu projektu poleceniem: `mvnw spring-boot:run`
