package pl.psk.termdemo;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;
import pl.psk.termdemo.uimanager.*;
import pl.psk.termdemo.model.keys.KeyInfo;
import pl.psk.termdemo.model.keys.KeyboardHandler;
import pl.psk.termdemo.uimanager.*;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static final int PORT = 2137;

    // IAC (Interpret As Command) - Wartość 0xff oznacza początek komendy interpretowanej jako polecenie.
// Jest to kluczowy element protokołu komunikacyjnego wykorzystywanego w terminalach do negocjacji i wykonywania różnych operacji.
    public static final int IAC = 0xff;

    // IAC_WILL - Wartość 0xfb oznacza komunikat "WILL" w protokole IAC, który jest używany do negocjacji obsługiwanych funkcji.
// Na przykład, jedna strona może "WILL" obsługiwać pewną funkcję, a druga strona może zdecydować, czy ją akceptować.
    public static final int IAC_WILL = 0xfb;

    // IAC_WONT - Wartość 0xfc oznacza komunikat "WONT" w protokole IAC, który jest używany do negocjacji obsługiwanych funkcji.
// Jest to odpowiedź na "WILL" i może oznaczać odrzucenie obsługi danej funkcji.
    public static final int IAC_WONT = 0xfc;

    // IAC_DO - Wartość 0xfd oznacza komunikat "DO" w protokole IAC, który jest używany do negocjacji obsługiwanych funkcji.
// Podobnie jak "WILL," ta komenda służy do zgłaszania gotowości do obsługi konkretnej funkcji.
    public static final int IAC_DO = 0xfd;

    // IAC_DONT - Wartość 0xfe oznacza komunikat "DONT" w protokole IAC, który jest używany do negocjacji obsługiwanych funkcji.
// Jest to odpowiedź na "DO" i może oznaczać odmowę obsługi danej funkcji.
    public static final int IAC_DONT = 0xfe;

    // IAC_ECHO - Wartość 0x01 oznacza komunikat "ECHO" w protokole IAC, który kontroluje, czy dane wysłane do terminala są również wyświetlane na ekranie (echo).
// Może być używany do włączania lub wyłączania trybu echo.
    public static final int IAC_ECHO = 0x01;

    // IAC_SGA - Wartość 0x03 oznacza komunikat "Suppress Go Ahead" w protokole IAC, który kontroluje, czy terminal wysyła "Go Ahead" po każdej komendzie.
// Ten komunikat może pomagać w poprawie efektywności komunikacji.
    public static final int IAC_SGA = 0x03;

    // BS (Backspace) - Wartość 0x08 reprezentuje znak wstecz (backspace), który usuwa jeden znak w lewo.
// Jest to często używany znak sterujący do korekty tekstu na ekranie.
    public static final int BS = 0x08;

    // CR (Carriage Return) - Wartość 0x0d reprezentuje znak powrotu karetki (carriage return), który przenosi kursor na początek linii.
// Jest to używane do rozpoczęcia wpisywania tekstu od początku bieżącej linii.
    public static final int CR = 0x0d;

    // ESC (Escape) - Wartość 0x1b reprezentuje znak ucieczki (escape), który rozpoczyna kontrolne sekwencje znaków.
// Jest to często wykorzystywane do uruchamiania specjalnych poleceń lub operacji.
    public static final int ESC = 0x1b;

    // DEL (Delete) - Wartość 0x7f reprezentuje znak usunięcia (delete), który jest używany do usuwania znaków.
// Może być używany do implementacji operacji usuwania znaków w tekście.
    public static final int DEL = 0x7f;

    // IAC_BINARY - Wartość 0x00 oznacza tryb binarny w protokole IAC, w którym dane są traktowane bez konwersji.
// Jest to używane do przekazywania danych w czystej postaci, bez interpretacji znaków kontrolnych.
    public static final int IAC_BINARY = 0x00;

    // IAC_NAWS - Wartość 0x1f oznacza komunikat "Negotiate About Window Size" w protokole IAC, używany do negocjacji rozmiaru okna terminala.
