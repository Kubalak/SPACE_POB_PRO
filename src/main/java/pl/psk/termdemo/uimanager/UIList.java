package pl.psk.termdemo.uimanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;

import java.util.List;

public class UIList implements UIComponent {

    Logger logger = LoggerFactory.getLogger(UIList.class);

    private StringBuilder textContent;
    private int x, y,width, height;
    private String bgColor = ANSIColors.BG_BRIGHT_BLUE.getCode();
    private String textColor = ANSIColors.TEXT_WHITE.getCode();
    private int zIndex;
    private boolean isActive;
    private UIManager uiManager;
    private int maxCharacters = Integer.MAX_VALUE;
    private List<String> listContents;
    private int listMargin = 1;
    private static final String LIST_DOT = ">";

    //x - współrzędna X do rozpoczęcia rysowania listy,
    //y - współrzędna Y jw.
    //width - TODO, max szerokość listy?
    //height - TODO, max wysokosc listy?
    //edit do powyzszego, jako ze tego nie uzywalem, to na razie usunalem (ale klasa UIComponent wymagala)
    //zIndex - nie wiem, ale wszystkie komponenty tego wymagaja :~D
    //UIManager - podpięcie pod głównego menadżera rysowania?
    //listContents - zawartość listy do wyrysowania
    public UIList (int x, int y, /*int width, int height,*/ int zIndex, UIManager uiManager, List<String> listContents) {
        this.x = x;
        this.y = y;
        /*this.width = width;
        this.height = height;*/
        this.zIndex = zIndex;
        this.uiManager = uiManager;
        this.textContent = new StringBuilder();
        this.listContents = listContents;
    }

    //dodanie nowego elementu do listy listContents
    public void addListElement(String data) { this.listContents.add(data); }

    //nieuzywane, ale odswieza menadżera rysowania (funkcja pomocnicza żeby cały czas tego nie pisać)
    public void refresh() {
        if (uiManager != null) {
            uiManager.refresh();
        }
    }

    //wypisywanie elementów listy, parametr uiTab wskazuje na zakładkę w której ma być wyrysowana lista.
    public void drawList(UITab uiTab) {
        int Y = this.y + 1;
        int X = this.x;
        int count = 1;
        int amountOfListElements = this.listContents.size();

        if (amountOfListElements == 0) {
            //do sth
        }
        else {
            for (String element : this.listContents) {
                Y += listMargin;
                element = LIST_DOT + " " + element;
                UILabel newLabel = new UILabel(element, X, Y, this.zIndex, textColor, bgColor, uiManager);
                uiTab.addComponent(newLabel);
            }
        }
    }
    //funkcje do ustawienia innego koloru tła oraz tekstu, jeżeli byłaby taka potrzeba
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }

    //jw.
    public void setTextColor(String textColor) { this.textColor = textColor; }

    //funkcja do zmieniania odstępu pomiędzy wierszami
    public void setListElementsMargin(int margin) { this.listMargin = margin; }

    //funkcje pomocnicze, które były wszędzie, a więc i tutaj je dodałem
    @Override
    public void draw(UIManager uiManager) { }

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
        logger.debug("Performing action");
        logger.debug("Text content: {}", textContent.toString());
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
        return height;
    }

    @Override
    public void setActive(boolean active) { this.isActive = true; }

    @Override
    public boolean isActive() { return this.isActive; }

    @Override
    public void highlight() {
        setBgColor(ANSIColors.BG_YELLOW.getCode());
        setTextColor(ANSIColors.TEXT_BLACK.getCode());

        uiManager.refresh();
    }

    @Override
    public void resetHighlight() {
        setBgColor(ANSIColors.BG_RED.getCode());
        setTextColor(ANSIColors.TEXT_WHITE.getCode());

        uiManager.refresh();
    }

    @Override
    public boolean isInteractable() { return true; }

    @Override
    public void windowResized(int width, int height) {}
}
