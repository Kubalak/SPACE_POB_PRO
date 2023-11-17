package pl.psk.termdemo.uimanager;



public interface UIComponent {
    void draw(UIManager uiManager);
    int getZIndex();
    boolean isInside(int x, int y);
    void performAction();
    void show();
    void hide();

    int getX();
    int getY();
    int getWidth();
    int getHeight();


    // nowe metody
    void setActive(boolean active);
    boolean isActive();
    void highlight();
    void resetHighlight();
    boolean isInteractable();
}