// Jest to przykład komunikatu używanego do dostosowywania interfejsu terminala do aktualnych wymiarów.
    public static final int IAC_NAWS = 0x1f;

    // IAC_SB - Wartość 0xfa oznacza początek podkomendy w protokole IAC.
// Podkomendy są używane do przekazywania dodatkowych informacji lub ustawień wewnątrz komunikatów IAC.
    public static final int IAC_SB = 0xfa;

    // IAC_SE - Wartość 0xf0 oznacza koniec podkomendy w protokole IAC.
// Jest to znak kończący podkomendę rozpoczętą komunikatem IAC_SB.
    public static final int IAC_SE = 0xf0;

    // CSI (Control Sequence Introducer) - Wartość 0x5b reprezentuje znak wprowadzający sekwencję kontrolną.
// Jest to często używane w sekwencjach kontrolnych, które wykonywują różne akcje w terminalu.
    public static final int CSI = 0x5b;

    // CSI_FINAL_BEGIN - Wartość 0x40 oznacza początek sekwencji kontrolnej.
// Jest to znak rozpoczynający sekwencję kontrolną po znaku wprowadzającym CSI.
    public static final int CSI_FINAL_BEGIN = 0x40;

    // CSI_FINAL_END - Wartość 0x7e oznacza koniec sekwencji kontrolnej.
// Jest to znak kończący sekwencję kontrolną po znaku wprowadzającym CSI.
    public static final int CSI_FINAL_END = 0x7e;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Serwer uruchomiony na porcie: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                if (in == null || out == null) {
                    logger.error("Błąd połączenia");
                    return;
                }

                new VT100ClientHandler(in, out).start();
            }
        } catch (Exception e) {
           logger.error("Błąd serwera: " + e.getMessage());
        }
    }
}

class VT100ClientHandler extends Thread {

    private  final Logger logger = LoggerFactory.getLogger(VT100ClientHandler.class);

    private InputStream in;
    private OutputStream out;
    private TUIScreen tuiScreen;
    private UIManager uiManager;
    private int ScreenWidth = 80;
    private int ScreenHeight = 24;

    private KeyboardHandler keyboardHandler = new KeyboardHandler();

    public VT100ClientHandler(InputStream in, OutputStream out) {
        if (in == null || out == null) {
            logger.error("Błąd połączenia");
            return;
        }

        this.in = in;
        this.out = out;

        tuiScreen = new TUIScreen(ScreenWidth, ScreenHeight);  // Inicjalizacja tutaj
        uiManager = new UIManager(tuiScreen, out);  // Inicjalizacja tutaj
    }

    private void enableMouseTracking() throws IOException {
        out.write("\033[?9h".getBytes());
        out.write("\033[?1005h".getBytes());
        out.flush();
    }

    private void setWindowSize(int x, int y) throws IOException {
        String resizeCommand = String.format("\033[8;%d;%dt", y, x);
        out.write(resizeCommand.getBytes());
        out.flush();
    }

    private void disableMouseTracking() throws IOException {
        // Wyłączanie trybu śledzenia myszy X10
        out.write("\033[?9l".getBytes());
        out.flush();
    }

