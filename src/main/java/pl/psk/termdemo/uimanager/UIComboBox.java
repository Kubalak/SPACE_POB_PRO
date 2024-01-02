package pl.psk.termdemo.uimanager;


import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;
import pl.psk.termdemo.model.keys.KeyInfo;
import pl.psk.termdemo.model.keys.KeyLabel;

import java.util.List;


public class UIComboBox implements UIComponent, KeyboardInputHandler {
    Logger logger = LoggerFactory.getLogger(UIComboBox.class);

    private List<String> items;
    private int selectedIndex;
    private int x, y, width, height;
    private String bgColor = ANSIColors.BG_BLUE.getCode();
    private String textColor = ANSIColors.TEXT_WHITE.getCode();
    private int zIndex;
    private boolean isActive;
    private UIManager uiManager;
    @Getter
    private boolean isExpanded;
    private int highlightIndex;
    private int defaultIndex;
    private int selectedOption = 0;


    public UIComboBox(int x, int y, int width, int height, int zIndex, UIManager uiManager, List<String> items) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.uiManager = uiManager;
        this.items = items;
        this.isExpanded = false;
    }
    public void toggleExpand() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            highlightIndex = selectedIndex;
        }
        uiManager.refresh();
    }


    public void selectHighlighted() {
        if (isExpanded) {
            selectedIndex = highlightIndex;
            toggleExpand();
        }
        uiManager.refresh();
    }


    @Override
    public void draw(UIManager uiManager) {

        String displayText = items.get(selectedIndex);

        for (int i = 0; i < width; i++) {
            ScreenCell emptyCell = new ScreenCell(' ', textColor, bgColor);
            uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, emptyCell);
        }

        for (int i = 0; i < displayText.length(); i++) {
            ScreenCell cell = new ScreenCell(displayText.charAt(i), textColor, bgColor);
            uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, cell);
        }

        if (isExpanded) {
            for (int i = 0; i < items.size(); i++) {
                String item = items.get(i);
                for (int j = 0; j < width; j++) {
                    char c = (j < item.length()) ? item.charAt(j) : ' ';
                    String currentBgColor = (i == selectedOption) ? ANSIColors.BG_YELLOW.getCode() : ANSIColors.BG_BLUE.getCode();
                    ScreenCell cell = new ScreenCell(c, ANSIColors.TEXT_WHITE.getCode(), currentBgColor);
                    uiManager.getScreen().addPixelToLayer(x + j, y + i + 1, zIndex, cell);
                }
            }
        }
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    @Override
    public void performAction() {
        if (isExpanded) {
            selectHighlighted();
        } else {
            toggleExpand();
        }
        uiManager.refresh();
    }

    @Override
    public void show() {
        if (uiManager != null) {
            uiManager.addComponentToScreen(this);
        }
    }

    @Override
    public void hide() {
        if (uiManager != null) {
            uiManager.removeComponent(this);
        }
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
        return isExpanded ? height + items.size() : height;
    }

    public String getSelectedValue() {
        return items.get(selectedIndex);
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public void highlight() {
        this.bgColor = ANSIColors.BG_YELLOW.getCode();
        uiManager.refresh();
    }

    @Override
    public void resetHighlight() {
        this.bgColor = ANSIColors.BG_BLUE.getCode();
        uiManager.refresh();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    public void confirmSelection() {
        logger.info("Confirming selection");
        logger.info("Selected index: {}", items.get(selectedOption));
        isExpanded = false;
        selectedIndex = selectedOption;
        isActive = false;
        resetHighlight();
        uiManager.setCurrentActiveComponent(-1);
        uiManager.refresh();
    }


    public void navigateOption(KeyLabel direction) {
        if (isExpanded) {
            if (direction == KeyLabel.ARROW_UP) {
                selectedOption = Math.max(0, selectedOption - 1);
            } else if (direction == KeyLabel.ARROW_DOWN) {
                selectedOption = Math.min(items.size() - 1, selectedOption + 1);
            }
        }
        uiManager.refresh();
    }

    @Override
    public void handleKeyboardInput(KeyInfo keyInfo) {
        if (isExpanded) {
            switch (keyInfo.getLabel()) {
                case ARROW_UP, ARROW_DOWN -> navigateOption(keyInfo.getLabel());
                case ENTER, ENTER_ALT -> {
                    confirmSelection();
                    uiManager.refresh();
                    uiManager.setCurrentActiveComponent(-1); // Uwalniamy focus z tego elementu
                }
                case ESC -> {
                    isExpanded = false;  // Anulujemy i zamykamy combobox
                    uiManager.refresh();
                    uiManager.setCurrentActiveComponent(-1);  // Uwalniamy focus z tego elementu
                }
            }
        } else {
            if (keyInfo.getLabel() == KeyLabel.ENTER || keyInfo.getLabel() == KeyLabel.ENTER_ALT) {
                performAction(); // Otwarcie lub zamknięcie comboboxa
                uiManager.refresh();
            }
        }
        uiManager.refresh();
    }

    @Override
    public void windowResized(int width, int height){

    }

}
