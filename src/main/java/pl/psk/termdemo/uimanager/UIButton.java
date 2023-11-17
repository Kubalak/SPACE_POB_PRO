package pl.psk.termdemo.uimanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;

public class UIButton implements UIComponent {

    Logger logger = LoggerFactory.getLogger(UIButton.class);

    protected String text;
    private final Runnable action;
    private int x, y, width, height;
    private String bgColor = ANSIColors.BG_RED.getCode();
    private String textColor = ANSIColors.TEXT_WHITE.getCode();
    private TextAlign textAlign = TextAlign.CENTER;  // domyślne wyrównanie tekstu do środka
    private int zIndex = 0;

    private UIManager uiManager;

    private boolean active;

    public enum TextAlign {
        LEFT, CENTER, RIGHT
    }

    public UIButton(int x, int y, int width, int height, int zIndex, String text, Runnable action, UIManager uiManager) {
        this.text = text;
        this.action = action;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.uiManager = uiManager;
    }




    public UIButton(int zIndex, String text, Runnable action, UIManager uiManager) {
        this.text = text;
        this.action = action;
        this.uiManager = uiManager;
        this.zIndex = zIndex;
        this.uiManager = uiManager;
    }

    public void setPositionAndSize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(UIManager uiManager) {
        logger.debug("Drawing button");
        String paddedText;
        switch (textAlign) {
            case LEFT:
                paddedText = String.format("%-" + width + "s", text);
                break;
            case RIGHT:
                paddedText = String.format("%" + width + "s", text);
                break;
            case CENTER:
            default:
                int padding = (width - text.length()) / 2;
                paddedText = String.format("%" + (text.length() + padding) + "s", text);
                break;
        }
        for (int i = 0; i < paddedText.length(); i++) {
            ScreenCell cell = new ScreenCell(paddedText.charAt(i), textColor, bgColor);
            uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, cell);
        }


    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    @Override
    public void performAction() {

    }

    @Override
    public void show() {
        if (uiManager != null) {
            uiManager.registerUIComponent(this);
            uiManager.addComponentToScreen(this);
        }
    }

    @Override
    public void hide() {
        if (uiManager != null) {
            uiManager.unregisterUIComponent(this);
            uiManager.removeComponent(this);
        }
    }

    public void click() {

        action.run();


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
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void highlight() {
        logger.debug("Highlighting button");
            setBgColor(ANSIColors.BG_YELLOW.getCode());
            setTextColor(ANSIColors.TEXT_BLACK.getCode());

            uiManager.render();
            uiManager.refresh();
    }

    @Override
    public void resetHighlight() {
        setBgColor(ANSIColors.BG_RED.getCode());
        setTextColor(ANSIColors.TEXT_WHITE.getCode());

        uiManager.render();
        uiManager.refresh();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }


    @Override
    public int getZIndex() {
        return zIndex;
    }

    public void setTextAlign(TextAlign textAlign) {
        this.textAlign = textAlign;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
