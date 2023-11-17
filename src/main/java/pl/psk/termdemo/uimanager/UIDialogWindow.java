package pl.psk.termdemo.uimanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;

import java.util.ArrayList;
import java.util.List;

public class UIDialogWindow implements UIComponent {

   Logger logger = LoggerFactory.getLogger(UIDialogWindow.class);

    private static final char HORIZONTAL_BORDER = '─';
    private static final char VERTICAL_BORDER = '│';
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char TOP_RIGHT_CORNER = '┐';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char BOTTOM_RIGHT_CORNER = '┘';

    private static final String BUTTON_BG_COLOR = ANSIColors.BG_RED.getCode();
    private static final String BUTTON_TEXT_COLOR = ANSIColors.TEXT_WHITE.getCode();
    private static final int MIN_BUTTON_WIDTH = 10;
    private static final int TEXT_MARGIN = 4;

    private String message = "";
    private int x, y, width, height;
    private String title;
    private List<UIButton> buttons;
    private String bgColor = ANSIColors.BG_WHITE.getCode();
    private String textColor = ANSIColors.TEXT_BLACK.getCode();
    private int zIndex;
    private TUIScreen screen;

    public UIDialogWindow(int x, int y, int width, int height, int zIndex, String title, UIManager uiManager) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.buttons = new ArrayList<>();
        this.screen = uiManager.getScreen();
        this.zIndex = zIndex;
    }

    public void show() {
        drawPartial(screen, height);
    }

    @Override
    public void hide() {
        hide(screen);
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

    public void hide(TUIScreen screen) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                screen.clearCellAt(x + j, y + i, zIndex);
            }
        }
        // Usuwanie cienia (dodane)
        for (int i = 1; i <= height; i++) {
            screen.clearCellAt(x + width, y + i, zIndex);
        }
        for (int j = 0; j <= width; j++) {
            screen.clearCellAt(x + j, y + height, zIndex);
        }
        // Koniec usuwania cienia
        for (UIButton button : buttons) {
            button.hide();
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addButton(UIButton button) {
        buttons.add(button);
    }

    public void setBgColor(String color) {
        this.bgColor = color;
    }

    public void setTextColor(String color) {
        this.textColor = color;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void draw(UIManager uiManager) {
        drawPartial(uiManager.getScreen(), height);
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    @Override
    public void performAction() {
        handleMouseClickAction();
    }

    private void handleMouseClickAction() {
        int buttonX = x + 2;
        for (UIButton button : buttons) {
            if (button.isInside(buttonX, y + height - 2)) {
                button.click();
                buttons.clear();
                break;
            }
            buttonX += button.text.length() + 2;
        }
    }

    public void setButtonTextColor(UIButton buttonType, String color) {
        if (buttons.contains(buttonType)) {
            buttonType.setTextColor(color);
        }
    }

    public void setButtonBgColor(UIButton buttonType, String color) {
        if (buttons.contains(buttonType)) {
            buttonType.setBgColor(color);
        }
    }

    public void setButtonTextAlign(UIButton buttonType, UIButton.TextAlign textAlign) {
        if (buttons.contains(buttonType)) {
            buttonType.setTextAlign(textAlign);
        }
    }

    private void drawPartial(TUIScreen screen, int lines) {
        String firstLine = message;
        String secondLine = "";

        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < width; j++) {
                char borderChar = ' ';  // domyślnie puste miejsce
                if (i == 0 && j == 0) {
                    borderChar = TOP_LEFT_CORNER;
                } else if (i == 0 && j == width - 1) {
                    borderChar = TOP_RIGHT_CORNER;
                } else if (i == height - 1 && j == 0) {
                    borderChar = BOTTOM_LEFT_CORNER;
                } else if (i == height - 1 && j == width - 1) {
                    borderChar = BOTTOM_RIGHT_CORNER;
                } else if (i == 0 || i == height - 1) {
                    borderChar = HORIZONTAL_BORDER;
                } else if (j == 0 || j == width - 1) {
                    borderChar = VERTICAL_BORDER;
                }
                screen.setText(x + j, y + i, String.valueOf(borderChar), textColor, bgColor, zIndex);
            }
        }

        if (message.length() > width - TEXT_MARGIN) {
            int breakIndex = message.substring(0, width - TEXT_MARGIN).lastIndexOf(' ');
            if (breakIndex == -1) {
                breakIndex = width - TEXT_MARGIN;
            }
            firstLine = message.substring(0, breakIndex).trim();
            secondLine = message.substring(breakIndex).trim();
        }

        int firstLineX = x + (width - firstLine.length()) / 2;
        screen.setText(firstLineX, y + (height / 2) - 1, firstLine, textColor, bgColor, zIndex);

        if (!secondLine.isEmpty()) {
            int secondLineX = x + (width - secondLine.length()) / 2;
            if (secondLineX >= 0 && secondLineX + secondLine.length() < width) {
                screen.setText(secondLineX, y + (height / 2), secondLine, textColor, bgColor, zIndex);
            } else {
                    logger.warn("Second line of the message is too long to fit in the dialog window");
            }
        }

        // Shadow for the right and bottom edges
        for (int i = 1; i <= height; i++) {
            screen.setText(x + width, y + i, " ", ANSIColors.BG_BLACK.getCode(), bgColor, zIndex);
        }
        for (int j = 0; j <= width; j++) {
            screen.setText(x + j, y + height, " ", ANSIColors.BG_BLACK.getCode(), bgColor, zIndex);
        }

        int titleX = x + (width - title.length()) / 2;
        screen.setText(titleX, y, title, textColor, bgColor, zIndex);

        int buttonX = x + 2;
        for (UIButton button : buttons) {
            String buttonText = String.format("%-" + MIN_BUTTON_WIDTH + "s", button.text);
            int padding = (MIN_BUTTON_WIDTH - button.text.length()) / 2;
            String paddedButtonText = String.format("%" + (padding + button.text.length()) + "s", button.text);
            paddedButtonText = String.format("%-" + MIN_BUTTON_WIDTH + "s", paddedButtonText);

            button.setPositionAndSize(buttonX, y + height - 2, paddedButtonText.length(), 1);
            screen.setText(buttonX, y + height - 2, paddedButtonText, BUTTON_TEXT_COLOR, BUTTON_BG_COLOR, zIndex);
            button.show();
            buttonX += paddedButtonText.length() + 2;
        }
    }

}