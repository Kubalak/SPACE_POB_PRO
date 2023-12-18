package  pl.psk.termdemo.ssh;


import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;
import pl.psk.termdemo.model.keys.KeyInfo;
import pl.psk.termdemo.model.keys.KeyboardHandler;
import pl.psk.termdemo.uimanager.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VT100SSHClientHandler implements Command {

    private final Logger logger = LoggerFactory.getLogger(VT100SSHClientHandler.class);

    private InputStream in;
    private OutputStream out, errout;
    private ChannelSession session;
    private Environment environment;
    private TUIScreen tuiScreen;
    private UIManager uiManager;
    private int ScreenWidth = 80;
    private int ScreenHeight = 24;
    private ExitCallback exitCallback;

    private KeyboardHandler keyboardHandler = new KeyboardHandler();

    private final BlockingQueue<byte[]> messages = new LinkedBlockingQueue<>();

    private Thread receiverThread, senderThread;

    public VT100SSHClientHandler() {
        tuiScreen = new TUIScreen(ScreenWidth, ScreenHeight);  // Inicjalizacja tutaj
    }

    public void init() {
        try {
            tuiScreen.addLayer(0);
            tuiScreen.addLayer(1);
            tuiScreen.addLayer(2);
            tuiScreen.setBgColor(ANSIColors.BG_BRIGHT_BLACK.getCode(), 0);
            UITab tab1 = new UITab("1", 0, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UITab tab3 = new UITab("Third table page", 15, 0, ScreenWidth, ScreenHeight, 0, uiManager);

            //tab do list
            UITab tab4 = new UITab("List view", 35, 0, ScreenWidth, ScreenHeight, 0, uiManager);

            UIBorder border = new UIBorder(1, 1, ScreenWidth - 1, ScreenHeight - 1, 0, uiManager);
            border.setBgColor(ANSIColors.BG_BRIGHT_BLUE.getCode());
            border.setTextColor(ANSIColors.TEXT_WHITE.getCode());
            final String name = "Term emu v0.1";
            // zIndex na 1 wyświetla 1 poziom wyżej. (BUG?)
            UILabel title = new UILabel(name, (ScreenWidth - name.length()) / 2, ScreenHeight - 1, 1, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel label = new UILabel("Press arrow down to activate next field.", 1, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UITextField field = new UITextField(1, 3, 15, 1, 0, uiManager);
            UILabel passLabel = new UILabel("Press arrow down again to activate next component - numeric input", 1, 4, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UITextField passField = new UITextField(1, 5, 15, 1, 0, uiManager);


            List<String> rowContents = new ArrayList<>();
            rowContents.add("1");
            rowContents.add("test");
            rowContents.add("373");
            rowContents.add("2");
            rowContents.add("testdwa");
            rowContents.add("2773");

            List<String> newLabes = new ArrayList<>();
            newLabes.add("ID");
            newLabes.add("Nazwa");
            newLabes.add("Wynik");

            //tabela dajemy x i y, width i height, index , managera i labelki i wiersze
            UITabela tabelaFinal = new UITabela(5, 5, 30, 10, 0, uiManager, newLabes, rowContents);

            //tu sie rysuje
            tabelaFinal.drawAllHeaders(tab3);
            tabelaFinal.drawAllRows(tab3);

            //koniec tabela
            passField.setNumeric(true);
            UILabel infoLabel = new UILabel("Use CTRL + ARROW_RIGHT to move to next tab.", 1, 6, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);


            UITab tab2 = new UITab("Second", 5, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UILabel tab2label = new UILabel("Label for second tab", 1, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel infoLabel2 = new UILabel("Use CTRL + ARROW_LEFT to move to previous tab.", 1, 3, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UIDialogWindow window = new UIDialogWindow(1, 4, 15, 5, 0, "A dialog", uiManager);
            window.setMessage("I ate your grandma");
            UIComboBox uiComboBox = new UIComboBox(10, 15, 15, 5, 0, uiManager, List.of("Daniel", "Kuba", "Patryk"));


            tab1.addComponent(border);
            tab1.addComponent(title);
            tab1.addComponent(label);
            tab1.addComponent(field);
            tab1.addComponent(passLabel);
            tab1.addComponent(passField);
            tab1.addComponent(infoLabel);


            tab2.addComponent(tab2label);
            tab2.addComponent(infoLabel2);
            tab2.addComponent(window);
            tab2.addComponent(uiComboBox);

//            tab3.addComponent(tabelaFinal);

            //Proba listy
            List<String> listComponents = new ArrayList<>();
            listComponents.add("Komponent 1");
            listComponents.add("Komponent 2");
            listComponents.add("Komponent 3");

            UIList listInTabElement = new UIList(5, 5, 0, uiManager, listComponents);
            listInTabElement.setListElementsMargin(3);
            listInTabElement.drawList(tab4);

            uiManager.addTab(tab1);
            uiManager.addTab(tab2);

            uiManager.addTab(tab3);
            uiManager.addTab(tab4);

            uiManager.initialize();
        } catch (Exception e) {
            logger.error("Błąd połączenia" + e.getMessage());
            e.printStackTrace();
        }
    }



    private void updateWindowSize(byte[] buffer, int i) {
        int width = ((buffer[i + 3] & 0xff) << 8) | (buffer[i + 4] & 0xff);
        int height = ((buffer[i + 5] & 0xff) << 8) | (buffer[i + 6] & 0xff);
        ScreenWidth = width;
        ScreenHeight = height;
        logger.info("Received window size: " + width + "x" + height);
    }

    @Override
    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;

    }

    @Override
    public void setErrorStream(OutputStream outputStream) {
        this.errout = outputStream;

    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.in = inputStream;

    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;

        uiManager = new UIManager(tuiScreen, out);  // Inicjalizacja tutaj

    }

    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        this.session = channelSession;
        this.environment = environment;

        Map<String, String> env = environment.getEnv();

        try {
            this.ScreenHeight = Integer.parseInt(env.get("LINES"));
            this.ScreenWidth = Integer.parseInt(env.get("COLUMNS"));
            uiManager.resizeUI(this.ScreenWidth, this.ScreenHeight);
        } catch (NumberFormatException e){
            logger.info(e.getLocalizedMessage());
        }

        init();
        receiverThread = new Thread(this::receiver);
        senderThread = new Thread(this::interpreter);

        receiverThread.start();
        senderThread.start();
    }

    @Override
    public void destroy(ChannelSession channelSession) throws Exception {
        if(receiverThread != null)
            receiverThread.interrupt();
        if(senderThread != null)
            senderThread.interrupt();
    }

    private void receiver(){
        try{
            byte[] buf = new byte[1024];
            int bytesread;
            while((bytesread = in.read(buf)) != -1){
                byte[] tmp = new byte[bytesread];
                System.arraycopy(buf, 0, tmp, 0, bytesread);
                messages.put(tmp);
            }
        } catch (Exception e){
            logger.error(e.getMessage() + e.getLocalizedMessage());
        }
        logger.info("Receiver thread finished!");
    }

    private void interpreter(){
        try {
            while(receiverThread.isAlive() || !messages.isEmpty()) {
                byte[] data = messages.take();
                int[] intData = new int[data.length];
                for(int i=0;i < data.length; ++i)
                    intData[i] = data[i] & 0xFF;

                KeyInfo keyInfo = keyboardHandler.getKeyInfo(intData);

                logger.info("Odebrano sekwencję " + Arrays.toString(intData));

                if(keyInfo != null){
                    logger.info("Odebrano klawisz "+keyInfo);
                    uiManager.handleKeyboardInput(keyInfo);
                }
                else {
                    logger.warn("Nieznana sekwencja klawiszy " + Arrays.toString(intData));
                }

                try {
                    Map<String, String> env = environment.getEnv();
                    int height = Integer.parseInt(env.get("LINES"));
                    int width = Integer.parseInt(env.get("COLUMNS"));
                    if(width != ScreenWidth || height != ScreenHeight) {
                        this.ScreenHeight = height;
                        this.ScreenWidth = width;
                        uiManager.resizeUI(this.ScreenWidth, this.ScreenHeight);
                    }
                } catch (NumberFormatException e){
                    logger.info(e.getLocalizedMessage());
                }
            }
        } catch (InterruptedException e){
            logger.error(e.getLocalizedMessage());
        }
    }
//    public void run() {
//        try {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            List<Byte> byteList = new ArrayList<>();
//            while ((bytesRead = in.read(buffer)) != -1) {
//                for (int i = 0; i < bytesRead; i++) {
//                    byteList.add(buffer[i]);
//                }
//                interpretReceivedData(byteList);
//                byteList.clear();  // clear the list after interpreting the received data
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void interpretReceivedData(List<Byte> data) throws IOException {
//        byte[] bytes = new byte[data.size()];
//        for (int i = 0; i < data.size(); i++) {
//            bytes[i] = data.get(i);
//        }
//        String received = new String(bytes, StandardCharsets.UTF_8);
//
//        logger.debug("Odebrano (bajty): " + data.size() + ", Dane: " + Arrays.toString(bytes));
//
//        int[] intData = new int[bytes.length];
//        for (int i = 0; i < bytes.length; i++) {
//            intData[i] = bytes[i] & 0xFF;
//        }
//        KeyInfo keyInfo = keyboardHandler.getKeyInfo(intData);
//        if (keyInfo != null) {
//            logger.debug("Odebrano klawisz: " + keyInfo.toString());
//            uiManager.handleKeyboardInput(keyInfo);
//        } else if (intData.length == 9) {
//
//            logger.warn("Nieznana sekwencja: " + Arrays.toString(intData));
//        } else
//            logger.warn("Nieznana sekwencja klawiszy: " + Arrays.toString(intData));
//    }

}