    @Override
    public void run() {
        try {
            setupConnection();

            tuiScreen.addLayer(0);
            tuiScreen.addLayer(1);
            tuiScreen.addLayer(2);
            tuiScreen.setBgColor(ANSIColors.BG_BLUE.getCode(), 0);
            UIBorder border = new UIBorder(0, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            border.setBgColor(ANSIColors.BG_BLUE.getCode());
            border.setTextColor(ANSIColors.TEXT_WHITE.getCode());
            border.show();
            uiManager.render();
            //      uiManager.refresh(out);

            UIBar bar = new UIBar(0, 0, ScreenWidth, 1, "Example upper bar", UIBar.TextAlign.CENTER, uiManager);
            bar.setBgColor(ANSIColors.BG_RED.getCode());
            bar.show();

            UIBar bar1 = new UIBar(0, 23, ScreenWidth, 1, "Example lower bar", UIBar.TextAlign.CENTER, uiManager);
            bar1.setTextColor(ANSIColors.getXtermTextColor(179));
            bar1.setBgColor(ANSIColors.BG_WHITE.getCode());
            bar1.show();

            uiManager.render();
            uiManager.refresh();

            UITextField textField = new UITextField(3, 3, 20, 1, 1, uiManager);
            textField.setPassword(true);
            textField.show();

            UITextField textField1 = new UITextField(3, 5, 20, 1, 1, uiManager);
            textField1.show();


            UIProgressBar progressBar = new UIProgressBar(3, 7, 20, 1, 1, uiManager);
            progressBar.setProgress(0.5);
            progressBar.show();
            uiManager.render();
            uiManager.refresh();
            sleep(3000);
            progressBar.setProgress(0.8);
            uiManager.render();
            uiManager.refresh();


//            List<String> items = Arrays.asList("Apple", "Banana", "Cherry", "Date", "Elderberry");
//
//            // Inicjalizacja UIComboBox
//            UIComboBox comboBox = new UIComboBox(5, 8, 20, 1, 2, uiManager, items);
//            // Rejestracja i wyświetlenie comboBox
//            comboBox.show();
//
//            // Rysowanie interfejsu użytkownika
//            uiManager.render();
//            uiManager.refresh();  // Załóżmy, że UIManager ma metodę refresh()


            UIBar bar3 = new UIBar(0, 12, ScreenWidth, 0, "Example upper bar", UIBar.TextAlign.CENTER, uiManager);
            bar3.setBgColor(ANSIColors.BG_RED.getCode());
            bar3.show();
            uiManager.render();
            uiManager.refresh();  // Załóżmy, że UIManager ma metodę refresh()


            textField1.hide();

            textField.hide();



            UIButton button = new UIButton(3, 2, 20, 1, 2, "Kliknij mnie", () -> {
                System.out.println("Kliknięto przycisk 1");
            }, uiManager);

            UIDialogWindow UIDialogWindow = new UIDialogWindow(1, 1, 30, 10, 2, "Dialog", uiManager);
            UIDialogWindow.setMessage("To jest wiadomość w oknie dialogowym");
            UIDialogWindow.addButton(button);
            UIDialogWindow.show();
            uiManager.render();
            uiManager.refresh();

        } catch (Exception e) {
            logger.error("Błąd połączenia"+ e.getMessage());

        }
    }

    public void renderLogo(String fileName, int startX, int startY) throws IOException {
        // Wczytanie ASCII art
        List<String> asciiArtLines = Files.readAllLines(Paths.get(fileName));

        // Iteracja przez linie i znaki
        for (int y = 0; y < asciiArtLines.size(); y++) {
            String line = asciiArtLines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                ScreenCell cell = new ScreenCell(c, ANSIColors.TEXT_WHITE.getCode(), ANSIColors.BG_BLUE.getCode());
                tuiScreen.addPixelToLayer(x + startX, y + startY, 2, cell);
            }
        }
    }

    @SneakyThrows
    private void setupConnection() throws IOException {
        negotiate();
        enableMouseTracking();
        new ReceiverThread().start();
    }

    @SneakyThrows
    private void negotiate() throws IOException {
        sendInitialNegotiationCommands();
        setInitialWindowSize();
        requestWindowSizeFromClient();

        readAndProcessIncomingData();
    }

    private void sendInitialNegotiationCommands() throws IOException {
        out.write(new byte[]{(byte) App.IAC, (byte) App.IAC_WILL, (byte) App.IAC_ECHO});
        out.write(new byte[]{(byte) App.IAC, (byte) App.IAC_WILL, (byte) App.IAC_SGA});
    }

    private void setInitialWindowSize() throws InterruptedException, IOException {
        setWindowSize(80, 24);
        sleep(1000);
    }

