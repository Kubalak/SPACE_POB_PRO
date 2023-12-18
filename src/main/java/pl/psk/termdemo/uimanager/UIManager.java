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

//TODO: Przerobić klasę by obsługiwała tylko karty
public class UIManager {

    private final Logger logger = LoggerFactory.getLogger(UIManager.class);

    private final TreeMap<Integer, List<UIComponent>> layers = new TreeMap<>();

    @Getter
    private TUIScreen screen;


    private final List<UITab> tabs = new ArrayList<>();

    private final OutputStream out;

    private int currentActiveComponent = -1;  // -1 oznacza brak aktywnego komponentu

    private int currentTab = 0;

    private boolean shouldRefresh;

    public UIManager(TUIScreen screen, OutputStream out) {
        logger.trace("Constructor UIManager with screen and out");
        this.screen = screen;
        this.out = out;
        this.shouldRefresh = true;
    }

    public void initialize(){
        this.shouldRefresh = true;
        render();
    }

    public void addComponentToScreen(UIComponent component) {
        logger.trace("Adding UI component to screen: " + component.getClass().getSimpleName());
        layers.computeIfAbsent(component.getZIndex(), k -> new ArrayList<>()).add(component);
    }

    public void addTab(UITab tab) {
        tab.show();
        if(tabs.isEmpty())
            tab.setActive(true);
        tabs.add(tab);
    }

    public void removeTab(UITab tab) {
        tab.hide();
        tabs.remove(tab);
    }

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
        logger.trace("Setting current active component to: " + index);
        this.currentActiveComponent = index;
    }

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

    private void moveToNextTab() {
        if (!tabs.isEmpty()) {
            tabs.get(currentTab).setActive(false);
            currentTab = (currentTab + 1) % tabs.size();
            tabs.get(currentTab).setActive(true);
        }

    }

    private void moveToPrevTab() {
        if (!tabs.isEmpty()) {
            tabs.get(currentTab).setActive(false);
            currentTab -= 1;
            if (currentTab < 0) currentTab = tabs.size() - 1;
            tabs.get(currentTab).setActive(true);
        }
    }

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
    @SneakyThrows
    public void refresh() {
        this.shouldRefresh = true;
    }

}
