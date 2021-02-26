###Uwagi
Kod sformatowano z użyciem google-java-format.

Ze względu na nieścisłości/wątpliwości przyjęto następujące założenia:
* pobieranie danych **na żądanie** powoduje nadpisanie wszystkich postów
* pobieranie danych **cykliczne** aktualizuje wpisy, ale nigdy nie tworzy nowych
* cykliczne pobieranie następuje raz dziennie o godzinie 14:00

###Instrukcja uruchomienia
Dla uproszczenia wykorzystano bazę danych H2.
