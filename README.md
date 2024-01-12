# Algorytm 5 - Kalkulator Prędkości Kosmicznych API

## Przegląd
Repozytorium zawiera API oparte na Flasku do obliczania pierwszej i drugiej prędkości kosmicznej ciał niebieskich. 
Jest zaprojektowane jako część większego projektu studenckiego.

## Funkcje
- Obliczanie pierwszej i drugiej prędkości kosmicznej dowolnych ciał niebieskich.
- Obsługa predefiniowanych ciał niebieskich o znanych wartościach masy i promienia.
- Możliwość obliczenia prędkości dla niestandardowych ciał niebieskich przy użyciu masy i promienia podanego przez użytkownika.
- Obsługa błędów dla nieprawidłowych danych wejściowych.

## Instalacja

Aby uruchomić API Kalkulatora Prędkości Kosmicznych na lokalnym komputerze, wykonaj następujące kroki:

1. Sklonuj repozytorium.
2. Upewnij się, że masz zainstalowany Python.
3. Zainstaluj Flask: `pip install Flask`.
4. Przejdź do katalogu projektu i uruchom serwer API za pomocą `python app.py`.

## Użycie

### Składanie żądania

API akceptuje żądania POST pod `/space_velocities`. Istnieją dwa sposoby dostarczenia danych do obliczeń prędkości:

1. **Użycie predefiniowanego ciała niebieskiego**: Wyślij obiekt JSON z kluczem `"body_name"`, określając nazwę ciała niebieskiego.

   Przykład:
   ```json
   {
       "body_name": "Mars"
   }
   ```
2. **Użycie niestandardowych wartości**: Wyślij obiekt JSON z kluczami `"mass"` i `"radius"`.

   Przykład:
   ```json
   
       {
           "mass": 6.0e24,
           "radius": 6.4e6
       }
   ```
### Format odpowiedzi

API odpowiada obiektem JSON zawierającym:

    "body_name": Nazwa ciała niebieskiego (jeśli podano) lub null.
    "first_velocity": Obliczona pierwsza prędkość kosmiczna (m/s).
    "second_velocity": Obliczona druga prędkość kosmiczna (m/s).

### Obsługa błędów

W przypadku nieprawidłowego wejścia (np. brakujące dane, wartości ujemne), API odpowiada obiektem JSON zawierającym klucz "error" i kod statusu 400.