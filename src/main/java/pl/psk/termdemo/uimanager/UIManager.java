package pl.psk.termdemo.uimanager;

import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.keys.KeyInfo;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Klasa odpowiedzialna za zarządzanie kartami i komponentami.
 * Umożliwia renderowanie ekranu i przekazywanie go przez sieć.
 */
public class UIManager {

    private final Logger logger = LoggerFactory.getLogger(UIManager.class);

    private final TreeMap<Integer, List<UIComponent>> layers = new TreeMap<>();

    /**
     * Ekran <i>TUIScreen</i>, na którym renderowane są komponenty.
     */
    @Getter
    private TUIScreen screen;

    private final List<UITab> tabs = new ArrayList<>();

    private final OutputStream out;

    private int currentTab = 0;

    private boolean shouldRefresh;

    /**
     * Domyślny konstruktor klasy.
     * @param screen Ekran, na którym wyświetlane są komponenty.
     * @param out Strumień wyjściowy, na który wysyłany będzie ekran po wyrenderowaniu.
     */
    public UIManager(TUIScreen screen, OutputStream out) {
        logger.trace("Constructor UIManager with screen and out");
        this.screen = screen;
        this.out = out;
        this.shouldRefresh = true;
    }

    /**
     * Zleca odświeżenie ekranu i wykonuje pierwszy render.
     */
    public void initialize(){
        this.shouldRefresh = true;
        render();
    }

    /**
     * Pozwala na dodanie komponentu do ekranu.
     * @param component Komponent, który ma zostać dodany.
     */
    public void addComponentToScreen(UIComponent component) {
        logger.trace("Adding UI component to screen: " + component.getClass().getSimpleName());
        layers.computeIfAbsent(component.getZIndex(), k -> new ArrayList<>()).add(component);
    }

    /**
     * Dodaje nową kartę.
     * @param tab Karta do dodania.
     */
    public void addTab(UITab tab) {
        tab.show();
        if(tabs.isEmpty())
            tab.setActive(true);
        tabs.add(tab);
    }

    /**
     * Usuwa kartę.
     * @param tab Karta do usunięcia.
     */
    public void removeTab(UITab tab) {
        tab.hide();
        tabs.remove(tab);
    }

    /**
     * Aktualizuje ekran, jeśli zmienna <i>shouldRefresh</i> jest ustawiona na <i style="color:orange;">true</i>.
     */
    @SneakyThrows
    private void render() {
        if(shouldRefresh) {
            logger.trace("Rendering UI components.");
            for (List<UIComponent> layer : layers.values()) {
                for (UIComponent component : layer) {
                    component.draw(this);
                }
            }
            logger.trace("Refreshing screen.");
            if (out != null) {
                screen.refresh(this.out);
            } else {
                logger.warn("OutputStream is null, skipping refresh.");
            }
            shouldRefresh = false;
        }
    }

    /**
     * Usuwa komponent z ekranu.
     * @param component Komponent do usunięcia.
     */
    public void removeComponent(UIComponent component) {
        logger.trace("Removing UI component: " + component.getClass().getSimpleName());
        int zIndex = component.getZIndex();
        if (layers.containsKey(zIndex)) {
            layers.get(zIndex).remove(component);
        }
        // Delete component from screen
        TUIScreen screen = getScreen();
        for (int i = 0; i < component.getWidth(); i++) {
            for (int j = 0; j < component.getHeight(); j++) {
                screen.clearCellAt(component.getX() + i, component.getY() + j, zIndex);
            }
        }
    }


    public void setCurrentActiveComponent(int index) {
        // logger.trace("Setting current active component to: " + index);
        // this.currentActiveComponent = index;
    }

    /**
     * Obsługuje klawiaturę.
     * @param keyInfo Informacja o wciśniętym klawiszu.
     */
    public void handleKeyboardInput(KeyInfo keyInfo) {
        logger.trace("Handling keyboard input: " + keyInfo);


        // Standards logic for navigation and activation
        switch (keyInfo.getLabel()) {
            case CTRL_ARROW_RIGHT, CTRL_ARROW_RIGHT_ALT:
                moveToNextTab();
                break;
            case CTRL_ARROW_LEFT, CTRL_ARROW_LEFT_ALT:
                moveToPrevTab();
                break;
            default:
                if (!tabs.isEmpty())
                    tabs.get(currentTab).handleKeyboard(keyInfo);
                break;
        }

        if(shouldRefresh)
            this.render();
    }

    /**
     * Przełącza na kolejną kartę.
     */
    private void moveToNextTab() {
        if (!tabs.isEmpty()) {
            tabs.get(currentTab).setActive(false);
            currentTab = (currentTab + 1) % tabs.size();
            tabs.get(currentTab).setActive(true);
        }

    }

    /**
     * Przełącza na poprzednią kartę.
     */
    private void moveToPrevTab() {
        if (!tabs.isEmpty()) {
            tabs.get(currentTab).setActive(false);
            currentTab -= 1;
            if (currentTab < 0) currentTab = tabs.size() - 1;
            tabs.get(currentTab).setActive(true);
        }
    }

    /**
     * Obsługuje zmianę wymiarów okna (zdalnego).
     * @param width Nowa szerokość okna.
     * @param height Nowa wysokość okna.
     */
    @SneakyThrows
    public void resizeUI(int width, int height){
        screen.resize(width, height);
        for(UITab tab : tabs)
            tab.windowResized(width, height);
        if(out != null) {
            shouldRefresh = true;
            render();
            logger.info("Screen resized");
        } else
            logger.warn("OutputStream is null, not refreshing.");
    }

    /**
     * Ustawia flagę <i>shouldRefresh</i> na <i style="color:orange;">true</i>.
     */
    @SneakyThrows
    public void refresh() {
        this.shouldRefresh = true;
    }

}
