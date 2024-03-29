package pl.psk.termdemo.uimanager;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;
import pl.psk.termdemo.model.keys.KeyInfo;
import pl.psk.termdemo.model.keys.KeyLabel;

import java.util.List;


public class UIComboBox implements UIComponent, KeyboardInputHandler  {
    Logger logger = LoggerFactory.getLogger(UIComboBox.class);

    private List<String> items;
    private int selectedIndex;
    private int x, y, width, height;
    private String bgColor = ANSIColors.BG_BLUE.getCode();
    private String textColor = ANSIColors.TEXT_WHITE.getCode();
    private int zIndex;
    private boolean isActive;
    private UIManager uiManager;
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
            zIndex += 1;
            highlightIndex = selectedIndex;
        } else {
            zIndex -= 1;
        }
        uiManager.render();
        uiManager.refresh();
    }


    public void selectHighlighted() {
        if (isExpanded) {
            selectedIndex = highlightIndex;
            toggleExpand();
        }
        uiManager.render();
        uiManager.refresh();
    }


    @Override
    public void draw(UIManager uiManager) {
        if (!isActive) {
            return;
        }

        String displayText = items.get(selectedIndex);

        for (int i = 0; i < width; i++) {
            ScreenCell emptyCell = new ScreenCell(' ', textColor, bgColor);
            uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, emptyCell);
        }

        for (int i = 0; i < displayText.length(); i++) {
            ScreenCell cell = new ScreenCell(displayText.charAt(i), textColor, bgColor);
            uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, cell);
        }

        ScreenCell arrowCell = new ScreenCell('v', textColor, bgColor);
        uiManager.getScreen().addPixelToLayer(x + width - 1, y, zIndex, arrowCell);

        if (isExpanded) {
            for (int i = 0; i < items.size(); i++) {
                String item = items.get(i);
                for (int j = 0; j < width; j++) {
                    char c = (j < item.length()) ? item.charAt(j) : ' ';
                    String currentBgColor = (i == selectedOption) ? ANSIColors.BG_YELLOW.getCode() : ANSIColors.BG_BLUE.getCode();
                    ScreenCell cell = new ScreenCell(c, ANSIColors.TEXT_WHITE.getCode(), currentBgColor);
                    uiManager.getScreen().addPixelToLayer(x + j, y + i + 1, zIndex + 1, cell);
                }
            }
        } else {
            for (int i = 1; i <= items.size(); i++) {
                for (int j = 0; j < width; j++) {
                    ScreenCell emptyCell = new ScreenCell(' ', textColor, bgColor);
                    uiManager.getScreen().addPixelToLayer(x + j, y + i, zIndex + 1, emptyCell);
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
        uiManager.render();
        uiManager.refresh();
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
    }

    @Override
    public void resetHighlight() {
        this.bgColor = ANSIColors.BG_BLUE.getCode();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    public void confirmSelection() {
        logger.info("Confirming selection");
        logger.debug("Selected index: {}", selectedOption);
        isExpanded = false;
        selectedIndex = selectedOption;
        isActive = false;
        resetHighlight();
        uiManager.setCurrentActiveComponent(-1);
        uiManager.render();
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
        uiManager.render();
        uiManager.refresh();
    }

    @Override
    public void handleKeyboardInput(KeyInfo keyInfo) {
        if (isExpanded) {
            switch (keyInfo.getLabel()) {
                case ARROW_UP, ARROW_DOWN -> navigateOption(keyInfo.getLabel());
                case ENTER -> {
                    confirmSelection();
                    uiManager.render(); // Odświeżamy tu, aby od razu ukryć listę
                    uiManager.refresh();
                    uiManager.setCurrentActiveComponent(-1); // Uwalniamy focus z tego elementu
                }
                case ESC -> {
                    isExpanded = false;  // Anulujemy i zamykamy combobox
                    uiManager.render(); // Odświeżamy tu, aby od razu ukryć listę
                    uiManager.refresh();
                    uiManager.setCurrentActiveComponent(-1);  // Uwalniamy focus z tego elementu
                }
            }
        } else {
            if (keyInfo.getLabel() == KeyLabel.ENTER) {
                performAction(); // Otwarcie lub zamknięcie comboboxa
                uiManager.render(); // Odświeżamy tu, aby od razu pokazać listę
                uiManager.refresh();
            }
        }
        uiManager.render();
        uiManager.refresh();
    }

}
