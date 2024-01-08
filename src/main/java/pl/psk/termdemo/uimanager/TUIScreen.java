package pl.psk.termdemo.uimanager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasa reprezentująca ekran aplikacji.
 */
public class TUIScreen {

    /**
     * Obiekt loggera.
     */
    private static final Logger logger = LoggerFactory.getLogger(TUIScreen.class);

    /**
     * Szerokość ekranu.
     */
    private int width;
    /**
     * Wysokość ekranu.
     */
    private int height;

    /**
     * Warstwy.
     */
    private final Map<Integer, ScreenCell[][]> layers = new HashMap<>();

    /**
     * Złączone warstwy do prezentacji.
     */
    private ScreenCell[][] mergedLayer;

    /**
     * Konstruktor ekranu.
     * @param width Szerokość ekranu.
     * @param height Wysokość ekranu.
     */
    public TUIScreen(int width, int height) {
        this.width = width;
        this.height = height;
        this.mergedLayer = new ScreenCell[height][width];
        clearScreen();
    }


    /**
     * Łączy wszystkie warstwy ekranu do wyjściowej <i>mergedLayer</i>.
     */
    private void mergeLayers() {
        clearScreen();
        logger.trace("Merging layers");
        var zIndexes = new ArrayList<>(layers.keySet());
        Collections.sort(zIndexes);
        for (Integer zIndex : zIndexes) {
            ScreenCell[][] layer = layers.get(zIndex);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (layer[i][j] != null) {
                        mergedLayer[i][j] = layer[i][j];
                    }
                }
            }
        }
    }

    /**
     * Ensures that the layer with the specified z-index exists.
     * @param zIndex The z-index of the layer.
     */
    private void ensureLayerExists(int zIndex) {
//        logger.trace("Ensuring layer with z-index {} exists", zIndex);
        if (!layers.containsKey(zIndex)) {
            layers.put(zIndex, new ScreenCell[height][width]);
        }
    }

    /**
     * Czyści komórkę ekranu na wybranej pozycji.
     * @param x Pozycja x komórki.
     * @param y Pozycja y komórki.
     * @param zIndex Parametr z-index komórki.
     */
    public void clearCellAt(int x, int y, int zIndex) {
        if(x >= width || y >= height || x < 0 || y < 0){
            logger.warn("\033[33mInvalid position set for pixel {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }

        ensureLayerExists(zIndex);
        layers.get(zIndex)[y][x] = null;
        // mergeLayers();
    }

    /**
     * Dodaje nową warstwę do ekranu.
     * @param zIndex z-index warstwy.
     */
    public void addLayer(int zIndex) {
//        logger.trace("Adding layer with z-index {}", zIndex);
        ensureLayerExists(zIndex);
    }


    /**
     * Dodaje komórkę do warstwy o określonym z-index.
     * @param x Pozycja x komórki.
     * @param y Pozycja y komórki.
     * @param zIndex z-index docelowej warstwy.
     * @param cell Komórka do dodania.
     */
    public void addPixelToLayer(int x, int y, int zIndex, ScreenCell cell) {
//        logger.trace("Adding cell at ({}, {}) with z-index {}", x, y, zIndex);
        if(x >= width || y >= height || x < 0 || y < 0){
            logger.warn("\033[33mInvalid position set for pixel {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }
        ensureLayerExists(zIndex);
        layers.get(zIndex)[y][x] = cell;
        // mergeLayers();
    }

    /**
     * Czyści ekran.
     */
    public void clearScreen() {
        logger.trace("Clearing screen");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                mergedLayer[i][j] = new ScreenCell(' ', ANSIColors.TEXT_WHITE.getCode(), ANSIColors.BG_WHITE.getCode());
            }
        }
    }

    /**
     * Ustawia tekst w wybranym miejscu i z-index.
     * @param x Pozycja x tekstu.
     * @param y Pozycja y tekstu.
     * @param text Tekst do wyświetlenia.
     * @param textColor Kolor tekstu.
     * @param bgColor kolor tła.
     * @param zIndex z-index docelowej warstwy.
     */
    public void setText(int x, int y, String text, String textColor, String bgColor, int zIndex) {
        logger.trace("Setting text at ({}, {}) with z-index {}", x, y, zIndex);
        if(x >= width || y  >= height || x < 0 || y < 0){
            logger.warn("\033[33mInvalid position set for text {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }
        ensureLayerExists(zIndex);
        ScreenCell[][] targetLayer = layers.get(zIndex);
        for (int i = 0; i < text.length() && x + i < width; i++) {
            //logger.trace("Setting character {} at ({}, {})", text.charAt(i), x + i, y);
            targetLayer[y][x + i] = new ScreenCell(text.charAt(i), textColor, bgColor);
        }
        //mergeLayers();
    }

    /**
     * Ustawia tło dla danej warstwy.
     * @param bgColor Kod koloru.
     * @param zIndex z-index docelowej warstwy.
     */
    public void setBgColor(String bgColor, int zIndex) {
        logger.trace("Setting background color with z-index {}", zIndex);
        ensureLayerExists(zIndex);
        ScreenCell[][] targetLayer = layers.get(zIndex);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (targetLayer[i][j] == null) {
                    // logger.trace("Setting background color at ({}, {})", j, i);
                    targetLayer[i][j] = new ScreenCell(' ', ANSIColors.TEXT_BLACK.getCode(), bgColor);
                } else {
                    // logger.trace("Setting background color at ({}, {})", j, i);
                    targetLayer[i][j].setBgColor(bgColor);
                }
            }
        }
        //mergeLayers();
    }

    /**
     * Renderuje ekran
     * @return Wyrenderowany ekran.
     */
    private String render() {
        logger.trace("Rendering screen");
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        sb.append("\033[H");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(mergedLayer[i][j].getBgColor());
                sb.append(mergedLayer[i][j].getTextColor());
                sb.append(mergedLayer[i][j].getCharacter());
            }
            sb.append("\033[E");
        }
        sb.append("\033[0m");
        long stop = System.nanoTime();
        logger.info("Render finished in {} ms", (stop - start) / 1000000.0);
        return sb.toString();
    }

    /**
     * Odświeża ekran.
     * @param out Strumień, na który należy wysłać ekran po wyrenderowaniu.
     * @throws IOException Jeśli nastąpi błąd I/O.
     */
    public void refresh(OutputStream out) throws IOException {
        mergeLayers();
        String rendered = render();
        out.write(rendered.getBytes());
        out.flush();
    }

    /**
     * Zwraca komórkę z wybranej pozycji.
     * @param x Pozycja x piksela.
     * @param y pozycja y piksela.
     * @param zIndex z-index warstwy, z której należy pobrać komórkę.
     * @return Piksel na wybranej pozycji.
     */
    public ScreenCell getPixelFromLayer(int x, int y, int zIndex) {
        if (layers.containsKey(zIndex)) {
            return layers.get(zIndex)[y][x];
        }
        return null;
    }

    /**
     * Zmienia wymiary ekranu.
     * @param width Nowa szerokość ekranu.
     * @param height Nowa wysokość ekranu.
     */
    public void resize(int width, int height){
        this.width = width;
        this.height = height;
        mergedLayer = new ScreenCell[height][width];
        List<Integer> keys = layers.keySet().stream().toList();
        layers.clear();
        for(Integer key : keys )
            layers.put(key, new ScreenCell[height][width]);

        clearScreen();
    }


}