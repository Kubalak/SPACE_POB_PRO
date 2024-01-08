package pl.psk.termdemo.uimanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;
import pl.psk.termdemo.model.keys.KeyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa implementująca karty w postaci TUI.
 */
public class UITab implements UIComponent {

    Logger logger = LoggerFactory.getLogger(UITab.class);
    private String title;
    private String textColor = ANSIColors.TEXT_WHITE.getCode();
    private String bgColor = ANSIColors.BG_BRIGHT_BLUE.getCode();

    private String tabColor = "\033[33m";
    private List<UIComponent> components = new ArrayList<>();
    private int x, y;
    private int w, h, zIndex, currentActiveComponent = -1;
    private final UIManager uiManager;
    boolean active;

    /**
     * Domyślny konstruktor.
     * @param title Tytuł karty, który będzie wyświetlany na liście kart.
     * @param x Pozycja x karty (tytułu).
     * @param y Pozycja y karty (tytułu)
     * @param windowWidth Szerokość okna.
     * @param windowHeight Wysokość okna.
     * @param zIndex z-index.
     * @param uiManager Obiekt UIManager, z którym współpracować będzie karta.
     */
    public UITab(String title, int x, int y, int windowWidth, int windowHeight, int zIndex, UIManager uiManager) {
        this.x = x;
        this.y = y;
        this.w = windowWidth;
        this.h = windowHeight;
        this.zIndex = zIndex;
        this.uiManager = uiManager;
        this.title = title;
        this.active = false;
    }

    @Override
    public void draw(UIManager uiManager) {
        if (active) {
            for (int i = 0; i < w; ++i)
                for (int j = y + 1; j < h; ++j) {
                    ScreenCell emptyCell = new ScreenCell(' ', textColor, bgColor);
                    uiManager.getScreen().addPixelToLayer(i, j, zIndex, emptyCell);
                }
        }
        uiManager.getScreen().addPixelToLayer(x, y, zIndex, new ScreenCell('┌', textColor, tabColor));
        uiManager.getScreen().addPixelToLayer(x + 1, y, zIndex, new ScreenCell('─', textColor, tabColor));

        for (int i = 0; i < title.length(); ++i) {
            uiManager.getScreen().addPixelToLayer(x + 2 + i, y, zIndex, new ScreenCell(title.charAt(i), textColor, tabColor));
        }
        uiManager.getScreen().addPixelToLayer(x + title.length() + 2, y, zIndex, new ScreenCell('─', textColor, tabColor));
        uiManager.getScreen().addPixelToLayer(x + title.length() + 3, y, zIndex, new ScreenCell('┐', textColor, tabColor));
        if (active) {
            for (UIComponent component : components)
                component.draw(uiManager);
        }
    }

    /**
     * Pozwala na dodanie komponentu do karty.
     * @param component Komponent. który ma zostać dodany.
     */
    public void addComponent(UIComponent component) {
        logger.debug("Adding UI component " + component.getClass().getSimpleName());
        components.add(component);
    }

    /**
     * Pozwala na usunięcie komponentu z karty.
     * @param component Komponent, który ma zostać usunięty.
     */
    public void removeComponent(UIComponent component) {
        logger.debug("Removing UI component " + component.getClass().getSimpleName());
        components.remove(component);
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h);
    }

    @Override
    public void performAction() {

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
        return h;
    }

    @Override
    public int getHeight() {
        return w;
    }

    @Override
    public void setActive(boolean active) {

        this.active = active;
        if (active) highlight();
        else resetHighlight();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void highlight() {
        textColor = ANSIColors.TEXT_RED.getCode();
        active = true;

        uiManager.refresh();

    }
    @Override
    public void resetHighlight() {
        textColor = ANSIColors.TEXT_WHITE.getCode();
        active = false;

        uiManager.refresh();
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    /**
     * Obsługuje klawiaturę.
     * @param key Informacja o wciśniętym klawiszu.
     */
    public void handleKeyboard(KeyInfo key) {
        switch (key.getLabel()) {
            case ARROW_DOWN:
                if (active && currentActiveComponent != -1 && components.get(currentActiveComponent) instanceof UIComboBox comboBox) {
                    if (comboBox.isExpanded()) {
                        comboBox.navigateOption(key.getLabel());
                        break;
                    }
                }
                moveToNextActiveComponent();
                break;
            case ARROW_UP:
                if (active && currentActiveComponent != -1 && components.get(currentActiveComponent) instanceof UIComboBox comboBox) {
                    if (comboBox.isExpanded()) {
                        comboBox.navigateOption(key.getLabel());
                        break;
                    }
                }
                moveToPrevActiveComponent();
                break;
            case ENTER, ENTER_ALT:
                if (active && currentActiveComponent != -1 && components.get(currentActiveComponent) instanceof UIComboBox comboBox) {
                    if (comboBox.isExpanded())
                        comboBox.confirmSelection();
                    else
                        comboBox.toggleExpand();
                    break;
                } else if (currentActiveComponent != -1)
                    components.get(currentActiveComponent).performAction();
                break;
            default:
                if (active && currentActiveComponent != -1) {
                    if (components.get(currentActiveComponent) instanceof UITextField activeField)
                        activeField.appendText(key);
                    if (components.get(currentActiveComponent) instanceof UIComboBox comboBox)
                        comboBox.handleKeyboardInput(key);
                }

                break;
        }
        highlightActiveComponent();
    }

    private void highlightActiveComponent() {
        logger.trace("Highlighting active component.");
        for (UIComponent component : components) {
            if (component.isActive()) {
                component.highlight();
            } else {
                component.resetHighlight();
            }
        }
    }

    private void moveToNextActiveComponent() {
        if (components.isEmpty()) {
            logger.trace("No components to activate.");
            return;
        }

        if (currentActiveComponent != -1) {
            components.get(currentActiveComponent).setActive(false);
            logger.trace("Deactivating current active component.");
        }
        int startComponent = currentActiveComponent == -1 ? (components.size() - 1) : currentActiveComponent;
        logger.trace("Starting from: " + startComponent);
        do {
            currentActiveComponent = (currentActiveComponent + 1) % components.size();
        } while (!components.get(currentActiveComponent).isInteractable() && currentActiveComponent != startComponent);

        components.get(currentActiveComponent).setActive(true);
    }

    private void moveToPrevActiveComponent() {
        if (components.isEmpty()) {
            logger.trace("No components to activate.");
            return;
        }

        if (currentActiveComponent != -1) {
            components.get(currentActiveComponent).setActive(false);
            logger.trace("Deactivating current active component.");
        }
        int startComponent = currentActiveComponent == -1 ? 0 : currentActiveComponent;
        do {
            currentActiveComponent = (currentActiveComponent - 1);
            if (currentActiveComponent < 0)
                currentActiveComponent = components.size() - 1;
        } while (!components.get(currentActiveComponent).isInteractable() && currentActiveComponent != startComponent);

        components.get(currentActiveComponent).setActive(true);
    }

    @Override
    public void windowResized(int width, int height){
        this.w = width;
        this.h = height;
        for(UIComponent component : components)
            component.windowResized(width, height);
    }
}
