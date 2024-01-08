package pl.psk.termdemo.uimanager;

import lombok.Data;
import lombok.NonNull;

/**
 * Klasa odpowiedzialna za pojedynczy <i>"piksel"</i> - komórkę ekranu.
 */
@Data
public class ScreenCell {
    /**
     * Znak wewnątrz komórki.
     */
    private char character;

    /**
     * Kolor tekstu.
     */
    @NonNull
    private String textColor;

    /**
     * Kolor tła w komórce.
     */
    @NonNull
    private String bgColor;

    /**
     * Domyślny konstruktor. Zawiera znak, kolor tekstu i tła.
     *
     * @param character Znak, który ma zostać wyświetlony.
     * @param textColor Kolor tekstu. Nie może być <i style="color: orange;">null</i>.
     * @param bgColor Kolor tła w komórce. Nie może być <i style="color: orange;">null</i>.
     * @throws IllegalArgumentException Jeśli przekazano null do <i>textColor</i> lub <i>bgColor</i>.
     */
    public ScreenCell(char character, @NonNull String textColor, @NonNull String bgColor) {
        this.character = character;
        this.textColor = textColor;
        this.bgColor = bgColor;
    }
}
