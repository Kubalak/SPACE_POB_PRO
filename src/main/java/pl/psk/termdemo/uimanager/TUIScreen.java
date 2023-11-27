package pl.psk.termdemo.uimanager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import java.util.HashMap;
import java.util.Map;

public class TUIScreen {

    private static final Logger logger = LoggerFactory.getLogger(TUIScreen.class);

    /**
     * The width and height of the screen.
     */
    private final int width;
    private final int height;

    /**
     * The layers of the screen.
     */
    private final Map<Integer, ScreenCell[][]> layers = new HashMap<>();

    /**
     * The merged layer of the screen.
     */
    private final ScreenCell[][] mergedLayer;

    /**
     * Creates a new screen with the specified width and height.
     * @param width The width of the screen.
     * @param height The height of the screen.
     */
    public TUIScreen(int width, int height) {
        this.width = width;
        this.height = height;
        this.mergedLayer = new ScreenCell[height][width];
        clearScreen();
    }


    /**
     * Merges all the layers into the merged layer.
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
     * Clears the cell at the specified position.
     * @param x The x position of the cell.
     * @param y The y position of the cell.
     * @param zIndex The z-index of the cell.
     */
    public void clearCellAt(int x, int y, int zIndex) {
        if(x >= width || y >= height){
            logger.warn("\033[33mInvalid position set for pixel {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }

        ensureLayerExists(zIndex);
        layers.get(zIndex)[y][x] = null;
        // mergeLayers();
    }

    /**
     * Adds a new layer to the screen.
     * @param zIndex The z-index of the layer.
     */
    public void addLayer(int zIndex) {
//        logger.trace("Adding layer with z-index {}", zIndex);
        ensureLayerExists(zIndex);
    }

    /**
     * Removes the layer with the specified z-index.
     * @param zIndex The z-index of the layer.
     */
//    public void removeLayer(int zIndex) {
//        logger.trace("Removing layer with z-index {}", zIndex);
//        layers.remove(zIndex);
//        mergeLayers();
//    }

    /**
     * Adds a pixel to the layer with the specified z-index.
     * @param x The x position of the pixel.
     * @param y The y position of the pixel.
     * @param zIndex The z-index of the layer.
     * @param cell The cell to add.
     */
    public void addPixelToLayer(int x, int y, int zIndex, ScreenCell cell) {
//        logger.trace("Adding cell at ({}, {}) with z-index {}", x, y, zIndex);
        if(x >= width || y >= height){
            logger.warn("\033[33mInvalid position set for pixel {} {} (max: {}, {})\033[0m", x, y, width - 1, height - 1);
            return;
        }
        ensureLayerExists(zIndex);
        layers.get(zIndex)[y][x] = cell;
        // mergeLayers();
    }

    /**
     * Updates a pixel in the layer with the specified z-index.
     * @param x The x position of the pixel.
     * @param y The y position of the pixel.
     * @param zIndex The z-index of the layer.
     * @param cell The cell to add.
     */
//    public void updatePixelInLayer(int x, int y, int zIndex, ScreenCell cell) {
//        logger.trace("Updating cell at ({}, {}) with z-index {}", x, y, zIndex);
//        if (layers.containsKey(zIndex)) {
//            layers.get(zIndex)[y][x] = cell;
//            mergeLayers();
//        }
//    }

    /**
     * Clears the screen.
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
     * Sets the text at the specified position.
     * @param x The x position of the text.
     * @param y The y position of the text.
     * @param text The text to set.
     * @param textColor The text color.
     * @param bgColor The background color.
     * @param zIndex The z-index of the layer.
     */
    public void setText(int x, int y, String text, String textColor, String bgColor, int zIndex) {
        logger.trace("Setting text at ({}, {}) with z-index {}", x, y, zIndex);
        if(x >= width || y  >= height){
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
     * Sets the text at the specified position.
     * @param bgColor The color code.
     * @param zIndex The z-index of the layer.
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
     * Renders the screen.
     * @return The rendered screen.
     */
    public String render() {
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
     * Refreshes the screen.
     * @param out The output stream.
     * @throws IOException If an I/O error occurs.
     */
    public void refresh(OutputStream out) throws IOException {
        mergeLayers();
        String rendered = render();
        out.write(rendered.getBytes());
        out.flush();
    }

    /**
     * Gets screen cell of the screen.
     * @param x The x position of the pixel.
     * @param y The y position of the pixel.
     * @param zIndex The z-index of the layer.
     * @return The pixel at the specified position.
     */
    public ScreenCell getPixelFromLayer(int x, int y, int zIndex) {
        if (layers.containsKey(zIndex)) {
            return layers.get(zIndex)[y][x];
        }
        return null;
    }


}