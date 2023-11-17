## Uruchomienie projektu

- IntelliJ Idea:
    - Nacisnąć przycisk &#9654;&#65039; w oknie programu z wybraną konfiguracją<br/>
    ![Run IntelliJ](help/run_intellij.jpg)
    - W przypadku błędnej konfiguracji zmienić wersję SDK na działającą w systemie (lub ją doinstalować)
    - Ewentualnie uruchomić metodę `main` z klasy `App`<br/>
    ![Run main](help/run_main.jpg)
- VS Code:
    - Otworzyć główny katalog w VS Code, a następnie przejść do pliku [App.java](src/main/java/pl/psk/termdemo/App.java) i wybrać opcję `Run Java`<br/>![Run app image](help/run.jpg)

## Plik jar
Aby utworzyć wykonywalny plik jar należy wybrać opcję `package` z menu `Lifecycle`
![Maven lifecycle](help/maven.jpg)

W katalogu `target` pojawią dwa pliki:
- term-emu-0.1-SNAPSHOT.jar
- term-emu-0.1-SNAPSHOT-jar-with-dependencies.jar

Aby uruchomić serwer należy w wierszu poleceń wpisać:<br/>
`java -jar term-emu-0.1-SNAPSHOT-jar-with-dependencies.jar`

![Running server](help/running.jpg)

## Połączenie z serwerem
Do połączenia z serwerem można wykorzystać dowolny program obsługujący połączenie za pomocą protokołu Telnet (SSH obecnie nie jest dostępne) np. [PuTTY](https://www.putty.org/)

Parametry do połączenia z serwerem na przykładzie programu PuTTY:

![Parametry PuTTY](help/putty.jpg)

Po połączeniu z serwerem wyświetlona zostanie wiadomość powitalna

![Hello](help/hello.jpg)

Po 3 sekundach konsola zmieni swój wygląd i wyświetlone zostanie okno dialogowe.

![Dialog](help/dialog.jpg)

Kombinacja klawiszy `CTRL + I` pozwala na wybór przycisku w menu dialogowym.

![Dialog select](help/dialog_select.jpg)

W tej wersji okno dialogowe jest martwe.<br/>
Aby rozłączyć się z serwerem trzeba zamknąć okno programu, który jest podłączony do serwera (`CTRL + C` nic nie da)<br/>
<i>That's all folks!</i>