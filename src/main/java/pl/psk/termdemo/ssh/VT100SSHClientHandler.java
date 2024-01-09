package  pl.psk.termdemo.ssh;


import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.Signal;
import org.apache.sshd.server.SignalListener;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;
import pl.psk.termdemo.model.keys.KeyInfo;
import pl.psk.termdemo.model.keys.KeyLabel;
import pl.psk.termdemo.model.keys.KeyboardHandler;
import pl.psk.termdemo.uimanager.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Klasa obsługująca sesję SSH.
 */


public class VT100SSHClientHandler implements Command {


    public static double withMathRound(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

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

    /**
     * Domyślny konstruktor. Inicjuje TUIScreen.
     */
    public VT100SSHClientHandler() {
        tuiScreen = new TUIScreen(ScreenWidth, ScreenHeight);  // Inicjalizacja tutaj
    }

    /**
     * Inicjuje ekran - tworzy komponenty i ustawia je na odpowiednich pozycjach.
     */
    public void init() {
        try {
            tuiScreen.addLayer(0);
            tuiScreen.addLayer(1);
            tuiScreen.addLayer(2);
            tuiScreen.addLayer(3);
            tuiScreen.setBgColor(ANSIColors.BG_BRIGHT_BLACK.getCode(), 0);
            UITab tab1 = new UITab("Algo 5", 0, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UITab tab3 = new UITab("Algo 3", 20, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UITab tab5 = new UITab("Algo 4", 30, 0, ScreenWidth, ScreenHeight, 0, uiManager);

            //tab do list
//            UITab tab4 = new UITab("List view", 60, 0, ScreenWidth, ScreenHeight, 0, uiManager);

            UIBorder border = new UIBorder(1, 1, ScreenWidth - 1, ScreenHeight - 1, 0, uiManager);
            border.setBgColor(ANSIColors.BG_BRIGHT_BLUE.getCode());
            border.setTextColor(ANSIColors.TEXT_WHITE.getCode());
            final String name = "Term emu v0.1";
            // zIndex na 1 wyświetla 1 poziom wyżej. (BUG?)
            UILabel title = new UILabel(name, (ScreenWidth - name.length()) / 2, ScreenHeight - 1, 1, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel label = new UILabel("Press arrow down to activate next field, and input planet name.", 3, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UITextField planetInput = new UITextField(3, 3, 15, 1, 0, uiManager);
            UILabel weightLabel = new UILabel("Press arrow down again to activate next component - numeric input", 3, 4, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel weightLabel2 = new UILabel("testowy", 3, 4, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UITextField weightInput = new UITextField(3, 5, 15, 1, 0, uiManager);
            weightInput.setNumeric(true);


            //algo4

            //tworzenie inputow do danych
            UILabel initPos1Label = new UILabel("Init position 1", 3, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initPos2Label = new UILabel("Init position 2", 3, 5, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initPost3Label = new UILabel("Init position 3", 3, 8, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initVel1Label = new UILabel("Init velocity 1", 3, 11, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initVel2Label = new UILabel("Init velocity 2", 3, 14, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initVel3Label = new UILabel("Init velocity 3", 3, 17, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel timeStepsLabel = new UILabel("Timesteps", 3, 20, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel startTimeLabel = new UILabel("Start time.", 3, 23, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel timeDeltaMinutesLabel = new UILabel("Time delta minutes", 3, 26, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UITextField initPos1 = new UITextField(3, 3, 15, 1, 0, uiManager);
            UITextField initPos2 = new UITextField(3, 6, 15, 1, 0, uiManager);
            UITextField initPos3 = new UITextField(3, 9, 15, 1, 0, uiManager);
            UITextField initVel1 = new UITextField(3, 12, 15, 1, 0, uiManager);
            UITextField initVel2 = new UITextField(3, 15, 15, 1, 0, uiManager);
            UITextField initVel3 = new UITextField(3, 18, 15, 1, 0, uiManager);
            UITextField timeSteps = new UITextField(3, 21, 15, 1, 0, uiManager);
            UITextField startTime = new UITextField(3, 24, 15, 1, 0, uiManager);
            UITextField timeDeltaMinutes = new UITextField(3, 27, 15, 1, 0, uiManager);
            initPos1.setNumeric(true);
            initPos2.setNumeric(true);
            initPos3.setNumeric(true);
            initVel1.setNumeric(true);
            initVel2.setNumeric(true);
            initVel3.setNumeric(true);
            timeSteps.setNumeric(true);
            timeDeltaMinutes.setNumeric(true);


            UILabel infoLabel = new UILabel("Use CTRL + ARROW_RIGHT to move to next tab.", 2, 28, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);


            UITab tab2 = new UITab("Algo 1", 10, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UILabel tab2label = new UILabel("Label for second tab", 1, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel infoLabel2 = new UILabel("Use CTRL + ARROW_LEFT to move to previous tab.", 2, 27, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UIComboBox comboBox= new UIComboBox(2, 2, 15, 5, 0, uiManager, List.of("Ziemia", "Mars", "Jowisz", "Wenus", "Merkury", "Saturn", "Uran", "Neptun", "Pluton"));
            // Display the velocities in a UILabel


            // ALGO 5
            UIButton apiButton = new UIButton(
                    2, 4, 15, 5, 0, "Calculate space Velocities",
                    () -> {
                        try {
                            //String fieldValue = field.getText(); // Example: Text from the 'field' UITextField
                            String fieldValue = comboBox.getSelectedValue();

                            JSONObject requestData = new JSONObject();
                            requestData.put("body_name", fieldValue);
                            // Include other fields as needed in the JSON request data

                            URL url = new URL("http://127.0.0.1:5000/space_velocities");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setDoOutput(true);

                            // Send JSON data from the form
                            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                            wr.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
                            wr.flush();
                            wr.close();

                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            // Process the response JSON and display in a UILabel
                            JSONObject jsonResponse = new JSONObject(response.toString());
                            double firstVelocity = jsonResponse.getDouble("first_velocity");
                            double secondVelocity = jsonResponse.getDouble("second_velocity");

                            // Handle the response or trigger actions based on the received data
                            UILabel velocitiesLabel = new UILabel("First Velocity: " + firstVelocity + ", Second Velocity: " + secondVelocity, 15, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
                            tab1.addComponent(velocitiesLabel);

                        } catch (IOException e) {
                            // Handle any IO-related exceptions (e.g., network issues, connection problems)
                            // Log the exception or perform error handling as needed
                            e.printStackTrace(); // Print the stack trace for debugging purposes
                        } catch (JSONException e) {
                            // Handle JSON-related exceptions (e.g., parsing errors)
                            // Log the exception or perform error handling as needed
                            e.printStackTrace(); // Print the stack trace for debugging purposes
                        } catch (Exception e) {
                            // Handle other types of exceptions
                            // Log the exception or perform error handling as needed
                            e.printStackTrace(); // Print the stack trace for debugging purposes
                        }
                    },
                    uiManager
            );

            UIButton distanceButton = new UIButton(
                    3, 5, 15, 5, 0, "Calculate Distance",
                    () -> {
                        try {
                            String planetName = planetInput.getText(); // Example: You can change this to the desired planet name

                            JSONObject requestData = new JSONObject();
                            requestData.put("planet_name", planetName);

                            URL url = new URL("http://127.0.0.1:5000/distance/" + planetName.toLowerCase());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setDoOutput(true);

                            // Send GET request to the server
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            // Handle the response from the server
                            JSONObject jsonResponse = new JSONObject(response.toString());
                            double distanceKm = jsonResponse.getDouble("distance_km");
                            double distanceAu = jsonResponse.getDouble("distance_au");

                            UILabel distances = new UILabel("distance_km: " + distanceKm + ", distance_au: " + distanceAu, 3, 7, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
                            tab2.addComponent(distances);


                        } catch (IOException e) {
                            // Handle exceptions
                            e.printStackTrace();
                        }
                    },
                    uiManager
            );

            UIButton weightButton = new UIButton(
                    3, 7, 15, 5, 0, "Calculate Weight",
                    () -> {
                        try {
                            String planet = planetInput.getText();
                            double weightOnEarth = weightInput.getNumber(); // Example: You can change this to the weight on Earth

                            JSONObject requestData = new JSONObject();
                            requestData.put("weight_on_earth", weightOnEarth);
                            requestData.put("planet", planet); // Example: You can change this to the desired planet

                            URL url = new URL("http://127.0.0.1:5000/calculate_weight");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setDoOutput(true);

                            // Send JSON data to the server
                            OutputStream os = connection.getOutputStream();
                            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
                            os.flush();
                            os.close();

                            // Handle the response from the server
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            // Process the response JSON and display the calculated weight on the planet
                            JSONObject jsonResponse = new JSONObject(response.toString());
                            double weightOnPlanet = jsonResponse.getDouble("weight_on_planet");
                            System.out.println("Weight on Planet: " + weightOnPlanet);

//                            UILabel weight = new UILabel("Weight on planet: " + weightOnPlanet, 1, 7, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
//                            tab3.addComponent(weight);
                            UIDialogWindow dialog = new UIDialogWindow(25, 10, 40, 10, 0, "Attention", uiManager);
                            dialog.setMessage("Weight on planet: " + weightOnPlanet);
                            tab3.addComponent(dialog);

                        } catch (IOException e) {
                            // Handle exceptions
                            e.printStackTrace();
                        }
                    },
                    uiManager
            );

            // ALGO 4
            UIButton algo4Button = new UIButton(
                    30, 5, 15, 5, 0, "Calculate algo4",
                    () -> {
                        try {


                            // pobranie danych z inputow
                            Integer timeStepsInt = (int) timeSteps.getNumber();
                            String startTimeString = startTime.getText();
                            Integer timeDeltaMinutesDouble = (int) timeDeltaMinutes.getNumber();

                            Double[] initialPositionsArray = new Double[3];
                            initialPositionsArray[0] = initPos1.getNumber();
                            initialPositionsArray[1] = initPos2.getNumber();
                            initialPositionsArray[2] = initPos3.getNumber();


                            Double[] initialVelocityArray = new Double[3];
                            initialVelocityArray[0] = initVel1.getNumber();
                            initialVelocityArray[1] = initVel2.getNumber();
                            initialVelocityArray[2] = initVel3.getNumber();


                            //tworzenie obiektu json i populowanie go danymi
                            JSONObject requestData = new JSONObject();
                            requestData.put("initial_position", initialPositionsArray);
                            requestData.put("initial_velocity", initialVelocityArray);
                            requestData.put("time_steps", timeStepsInt);
                            requestData.put("start_time", startTimeString);
                            requestData.put("time_delta_minutes", timeDeltaMinutesDouble);


                            //tworzenie polaczenie i wyslanie jsona na serwer
                            URL url = new URL("http://127.0.0.1:5000/predict_orbit");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setDoOutput(true);

                            OutputStream os = connection.getOutputStream();
                            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
                            os.flush();
                            os.close();

                            //tworzenie response i populowanie go
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            JSONObject jsonResponse = new JSONObject(response.toString());
                            JSONArray orbitPositions = jsonResponse.getJSONArray("orbit_positions");

                            // liczba obiektow odebranych z response
                            int DATA_COUNT_FROM_RESPONSE = 7;

                            //lista wierszy
                            List<String> rowContents2 = new ArrayList<>();

                            //tworzenie wierszy i dodawanie na ekran odpowiednimi danymi
                            for (int i = 0; i < DATA_COUNT_FROM_RESPONSE; i++) {
                                JSONArray innerArray = orbitPositions.getJSONArray(i);
                                for (int j = 0; j < innerArray.length(); j++) {
                                    double value = innerArray.getDouble(j);

                                    String test = String.valueOf(value);
                                    String replacedText = test.replace(",", ".");
                                    String trimmedNumber = "";
                                    int dotIndex = replacedText.indexOf('.');
                                        if (dotIndex != -1) {
                                            int decimalPlaces = replacedText.length() - dotIndex - 1;
                                            if(decimalPlaces < 3) {
                                                trimmedNumber = replacedText;
                                            } else {
                                                trimmedNumber = replacedText.substring(0, dotIndex + 3);
                                            }
                                        }
                                    rowContents2.add(trimmedNumber);
                                }
                            }


                            UILabel tabelLabel = new UILabel("Orbit predictions", 80, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);

                            List<String> newLabes2 = new ArrayList<>();
                            newLabes2.add("X1");
                            newLabes2.add("X2");
                            newLabes2.add("X3");

                            UITabela tabelaFinal2 = new UITabela(70, 7, 30, 10, 0, uiManager, newLabes2, rowContents2);

                            tabelaFinal2.drawAllHeaders(tab5);
                            tabelaFinal2.drawAllRows(tab5);

                            tab5.addComponent(tabelaFinal2);
                            tab5.addComponent(tabelLabel);



                        } catch (Exception e) {
                            // Handle other types of exceptions
                            // Log the exception or perform error handling as needed
                            e.printStackTrace(); // Print the stack trace for debugging purposes
                        }
                    },
                    uiManager
            );


            tab1.addComponent(border);
            tab1.addComponent(title);
            tab1.addComponent(infoLabel);
            tab1.addComponent(infoLabel2);
            tab1.addComponent(apiButton);
            tab1.addComponent(comboBox);

            tab2.addComponent(infoLabel);
            tab2.addComponent(infoLabel2);
            tab2.addComponent(border);
            tab2.addComponent(label);
            tab2.addComponent(planetInput);
            tab2.addComponent(distanceButton);


            tab3.addComponent(infoLabel);
            tab3.addComponent(infoLabel2);
            tab3.addComponent(border);
            tab3.addComponent(label);
            tab3.addComponent(planetInput);
            tab3.addComponent(weightLabel);
            tab3.addComponent(weightInput);
            tab3.addComponent(weightButton);


            //tabela z algo4
            tab5.addComponent(border);
            tab5.addComponent(initPos1Label);
            tab5.addComponent(initPos1);
            tab5.addComponent(initPos2Label);
            tab5.addComponent(initPos2);
            tab5.addComponent(initPost3Label);
            tab5.addComponent(initPos3);
            tab5.addComponent(initVel1Label);
            tab5.addComponent(initVel1);
            tab5.addComponent(initVel2Label);
            tab5.addComponent(initVel2);
            tab5.addComponent(initVel3Label);
            tab5.addComponent(initVel3);
            tab5.addComponent(timeStepsLabel);
            tab5.addComponent(timeSteps);
            tab5.addComponent(startTimeLabel);
            tab5.addComponent(startTime);
            tab5.addComponent(timeDeltaMinutesLabel);
            tab5.addComponent(timeDeltaMinutes);
            tab5.addComponent(algo4Button);

            //Proba listy
            List<String> listComponents = new ArrayList<>();
            listComponents.add("Komponent 1");
            listComponents.add("Komponent 2");
            listComponents.add("Komponent 3");

            UIList listInTabElement = new UIList(5, 5, 0, uiManager, listComponents);
            listInTabElement.setListElementsMargin(3);
//            listInTabElement.drawList(tab4);

            uiManager.addTab(tab1);
            uiManager.addTab(tab2);

            uiManager.addTab(tab3);
//            uiManager.addTab(tab4);
            uiManager.addTab(tab5);

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

    /**
     * Ustawia exitCallback.
     * @param exitCallback Obiekt ExitCallback.
     */
    @Override
    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;

    }

    /**
     * Ustawia strumień błędu.
     * @param outputStream Strumień błędu.
     */
    @Override
    public void setErrorStream(OutputStream outputStream) {
        this.errout = outputStream;

    }

    /**
     * Ustawia strumień wejściowy - z niego odczytywane są klawisze, które są naciskane.
     * @param inputStream Strumień wejściowy.
     */
    @Override
    public void setInputStream(InputStream inputStream) {
        this.in = inputStream;

    }

    /**
     * Ustawia strumień wyjściowy - to na niego wysyłane będę dane do wyświetlenia na ekranie.
     * @param outputStream Strumień wyjściowy.
     */
    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;

        uiManager = new UIManager(tuiScreen, out);  // Inicjalizacja tutaj

    }

    /**
     * Uruchamia nową sesję użytkownika.
     * @param channelSession Obiekt ChannelSession - sesja kanału.
     * @param environment Środowisko - pozwala na odczytanie rozmiaru okna zdalnego i innych parametrów.
     * @throws IOException W przypadku niepowodzenia.
     */
    @Override
    public void start(ChannelSession channelSession, Environment environment) throws IOException {
        this.session = channelSession;
        this.environment = environment;

        Map<String, String> env = environment.getEnv();
        this.environment.addSignalListener(new SignalListener() {
            @Override
            public void signal(Channel channel, Signal signal) {
                try {
                    messages.put(new byte[]{(byte) 255, (byte) 255, (byte) 0, (byte) 255, (byte) 255});
                } catch (Exception e){
                    logger.error(e.getMessage());
                }
            }
        }, Signal.WINCH);

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

    /**
     * Przerywa sesję użytkownika.
     * @param channelSession Obiekt ChannelSession - nieużywany.
     * @throws Exception W przypadku błędu w przerwaniu działania wątków.
     */
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
                    if(keyInfo.getLabel() == KeyLabel.INTERNAL_WIN_RESIZE) {
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
                    else {
                        logger.info("Odebrano klawisz " + keyInfo);
                        uiManager.handleKeyboardInput(keyInfo);
                    }
                }
                else {
                    logger.warn("Nieznana sekwencja klawiszy " + Arrays.toString(intData));
                }
            }
        } catch (InterruptedException e){
            logger.error(e.getLocalizedMessage());
        }
    }
}

