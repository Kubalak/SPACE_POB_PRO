package pl.psk.termdemo.uimanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;


public class UIBar implements UIComponent {

    Logger logger = LoggerFactory.getLogger(UIBar.class);

    private int x, y, width;
    private String bgColor;
    private String textColor;
    private String text;
    private TextAlign align;
    private int zIndex;  // Pole do obsługi warstw
    private UIManager screenManager;  // Dodajemy to pole

    public enum TextAlign {
        LEFT, CENTER, RIGHT
    }

    // Konstruktor z wartością zIndex
    public UIBar(int x, int y, int width, int zIndex, String text, TextAlign align, UIManager screenManager) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.text = text;
        this.align = align;
        this.bgColor = ANSIColors.BG_BLUE.getCode();
        this.textColor = ANSIColors.TEXT_WHITE.getCode();
        this.zIndex = zIndex;
        this.screenManager = screenManager;  // Inicjalizujemy to pole
    }
    public void show() {
        screenManager.addComponentToScreen(this);  // Dodajemy komponent
    }

    public void hide() {
        screenManager.removeComponent(this);  // Usuwamy komponent
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
        return 1;
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

    public void destroy() {
        hide();  // Usuwamy komponent i zasoby
    }
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    @Override
    public void draw(UIManager uiManager) {
        logger.info("Drawing UIBar");
        TUIScreen screen = uiManager.getScreen();
        for (int i = 0; i < width; i++) {
            screen.addPixelToLayer(x + i, y, zIndex, new ScreenCell(' ', textColor, bgColor));
        }

        int textStart = x;
        switch (align) {
            case CENTER:
                textStart = x + (width - text.length()) / 2;
                break;
            case RIGHT:
                textStart = x + width - text.length();
                break;
            case LEFT:
            default:
                break;
        }
        for (int i = 0; i < text.length() && textStart + i < width; i++) {
            screen.addPixelToLayer(textStart + i, y, zIndex, new ScreenCell(text.charAt(i), textColor, bgColor));
        }
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return false; // TUIBar is not interactive, so it always returns false
    }

    @Override
    public void performAction() {
        // TUIBar does not have any action, so this method does nothing
    }
}
