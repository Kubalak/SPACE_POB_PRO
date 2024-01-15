package pl.psk.termdemo.uimanager;


import pl.psk.termdemo.model.color.ANSIColors;


import java.util.List;




/**
 * Klasa reprezentująca tabele skladajaca zawierajaca pole x,y, width, height, uiManager oraz Listy newLabels jako nazwy kolumn oraz rowContents jako zawartosci komorek tabeli
 *
 */
public class UITabela implements UIComponent {




        /**
         * Wspolrzedne x,y oraz szerokosc tabeli i wysokosc jednej komorki tabeli
         */
        private int x, y, width, height;

        /**
         * Inicjalizacja UIManagera
         */
        private UIManager uiManager;

        /**
         * Lista zawierajaca nazwy kolumn
         */
        private List<String> newLabels;

        /**
         * Lista zawierajaca zawartosc komorek tabeli
         */
        private List<String> rowContents;



    /**
     * Konstruktor tworzący tabeli na danym x, y, o okreslonej szerokosci. Height odpowiada za wysokosc jednej komorki z danymi. Ustawia takze zindex, obiekt uiManagera oraz liste nazwy kolumn i wierszy tabeli
     *
     * @param x      Współrzędna x lewego górnego rogu tabeli
     * @param y      Współrzędna y lewego dolnego rogu tabeli
     * @param width  Szerokość tabeli.
     * @param height Wysokosc jednej komorki tabeli
     * @param uiManager obiekt, na którym rysowany jest obiekt tabeli
     * @param newLabels Lista nazwy kolumn tabeli
     * @param rowContents Lista wierszy tabeli
     */
    public UITabela(int x, int y, int width, int height, UIManager uiManager, List<String> newLabels, List<String> rowContents) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.uiManager = uiManager;
        this.newLabels = newLabels;
        this.rowContents = rowContents;
    }

        /**
         * Funkcja rysująca wszystkie nazwy kolumn tabeli
         * @param uiTab Zmienna wskazująca na zakładkę, w której mają byc rysowane wszystkie nazwy kolumn. Rysowane sa na podstawie zadeklarowanych konstruktorem pól x, y, width
         */
        public void drawAllHeaders(UITab uiTab) {
            int xPosition = this.x + 3;
            for (String label : this.newLabels) {
                UILabel labelObject = new UILabel(label, xPosition, this.y -3 , 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
                xPosition += (this.width / this.newLabels.size()) + 3;
                uiTab.addComponent(labelObject);
            }
        }

        /**
         * Funkcja rysujaca wiersze tabeli i ich zawartość
         * @param uiTab Zmienna wskazująca na zakładkę, w której mają byc wiersze tabeli i ich zawartość. Rysowane sa na podstawie zadeklarowanych konstruktorem pól x, y, width. Height odpowiada natomiast za wysokosc jednej komorki tabeli. Przyjęto ze maksymalna ilosc elementow w jednym wierszu wynosi 3
         *              Zawartosc wierszy brana jest z zadeklarowanej w konstruktorze zawartosci listy rowContents. Nastepnie tworzony jest odpowiednio obiekt UIBorder ktory odpowiada za komorke tabeli. Uzupelniany jest jego tekst, kolor, border i dodawany jest w odpowiednich wspolrzednych
         *              do zakladki. Wystepuje rowniez warunek ktory ,,upewnia sie" ze ilosc komorek w rzedzie nie przekroczy 3 i kolejny bedzie narysowany w odpowiednim miejscu
         */
        public void drawAllRows(UITab uiTab) {
            int startingY = this.y - 2;
            int startingX = this.x;
            int count  = 1;
            int elementsPerRow = 3; // Ilość elementów w jednym rzędzie
            int totalElements = this.rowContents.size();
            System.out.println(this.newLabels.size());
            for(String rowContent : this.rowContents) {
                if (totalElements >= elementsPerRow && count > 1 && (count - 1) % elementsPerRow == 0) {
                    startingY = startingY + this.height;
                    startingX = this.x;
                }
                UIBorder border = new UIBorder(startingX, startingY, this.width / 3 + 2, this.height , 0, uiManager, rowContent);
                count++;
                border.setTextInBorder(uiTab);
                border.setTextColor(ANSIColors.TEXT_WHITE.getCode());
                border.setBgColor(ANSIColors.BG_BRIGHT_BLUE.getCode());
                startingX += this.width / 3 + 2;
                uiTab.addComponent(border);
            }
        }

    /**
     * Funkcja pomocnicza
     * @param uiManager Obiekt UIManager, który ma odpowiadać za rysowanie komponentu.
     */
    @Override
    public void draw(UIManager uiManager) {

    }

    /**
     * Funkcja zwracająca wartość zIndex kompomentu
     * @return wartość zIndex
     */
    @Override
    public int getZIndex() {
        return 0;
    }

    /**
     * Funkcja pomocnicza.
     * @param x Współrzędna x.
     * @param y Współrzędna y.
     * @return zmienna bool, wskazująca czy kursor myszy jest na elemencie. Aktualnie na sztywno na false
     */
    @Override
    public boolean isInside(int x, int y) {
        return false;
    }


    @Override
    public void performAction() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setActive(boolean active) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void highlight() {

    }

    @Override
    public void resetHighlight() {

    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public void windowResized(int width, int height) {

    }
}
