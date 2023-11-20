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

    private final List<UIComponent> uiComponents = new ArrayList<>();

    private final List<UITab> tabs = new ArrayList<>();

    private final OutputStream out;

    private int currentActiveComponent = -1;  // -1 oznacza brak aktywnego komponentu

    private int currentTab = 0;

    public UIManager(TUIScreen screen, OutputStream out) {
        logger.trace("Constructor UIManager with screen and out");
        this.screen = screen;
        this.out = out;
    }

    public void registerUIComponent(UIComponent component) {
        logger.trace("Registering UI component: " + component.getClass().getSimpleName());
        uiComponents.add(component);

    }
    public void addComponentToScreen(UIComponent component) {
        logger.trace("Adding UI component to screen: " + component.getClass().getSimpleName());
        layers.computeIfAbsent(component.getZIndex(), k -> new ArrayList<>()).add(component);
    }
    public void unregisterUIComponent(UIComponent component) {
        logger.trace("Unregistering UI component: " + component.getClass().getSimpleName());
        uiComponents.remove(component);
        removeComponent(component);
    }

    public void addTab(UITab tab){
        tab.show();
        tab.setActive(tabs.isEmpty());
        tabs.add(tab);
    }
    public void removeTab(UITab tab){
        tab.hide();
        tabs.remove(tab);
    }

    public void handleMouseClick(int x, int y) {
        logger.trace("Handling mouse click at: " + x + ", " + y);
        for (UIComponent component : uiComponents) {
            if (component.isInside(x, y)) {
                component.performAction();
                break;
            }
        }
    }

    public void handleKeyboardInputForTextField(KeyInfo keyInfo) {
        logger.trace("Handling keyboard input for text field: " + keyInfo);
        if (currentActiveComponent == -1) {
            logger.trace("No active component, skipping.");
            return;
        }

        UIComponent activeComponent = uiComponents.get(currentActiveComponent);
        if (activeComponent instanceof UITextField activeTextField) {
          logger.trace("Active component is a text field, appending text.");
            activeTextField.appendText(keyInfo);
        }
    }

    public void render() {
        logger.trace("Rendering UI components.");
        screen.clearScreen();
        for (List<UIComponent> layer : layers.values()) {
            for (UIComponent component : layer) {
                component.draw(this);
            }
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

        // Check if active component is a keyboard input handler for custom logic (e.g. combobox)
        UIComponent activeComponent = currentActiveComponent == -1 ? null : uiComponents.get(currentActiveComponent);
        if (activeComponent instanceof KeyboardInputHandler) {
           logger.trace("Active component is a keyboard input handler, delegating input.");
            ((KeyboardInputHandler) activeComponent).handleKeyboardInput(keyInfo);
            return;
        }

        // Standards logic for navigation and activation
        switch (keyInfo.getLabel()) {
            case CTRL_ARROW_RIGHT:
                moveToNextTab();
                break;
            case CTRL_ARROW_LEFT:
                moveToPrevTab();
                break;
            case ENTER:
                if (currentActiveComponent != -1)
                    uiComponents.get(currentActiveComponent).performAction();
                break;
            default:
                if(!tabs.isEmpty())
                    tabs.get(currentTab).handleKeyboard(keyInfo);
                break;
        }
    }

//    private void navigateUsingArrows(KeyLabel direction) {
//        logger.trace("Navigating using arrows: " + direction);
//        if (currentActiveComponent == -1) {
//            return;
//        }
//
//        UIComponent currentComponent = uiComponents.get(currentActiveComponent);
//        int currentX = currentComponent.getX() + currentComponent.getWidth() / 2;
//        int currentY = currentComponent.getY() + currentComponent.getHeight() / 2;
//
//        UIComponent closestComponent = null;
//        int minDistance = Integer.MAX_VALUE;
//
//        for (UIComponent component : uiComponents) {
//            if (!component.isInteractable() || component == currentComponent) {
//                continue;
//            }
//
//            int componentX = component.getX() + component.getWidth() / 2;
//            int componentY = component.getY() + component.getHeight() / 2;
//
//            int distanceX = componentX - currentX;
//            int distanceY = componentY - currentY;
//
//            boolean isDirectionMatch = switch (direction) {
//                case ARROW_UP -> distanceY < 0;
//                case ARROW_DOWN -> distanceY > 0;
//                case ARROW_LEFT -> distanceX < 0;
//                case ARROW_RIGHT -> distanceX > 0;
//                default -> false;
//            };
//
//            if (isDirectionMatch) {
//                int distance = distanceX * distanceX + distanceY * distanceY;
//                if (distance < minDistance) {
//                    minDistance = distance;
//                    closestComponent = component;
//                }
//            }
//        }
//
//        if (closestComponent != null) {
//            uiComponents.get(currentActiveComponent).setActive(false);
//            closestComponent.setActive(true);
//            currentActiveComponent = uiComponents.indexOf(closestComponent);
//        }
//    }

//    private void highlightActiveComponent() {
//        logger.trace("Highlighting active component.");
//        for (UIComponent component : uiComponents) {
//            if (component.isActive()) {
//                component.highlight();
//            } else {
//                component.resetHighlight();
//            }
//        }
//    }
//
//    private void moveToNextActiveComponent() {
//        if (uiComponents.isEmpty()) {
//           logger.trace("No components to activate.");
//            return;
//        }
//
//        if (currentActiveComponent != -1) {
//            uiComponents.get(currentActiveComponent).setActive(false);
//           logger.trace("Deactivating current active component.");
//        }
//
//        do {
//            currentActiveComponent = (currentActiveComponent + 1) % uiComponents.size();
//        } while (!uiComponents.get(currentActiveComponent).isInteractable());
//
//        uiComponents.get(currentActiveComponent).setActive(true);
//    }
//
//    private void moveToPrevActiveComponent(){
//        if (uiComponents.isEmpty()) {
//            logger.trace("No components to activate.");
//            return;
//        }
//
//        if (currentActiveComponent != -1) {
//            uiComponents.get(currentActiveComponent).setActive(false);
//            logger.trace("Deactivating current active component.");
//        }
//        do {
//            currentActiveComponent = (currentActiveComponent - 1);
//            if(currentActiveComponent < 0)
//                currentActiveComponent = uiComponents.size() - 1;
//        } while (!uiComponents.get(currentActiveComponent).isInteractable());
//        uiComponents.get(currentActiveComponent).setActive(true);
//    }

    private void moveToNextTab() {
        if(!tabs.isEmpty()) {
            tabs.get(currentTab).setActive(false);
            currentTab = (currentTab + 1) % tabs.size();
            tabs.get(currentTab).setActive(true);
        }
    }

    private void moveToPrevTab(){
        if(!tabs.isEmpty()) {
            tabs.get(currentTab).setActive(false);
            currentTab -= 1;
            if (currentTab < 0) currentTab = tabs.size() - 1;
            tabs.get(currentTab).setActive(true);
        }
    }


    @SneakyThrows
    public void refresh() {
      logger.trace("Refreshing screen.");
        if(out != null) {
            screen.refresh(this.out);
        } else {
            logger.warn("OutputStream is null, skipping refresh.");
        }
    }

}
