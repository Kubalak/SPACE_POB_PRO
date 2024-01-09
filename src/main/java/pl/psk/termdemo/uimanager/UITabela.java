package pl.psk.termdemo.uimanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.psk.termdemo.model.color.ANSIColors;
import pl.psk.termdemo.model.keys.KeyInfo;
import pl.psk.termdemo.model.keys.KeyLabel;

import java.util.List;
import java.util.regex.Pattern;

public class UITabela implements UIComponent {
        Logger logger = LoggerFactory.getLogger(pl.psk.termdemo.uimanager.UITabela.class);

        private StringBuilder textContent;
        private int x, y, width, height;
        private String bgColor = ANSIColors.BG_RED.getCode();
        private String textColor = ANSIColors.TEXT_WHITE.getCode();
        private boolean isNumeric, isPassword;
        private Pattern regexPattern = null;
        private int zIndex;
        private boolean isActive;
        private UIManager uiManager;
        private int maxCharacters = Integer.MAX_VALUE;

        // UIBorder = prostokacik w tabeli
        private List<UIBorder> borders;

        //UILabel teksty nad tabela

        private List<String> newLabels;

        private List<String> rowContents;



    public UITabela(int x, int y, int width, int height, int zIndex, UIManager uiManager, List<String> newLabels, List<String> rowContents) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.uiManager = uiManager;
        this.textContent = new StringBuilder();
        this.newLabels = newLabels;
        this.rowContents = rowContents;
    }

        public void drawAllHeaders(UITab uiTab) {
            int xPosition = this.x + 3;
            System.out.println(this.newLabels);
            for (String label : this.newLabels) {
                UILabel labelObject = new UILabel(label, xPosition, this.y -3 , 0, ANSIColors.BG_BRIGHT_BLUE.getCode(), uiManager);
                xPosition += (this.width / this.newLabels.size()) + 3;
                uiTab.addComponent(labelObject);
            }
        }

        public void drawAllRows(UITab uiTab) {
            int startingY = this.y - 2;
            int startingX = this.x;
            int count  = 1;
            int elementsPerRow = 3; // Ilość elementów w jednym rzędzie
            int totalElements = this.rowContents.size();
            System.out.println(this.newLabels.size());
            for(String rowContent : this.rowContents) {
                if (totalElements >= elementsPerRow && count > 1 && (count - 1) % elementsPerRow == 0) {
//                    startingY = startingY + this.height / (this.rowContents.size() / 3);
                    startingY = startingY + 3;
                    startingX = this.x;
                }
                UIBorder border = new UIBorder(startingX, startingY, this.width / 3 + 2, 3 , 0, uiManager, rowContent);
                count++;
                border.setTextInBorder(uiTab);
                border.setBgColor(ANSIColors.BG_BRIGHT_BLUE.getCode());
                startingX += this.width / 3 + 2;
                uiTab.addComponent(border);
            }
        }


        public void setMaxCharacters(int maxCharacters) {
            this.maxCharacters = maxCharacters;
        }

        public void setNumeric(boolean numeric) {
            isNumeric = numeric;
        }

        public void setPassword(boolean password) {
            isPassword = password;
        }

        public void setRegexPattern(String regex) {
            this.regexPattern = Pattern.compile(regex);
        }

        public String getText() {
            return textContent.toString();
        }

        @Override
        public void draw(UIManager uiManager) {

//            String fullText = isPassword ? "*".repeat(textContent.length()) : textContent.toString();
//            String displayText = fullText;
//
//            if (fullText.length() > width) {
//                displayText = fullText.substring(fullText.length() - width);
//            }
//            // Czyść cały obszar pola tekstowego
//            for (int i = 0; i < width; i++) {
//                ScreenCell emptyCell = new ScreenCell(' ', textColor, bgColor);  // Użyj pustego znaku do wyczyszczenia
//                uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, emptyCell);
//            }
//
//            // Następnie dodaj nowy tekst
//            for (int i = 0; i < displayText.length(); i++) {
//                ScreenCell cell = new ScreenCell(displayText.charAt(i), textColor, bgColor);
//                uiManager.getScreen().addPixelToLayer(x + i, y, zIndex, cell);
//            }
        }

        @Override
        public boolean isInside(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
        }

        public void appendText(KeyInfo keyInfo) {
            logger.debug("Appending text: {}", keyInfo.getValue());

            // Sprawdzenie kryteriów przed dodaniem znaku
            String newChar = keyInfo.getValue();

            // Jeżeli pole ma być numeryczne, ale znak nie jest cyfrą
            if(keyInfo.getLabel() != KeyLabel.DELETE) {
                // Jeśli znak nie jest cyfrą ani kropką
                if (isNumeric && !newChar.matches("\\d|\\.")) return;
                // Jeżeli znak nie pasuje do wzorca regex
                if (regexPattern != null && !newChar.matches(regexPattern.pattern())) return;
                // Jeśli kropka już istnieje
                if (isNumeric && textContent.indexOf(".") != -1 && keyInfo.getValue().equals(".")) return;
            }

            // Reszta logiki - kasowanie, dodawanie znaków itd.
            if (textContent.length() < maxCharacters || keyInfo.getLabel() == KeyLabel.DELETE) {
                if (keyInfo.getLabel() == KeyLabel.DELETE) {
                    if (!textContent.isEmpty()) {
                        textContent.deleteCharAt(textContent.length() - 1);

                        uiManager.refresh();
                    }
                    return;
                }

                if (keyInfo.getLabel() == KeyLabel.ENTER) {
                    performAction();
                    return;
                }

                // Dodanie nowego znaku
                textContent.append(keyInfo.getValue());

                // Ograniczenie długości tekstu do maxCharacters
                if (textContent.length() > maxCharacters)
                    textContent.deleteCharAt(0); // usuń pierwszy znak

                uiManager.refresh();
            }
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
        public int getX() {return x;}

        @Override
        public int getY() {return y;}

        @Override
        public int getWidth() {return width;}

        @Override
        public int getHeight() {return height;}

        @Override
        public void setActive(boolean active) {this.isActive = active;}

        @Override
        public boolean isActive() {return this.isActive;}

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
        public boolean isInteractable() {return true;}


        @Override
        public int getZIndex() {return zIndex;}

        public void setBgColor(String bgColor) {this.bgColor = bgColor;}

        public void setTextColor(String textColor) {this.textColor = textColor;}

    @Override
    public void windowResized(int width, int height){

    }
}
