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
            //Dodanie warstw
            tuiScreen.addLayer(0);
            tuiScreen.addLayer(1);
            tuiScreen.addLayer(2);
            tuiScreen.addLayer(3);

            //Inicjalizacja podstawowego koloru tła
            tuiScreen.setBgColor(ANSIColors.BG_BRIGHT_BLACK.getCode(), 0);

            //Definiowanie zakładek
            UITab tab1 = new UITab("Algo 1", 0, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UITab tab2 = new UITab("Algo 2", 10, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UITab tab3 = new UITab("Algo 3", 20, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UITab tab4 = new UITab("Algo 4", 30, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UITab tab5 = new UITab("Algo 5", 40, 0, ScreenWidth, ScreenHeight, 0, uiManager);
            UITab tab6 = new UITab("Summary", 50, 0, ScreenWidth, ScreenHeight, 0, uiManager);

            //Definiowanie
            UIBorder border = new UIBorder(1, 1, ScreenWidth - 1, ScreenHeight - 1, 0, uiManager);
            border.setBgColor(ANSIColors.BG_BRIGHT_BLUE.getCode());
            border.setTextColor(ANSIColors.TEXT_WHITE.getCode());

            //Definiowanie labeli
            final String name = "Term emu v1.0";
            UILabel titleLabel = new UILabel(name, (ScreenWidth - name.length()) / 2, ScreenHeight - 1, 1, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel infoLabel1 = new UILabel("Press arrow down to activate next field, and input planet name.", 3, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel infoLabel2 = new UILabel("Press arrow down again to activate next component - numeric input", 3, 4, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel infoLabel3 = new UILabel("Press enter to dropdown combo box", 2, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel tabelLabel = new UILabel("Orbit predictions", 80, 2, 0,ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel velocitiesLabel = new UILabel("", 20, 4, 0, ANSIColors.TEXT_BRIGHT_GREEN.getCode(), ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel distancesLabel = new UILabel("", 3, 7, 0, ANSIColors.TEXT_BRIGHT_GREEN.getCode(), ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);

            UILabel initPos1Label = new UILabel("Init position 1", 3, 2, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initPos2Label = new UILabel("Init position 2", 3, 5, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initPost3Label = new UILabel("Init position 3", 3, 8, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initVel1Label = new UILabel("Init velocity 1", 3, 11, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initVel2Label = new UILabel("Init velocity 2", 3, 14, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel initVel3Label = new UILabel("Init velocity 3", 3, 17, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel timeStepsLabel = new UILabel("Timesteps", 3, 20, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel startTimeLabel = new UILabel("Start time in expected format: 2023-01-01T00:00:00", 3, 23, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel timeDeltaMinutesLabel = new UILabel("Time delta minutes", 3, 26, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);

            UILabel helpLabel1 = new UILabel("Use CTRL + ARROW_RIGHT to move to next tab.", 2, 28, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel helpLabel2 = new UILabel("Use CTRL + ARROW_LEFT to move to previous tab.", 2, 27, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);

            UILabel dateLabel1 = new UILabel("Input date in format YYYY-MM-DD", 3, 3, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel dateLabel2 = new UILabel("Input date in format YYYY-MM-DD", 3, 11, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel latLabel = new UILabel("Input latitude example: 50.874167,", 3, 14, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel lonLabel = new UILabel("Input latitude example: 20.633333,", 3, 17, 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel lunarPhaseLabel = new UILabel("", 3, 8, 0, ANSIColors.TEXT_BRIGHT_GREEN.getCode(), ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
            UILabel dayNightLabel = new UILabel("", 3, 22, 0, ANSIColors.TEXT_BRIGHT_GREEN.getCode(), ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);

            //tworzenie inputow do danych
            UITextField planetInput1 = new UITextField(3, 3, 15, 1, 0, uiManager);
            UITextField planetInput2 = new UITextField(3, 3, 15, 1, 0, uiManager);
            UITextField weightInput = new UITextField(3, 5, 15, 1, 0, uiManager);
            UITextField initPos1 = new UITextField(3, 3, 15, 1, 0, uiManager);
            UITextField initPos2 = new UITextField(3, 6, 15, 1, 0, uiManager);
            UITextField initPos3 = new UITextField(3, 9, 15, 1, 0, uiManager);
            UITextField initVel1 = new UITextField(3, 12, 15, 1, 0, uiManager);
            UITextField initVel2 = new UITextField(3, 15, 15, 1, 0, uiManager);
            UITextField initVel3 = new UITextField(3, 18, 15, 1, 0, uiManager);
            UITextField timeSteps = new UITextField(3, 21, 15, 1, 0, uiManager);
            UITextField startTime = new UITextField(3, 24, 15, 1, 0, uiManager);
            UITextField timeDeltaMinutes = new UITextField(3, 27, 15, 1, 0, uiManager);
            UITextField dateInput1 = new UITextField(3, 4, 15, 1, 0, uiManager);
            UITextField dateInput2 = new UITextField(3, 12, 15, 1, 0, uiManager);
            UITextField latInput = new UITextField(3, 15, 15, 1, 0, uiManager);
            UITextField lonInput = new UITextField(3, 18, 15, 1, 0, uiManager);
            weightInput.setNumeric(true);
            initPos1.setNumeric(true);
            initPos2.setNumeric(true);
            initPos3.setNumeric(true);
            initVel1.setNumeric(true);
            initVel2.setNumeric(true);
            initVel3.setNumeric(true);
            timeSteps.setNumeric(true);
            timeDeltaMinutes.setNumeric(true);
            latInput.setNumeric(true);
            lonInput.setNumeric(true);

            //Definicja comboboxa
            UIComboBox comboBox= new UIComboBox(2, 4, 15, 5, 0, uiManager, List.of("Ziemia", "Mars", "Jowisz", "Wenus", "Merkury", "Saturn", "Uran", "Neptun", "Pluton"));

            UIDialogWindow dialog = new UIDialogWindow(25, 10, 40, 10, 0, "Attention", uiManager);

            //Labele do tabeli
            List<String> newLabels2 = new ArrayList<>();
            newLabels2.add("X1");
            newLabels2.add("X2");
            newLabels2.add("X3");

            //Algo 1 Button
            UIButton distanceButton = new UIButton(
                    3, 5, 15, 5, 0, "Calculate Distance",
                    () -> {
                        try {
                            String planetName = planetInput1.getText(); // Example: You can change this to the desired planet name

                            JSONObject requestData = new JSONObject();
                            requestData.put("planet_name", planetName);

                            URL url = new URL("http://127.0.0.1:5000/distance/" + planetName.toLowerCase());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setDoOutput(true);

                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            JSONObject jsonResponse = new JSONObject(response.toString());
                            double distanceKm = jsonResponse.getDouble("distance_km");
                            double distanceAu = jsonResponse.getDouble("distance_au");

                            distancesLabel.setText("distance_km: " + distanceKm + ", distance_au: " + distanceAu);
                            tab1.addComponent(distancesLabel);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    uiManager
            );

            //Algo 2 Button
            UIButton lunarPhaseButton = new UIButton(3,6, 15,5, 0, "Calc Lunar Phase",
                    () -> {
                        try {
                            String date = dateInput1.getText();

                            JSONObject requestData = new JSONObject();
                            requestData.put("date", date);

                            URL url = new URL("http://127.0.0.1:5000/lunar_phase");
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

                            JSONObject jsonResponse = new JSONObject(response.toString());
                            String phaseText = jsonResponse.getString("phase");
                            lunarPhaseLabel.setText("The moon phase for this value is:" + phaseText);
                            tab2.addComponent(lunarPhaseLabel);

                        } catch (IOException e) {
                            // Handle exceptions
                            e.printStackTrace();
                        }
                    },
                    uiManager
            );

            //Algo 2 Button
            UIButton dayNightButton = new UIButton(3,20, 15,5, 0, "Calc day duration",
                    () -> {
                        try {
                            String date = dateInput2.getText();
                            double lat = latInput.getNumber();
                            double lon = lonInput.getNumber();

                            JSONObject requestData = new JSONObject();
                            requestData.put("date", date);
                            requestData.put("lat", lat);
                            requestData.put("lon", lon);

                            URL url = new URL("http://127.0.0.1:5000/day_length");
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

                            JSONObject jsonResponse = new JSONObject(response.toString());
                            JSONObject dayDurationObject = jsonResponse.getJSONObject("day_duration");
                            int hour = dayDurationObject.getInt("hour");
                            int minute = dayDurationObject.getInt("minute");
                            String phaseText = "Day durration is: " + hour + " hours " + minute + " minutes";
                            dayNightLabel.setText(phaseText);
                            tab2.addComponent(dayNightLabel);

                        } catch (IOException e) {
                            // Handle exceptions
                            e.printStackTrace();
                        }
                    },
                    uiManager
            );

            //Algo 3 Button
            UIButton weightButton = new UIButton(
                    3, 7, 15, 5, 0, "Calculate Weight",
                    () -> {
                        try {
                            String planet = planetInput2.getText();
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


                            UITabela tabelaFinal2 = new UITabela(70, 7, 30, 10, 0, uiManager, newLabels2, rowContents2);

                            tabelaFinal2.drawAllHeaders(tab4);
                            tabelaFinal2.drawAllRows(tab4);

                            tab4.addComponent(tabelaFinal2);
                            tab4.addComponent(tabelLabel);

                        } catch (Exception e) {
                            // Handle other types of exceptions
                            // Log the exception or perform error handling as needed
                            e.printStackTrace(); // Print the stack trace for debugging purposes
                        }
                    },
                    uiManager
            );

            // ALGO 5 Button
            UIButton spaceVelocityButton = new UIButton(
                    2, 6, 15, 5, 0, "Calculate space Velocities",
                    () -> {
                        try {
                            String fieldValue = comboBox.getSelectedValue();
                            JSONObject requestData = new JSONObject();
                            requestData.put("body_name", fieldValue);

                            URL url = new URL("http://127.0.0.1:5000/space_velocities");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setDoOutput(true);

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
                            velocitiesLabel.setText("First Velocity: " + firstVelocity + ", Second Velocity: " + secondVelocity);
                            tab5.addComponent(velocitiesLabel);

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

            //Summary Button
            UIButton getSummaryButton = new UIButton(3,2, 12,5, 0, "Get Summary",
                    () -> {
                            //Proba listy
                            List<String> listComponents = new ArrayList<>();
                            listComponents.add(distancesLabel.getText());
                            listComponents.add(lunarPhaseLabel.getText());
                            listComponents.add(dayNightLabel.getText());
                            listComponents.add(velocitiesLabel.getText());

                            UIList listInTabElement = new UIList(3, 5, 0, uiManager, listComponents);
                            listInTabElement.setListElementsMargin(2);
                            listInTabElement.drawList(tab6);
                    },
                    uiManager
            );


            tab1.addComponent(border);
            tab1.addComponent(titleLabel);
            tab1.addComponent(infoLabel1);
            tab1.addComponent(planetInput1);
            tab1.addComponent(distanceButton);
            tab1.addComponent(helpLabel1);
            tab1.addComponent(helpLabel2);

            tab2.addComponent(border);
            tab2.addComponent(titleLabel);
            tab2.addComponent(dateLabel1);
            tab2.addComponent(dateInput1);
            tab2.addComponent(lunarPhaseButton);
            tab2.addComponent(dateLabel2);
            tab2.addComponent(dateInput2);
            tab2.addComponent(latLabel);
            tab2.addComponent(latInput);
            tab2.addComponent(lonLabel);
            tab2.addComponent(lonInput);
            tab2.addComponent(dayNightButton);
            tab2.addComponent(helpLabel1);
            tab2.addComponent(helpLabel2);
//
            tab3.addComponent(border);
            tab3.addComponent(titleLabel);
            tab3.addComponent(infoLabel1);
            tab3.addComponent(planetInput2);
            tab3.addComponent(infoLabel2);
            tab3.addComponent(weightInput);
            tab3.addComponent(weightButton);
            tab3.addComponent(helpLabel1);
            tab3.addComponent(helpLabel2);

            //tabela z algo4
            tab4.addComponent(border);
            tab4.addComponent(initPos1Label);
            tab4.addComponent(initPos1);
            tab4.addComponent(initPos2Label);
            tab4.addComponent(initPos2);
            tab4.addComponent(initPost3Label);
            tab4.addComponent(initPos3);
            tab4.addComponent(initVel1Label);
            tab4.addComponent(initVel1);
            tab4.addComponent(initVel2Label);
            tab4.addComponent(initVel2);
            tab4.addComponent(initVel3Label);
            tab4.addComponent(initVel3);
            tab4.addComponent(timeStepsLabel);
            tab4.addComponent(timeSteps);
            tab4.addComponent(startTimeLabel);
            tab4.addComponent(startTime);
            tab4.addComponent(timeDeltaMinutesLabel);
            tab4.addComponent(timeDeltaMinutes);
            tab4.addComponent(algo4Button);


            tab5.addComponent(border);
            tab5.addComponent(titleLabel);
            tab5.addComponent(infoLabel3);
            tab5.addComponent(spaceVelocityButton);
            tab5.addComponent(comboBox);
            tab5.addComponent(helpLabel1);
            tab5.addComponent(helpLabel2);

            tab6.addComponent(border);
            tab6.addComponent(titleLabel);
            tab6.addComponent(getSummaryButton);

            uiManager.addTab(tab1);
            uiManager.addTab(tab2);
            uiManager.addTab(tab3);
            uiManager.addTab(tab4);
            uiManager.addTab(tab5);
            uiManager.addTab(tab6);
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
//                    } else if (Arrays.stream(intData).anyMatch("3"::equals) == true) {
//                        logger.info("Wykryto CTRL+C czyli koniec sesji");
//
                    } else {
                        logger.info("Odebrano klawisz " + keyInfo);
                        uiManager.handleKeyboardInput(keyInfo);
                        //destroy(session);
                    }
                }
                else {
                    logger.warn("Nieznana sekwencja klawiszy " + Arrays.toString(intData));
                }
            }
        } catch (InterruptedException e){
            logger.error(e.getLocalizedMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

