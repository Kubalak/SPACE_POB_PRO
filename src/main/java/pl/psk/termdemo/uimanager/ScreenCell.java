package pl.psk.termdemo.uimanager;

import lombok.Data;
import lombok.NonNull;

@Data
public class ScreenCell {
    /**
     * The character to be displayed in the cell.
     */
    private char character;

    /**
     * The text color of the character.
     */
    @NonNull
    private String textColor;

    /**
     * The background color of the cell.
     */
    @NonNull
    private String bgColor;

    /**
     * Sets the character, text color, and background color.
     *
     * @param character The character to be displayed.
     * @param textColor The text color of the character. Cannot be null.
     * @param bgColor The background color of the cell. Cannot be null.
     * @throws IllegalArgumentException if textColor or bgColor is null.
     */
    public ScreenCell(char character, @NonNull String textColor, @NonNull String bgColor) {
        this.character = character;
        this.textColor = textColor;
        this.bgColor = bgColor;
    }
}