    private void requestWindowSizeFromClient() throws IOException {
        out.write(new byte[]{(byte) App.IAC, (byte) App.IAC_DO, (byte) App.IAC_NAWS});
    }

    private void readAndProcessIncomingData() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = in.read(buffer)) != -1) {
            for (int i = 0; i < bytesRead; i++) {
                if (buffer[i] == (byte) App.IAC) {
                    byte command = buffer[i + 1];
                    switch (command) {
                        case (byte) App.IAC_DO:
                        case (byte) App.IAC_DONT:
                        case (byte) App.IAC_WILL:
                            break;
                        case (byte) App.IAC_SB:
                            if (buffer[i + 2] == (byte) App.IAC_NAWS) {
                                updateWindowSize(buffer, i);
                                return;
                            }
                            break;
                    }
                    i += 2; // Skip the command and option bytes
                }
            }
        }
    }

    private void updateWindowSize(byte[] buffer, int i) {
        int width = ((buffer[i + 3] & 0xff) << 8) | (buffer[i + 4] & 0xff);
        int height = ((buffer[i + 5] & 0xff) << 8) | (buffer[i + 6] & 0xff);
        ScreenWidth = width;
        ScreenHeight = height;
        logger.info("Received window size: " + width + "x" + height);
    }


    private class ReceiverThread extends Thread {


        private static final int MOUSE_SEQUENCE_OFFSET = 32;
        private static final int MOUSE_BUTTON_MASK = 0x03;

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                int bytesRead;
                List<Byte> byteList = new ArrayList<>();
                while ((bytesRead = in.read(buffer)) != -1) {
                    for (int i = 0; i < bytesRead; i++) {
                        byteList.add(buffer[i]);
                    }
                    interpretReceivedData(byteList);
                    byteList.clear();  // clear the list after interpreting the received data
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void interpretReceivedData(List<Byte> data) throws IOException {
            byte[] bytes = new byte[data.size()];
            for (int i = 0; i < data.size(); i++) {
                bytes[i] = data.get(i);
            }
            String received = new String(bytes, StandardCharsets.UTF_8);

            logger.debug("Odebrano (bajty): " + data.size() + ", Dane: " + Arrays.toString(bytes));

            if (received.startsWith("\033[M")) {
                handleMousePosition(bytes);
            } else {
                int[] intData = new int[bytes.length];
                for (int i = 0; i < bytes.length; i++) {
                    intData[i] = bytes[i] & 0xFF;
                }
                KeyInfo keyInfo = keyboardHandler.getKeyInfo(intData);
                if (keyInfo != null) {
                    logger.debug("Odebrano klawisz: " + keyInfo.toString());
                    uiManager.handleKeyboardInput(keyInfo);
                } else {
                    logger.warn("Nieznana sekwencja klawiszy: " + Arrays.toString(intData));
                }
            }

        }



        private void handleMousePosition(byte[] sequence) throws IOException {
            int cb = sequence[3] - MOUSE_SEQUENCE_OFFSET;

            int x = decodeCoordinate(sequence, 4);
            int y = decodeCoordinate(sequence, 5);

            int button = cb & MOUSE_BUTTON_MASK; // Extracting least significant 2 bits
            String buttonStr = decodeMouseButton(button);

            if (button == 0 || button == 1 || button == 2) {
                uiManager.handleMouseClick(x, y);
            }

            logger.info("Pozycja myszy: X=" + x + ", Y=" + y + ", Przycisk: " + buttonStr);
        }

        private int decodeCoordinate(byte[] sequence, int index) {
            int coordinate = sequence[index] & 0xFF;
            coordinate -= MOUSE_SEQUENCE_OFFSET;
            return coordinate;
        }

        private String decodeMouseButton(int buttonCode) {
            return switch (buttonCode) {
                case 0 -> "Lewy przycisk";
                case 1 -> "Środkowy przycisk";
                case 2 -> "Prawy przycisk";
                default -> "Nieznany przycisk";
            };
        }

    }
}

