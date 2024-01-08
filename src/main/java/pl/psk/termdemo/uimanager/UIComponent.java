package pl.psk.termdemo.uimanager;


/**
 * Interfejs dla wszystkich komponentów interfejsu TUI.
 */
public interface UIComponent {
    /**
     * Rysuje komponent.
     * @param uiManager Obiekt UIManager, który ma odpowiadać za rysowanie komponentu.
     */
    void draw(UIManager uiManager);

    /**
     * Zwraca z-index komponentu.
     * @return z-index komponentu.
     */
    int getZIndex();

    /**
     * Sprawdza czy pozycja x, y znajduje się wewnątrz komponentu.
     * @param x Współrzędna x.
     * @param y Współrzędna y.
     * @return true jeśli znajduje się wewnątrz lub false w przeciwnym razie.
     */
    boolean isInside(int x, int y);

    /**
     * Pozwala na wykonanie akcji w przypadku komponentów funkcjonalnych.
     */
    void performAction();

    /**
     * Pokazuje komponent na ekranie.
     */
    void show();

    /**
     * Ukrywa komponent na ekranie.
     */
    void hide();

    /**
     * Zwraca pozycję x komponentu.
     * @return Współrzędna x komponentu.
     */

    int getX();
    /**
     * Zwraca pozycję y komponentu.
     * @return Współrzędna y komponentu.
     */
    int getY();

    /**
     * Zwraca szerokość komponentu.
     * @return Szerokość komponentu.
     */
    int getWidth();

    /**
     * Zwraca wysokość komponentu.
     * @return Wysokość komponentu.
     */
    int getHeight();

    /**
     * Pozwala na aktywację komponentu.
     * @param active Stan komponentu.
     */
    void setActive(boolean active);

    /**
     * Zwraca czy komponent jest aktywny.
     * @return true, jeśli komponent jest aktywny false w przeciwnym wypadku.
     */
    boolean isActive();

    /**
     * Podświetla komponent.
     */
    void highlight();

    /**
     * Resetuje podświetlenie komponentu.
     */
    void resetHighlight();

    /**
     * Informuje, czy komponent jest interaktywny.
     * @return true, jeśli komponent jest interaktywny, false w przeciwnym wypadku.
     */
    boolean isInteractable();

    /**
     * Obsługuje zmianę rozmiaru okna.
     * @param width Nowa szerokość okna.
     * @param height Nowa wysokość okna.
     */
    void windowResized(int width, int height);
}