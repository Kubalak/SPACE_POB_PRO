package pl.psk.termdemo.uimanager;

import org.slf4j.Logger;
import pl.psk.termdemo.model.color.ANSIColors;

public class UIBorder implements UIComponent {

    Logger logger = org.slf4j.LoggerFactory.getLogger(UIBorder.class);

    private static final char HORIZONTAL_BORDER = '─';
    private static final char VERTICAL_BORDER = '│';
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char TOP_RIGHT_CORNER = '┐';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char BOTTOM_RIGHT_CORNER = '┘';

    private int x, y, width, height;
    private int zIndex;
    private String bgColor = ANSIColors.BG_BLUE.getCode();
    private String textColor = ANSIColors.TEXT_WHITE.getCode();

    private boolean isVisible = false;
    private UIManager uiManager;

    public UIBorder(int x, int y, int width, int height, int zIndex, UIManager uiManager) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.uiManager = uiManager;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    @Override
    public void draw(UIManager uiManager) {
        logger.debug("Drawing border");
        TUIScreen screen = uiManager.getScreen();
        for (int i = x; i < x + width; i++) {
            screen.addPixelToLayer(i, y, zIndex, new ScreenCell(HORIZONTAL_BORDER, textColor, bgColor));
            screen.addPixelToLayer(i, y + height - 1, zIndex, new ScreenCell(HORIZONTAL_BORDER, textColor, bgColor));
        }
        for (int i = y; i < y + height; i++) {
            screen.addPixelToLayer(x, i, zIndex, new ScreenCell(VERTICAL_BORDER, textColor, bgColor));
            screen.addPixelToLayer(x + width - 1, i, zIndex, new ScreenCell(VERTICAL_BORDER, textColor, bgColor));
        }
        screen.addPixelToLayer(x, y, zIndex, new ScreenCell(TOP_LEFT_CORNER, textColor, bgColor));
        screen.addPixelToLayer(x + width - 1, y, zIndex, new ScreenCell(TOP_RIGHT_CORNER, textColor, bgColor));
        screen.addPixelToLayer(x, y + height - 1, zIndex, new ScreenCell(BOTTOM_LEFT_CORNER, textColor, bgColor));
        screen.addPixelToLayer(x + width - 1, y + height - 1, zIndex, new ScreenCell(BOTTOM_RIGHT_CORNER, textColor, bgColor));
    }

    @Override
    public boolean isInside(int x, int y) {
        return false;
    }

    @Override
    public void performAction() {
        return;
    }

    public void show() {
        uiManager.addComponentToScreen(this);  // Dodajemy komponent
    }

    public void hide() {
        uiManager.removeComponent(this);  // Usuwamy komponent
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setActive(boolean active) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void highlight() {

    }

    @Override
    public void resetHighlight() {

    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }
}