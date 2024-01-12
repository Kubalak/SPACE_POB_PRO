package pl.psk.termdemo.uimanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;

import java.util.List;

public class UIList implements UIComponent {
    /**
     * Obiekt loggera
     */
    Logger logger = LoggerFactory.getLogger(UIList.class);

    /**
     * Obiekt textContent, używany w loggerze
     */
    private StringBuilder textContent;

    /**
     * Zmienne int, odpowiadające za:
     * x - współrzędna X, od której rysowana jest lista.
     * y - spółrzędna X, od której rysowana jest lista.
     */
    private int x, y, width, height;

    /**
     * Zmienna string odpowiadająca za kolor tła.
     */
    private String bgColor = ANSIColors.BG_BRIGHT_BLUE.getCode();

    /**
     * Zmienna string odpowiadająca za kolor tekstu.
     */
    private String textColor = ANSIColors.TEXT_BRIGHT_GREEN.getCode();

    /**
     * Zmienna zIndex
     */
    private int zIndex;

    /**
     * Zmienna bool, która wskazuje, czy obiekt jest aktywny.
     */
    private boolean isActive;

    /**
     * Inicjalizacja UIManagera
     */
    private UIManager uiManager;

    /**
     * Zmienna ograniczająca długość wiadomości
     */
    private int maxCharacters = Integer.MAX_VALUE;

    /**
     * Lista przechowująca zawartość listy w formacie string
     */
    private List<String> listContents;

    /**
     * Zmienna ustalająca odstęp pomiędzy wierszami
     */
    private int listMargin = 1;

    /**
     * Zmienna przechowująca znak początkowy elementów listy.
     */
    private static final String LIST_DOT = ">";


    /**
     * Inicjalizacja obiektu listy
     * @param x współrzędna X, od której rysowana jest lista.
     * @param y spółrzędna X, od której rysowana jest lista.
     * @param zIndex zmienna zIndex
     * @param uiManager klasa UIManager, w której ma znajdować się lista
     * @param listContents lista z zawartością listy
     */

    public UIList (int x, int y, int zIndex, UIManager uiManager, List<String> listContents) {
        this.x = x;
        this.y = y;
        this.zIndex = zIndex;
        this.uiManager = uiManager;
        this.textContent = new StringBuilder();
        this.listContents = listContents;
    }

    /**
     * Funkcja dodająca nowy element do istniejącej listy.
     * @param data Nowe dane, dodawane do listy <i>listContents</i>
     */
    public void addListElement(String data) { this.listContents.add(data); }

    /**
     * Funkcja pomocnicza odświeżająca UIManager
     */
    public void refresh() {
        if (uiManager != null) {
            uiManager.refresh();
        }
    }

    /**
     * Funkcja rysująca obiekt listy.
     * @param uiTab Zmienna wskazująca na zakładkę, w której ma być rysowana lista.
     */
    public void drawList(UITab uiTab) {
        int Y = this.y + 1;
        int X = this.x;
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

    /**
     * Funkcja ustawiająca inny kolor tła listy.
     * @param bgColor zmienna ustawiająca kolor tła - domyślnie <i>BG_BRIGHT_BLUE</i>
     */
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }

    /**
     * Funkcja zmieniająca kolor tekstu
     * @param textColor zmienna ustawiająca kolor tekstu - domyślnie <i>TEXT_WHITE</i>
     */
    public void setTextColor(String textColor) { this.textColor = textColor; }

    /**
     * Funkcja ustawiająca odstęp pomiędzy wierszami.
     * @param margin odstęp pomiędzy wierszami w linijkach - domyślnie 1.
     */
    public void setListElementsMargin(int margin) { this.listMargin = margin; }

    /**
     * Funkcja pomocnicza
     * @param uiManager Obiekt UIManager, który ma odpowiadać za rysowanie komponentu.
     */
    @Override
    public void draw(UIManager uiManager) {}

    /**
     * Funkcja zwracająca wartość zIndex kompomentu
     * @return wartość zIndex
     */
    @Override
    public int getZIndex() {
        return zIndex;
    }

    /**
     * Funkcja pomocnicza.
     * @param mouseX Współrzędna x.
     * @param mouseY Współrzędna y.
     * @return zmienna bool, wskazująca czy kursor myszy jest na elemencie.
     */
    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }

    /**
     * Funkcja loggera
     */
    @Override
    public void performAction() {
        logger.debug("Performing action");
        logger.debug("Text content: {}", textContent.toString());
    }

    /**
     * Funkcja odkrywająca element
     */
    @Override
    public void show() {
        if (uiManager != null) {
            uiManager.addComponentToScreen(this);
        }
    }

    /**
     * Funkcja ukrywająca element
     */
    @Override
    public void hide() {
        if (uiManager != null) {
            uiManager.removeComponent(this);
        }
    }

    /**
     * Funkcja zwracająca współrzędną X
     * @return współrzędna X
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Funkcja zwracająca współrzędną Y
     * @return współrzędna Y
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Funkcja pomocnicza.
     * @return zmienna width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Funkcja pomocnicza.
     * @return zmienna height
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Funkcja aktywująca komponent
     * @param active Stan komponentu.
     */
    @Override
    public void setActive(boolean active) { this.isActive = true; }

    /**
     * Funkcja zwracająca status komponentu
     * @return Status komponentu
     */
    @Override
    public boolean isActive() { return this.isActive; }

    /**
     * Funkcja podkreślająca element listy
     */
    @Override
    public void highlight() {
        setBgColor(ANSIColors.BG_YELLOW.getCode());
        setTextColor(ANSIColors.TEXT_BLACK.getCode());

        uiManager.refresh();
    }

    /**
     * Funkcja resetująca podświetlenie
     */
    @Override
    public void resetHighlight() {
        setBgColor(ANSIColors.BG_RED.getCode());
        setTextColor(ANSIColors.TEXT_WHITE.getCode());

        uiManager.refresh();
    }

    /**
     * Funkcja pomocnicza.
     */
    @Override
    public boolean isInteractable() { return true; }

    /**
     * Funkcja pomocnicza.
     * @param width Nowa szerokość okna.
     * @param height Nowa wysokość okna.
     */
    @Override
    public void windowResized(int width, int height) {}
}
