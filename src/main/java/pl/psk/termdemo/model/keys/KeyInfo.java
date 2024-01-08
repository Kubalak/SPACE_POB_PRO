package pl.psk.termdemo.model.keys;

import lombok.NonNull;
import lombok.Value;

/**
 * Klasa przechowująca informacje o klawiszu.
 */
@Value
public class KeyInfo {
    String value;
    boolean isFunctional;
    KeyLabel label;

    /**
     * Konstruktor dla klawiszy funkcyjnych.
     * @param label Nazwa klawisza
     */
    public KeyInfo(KeyLabel label) {
        this.value = "";
        this.isFunctional = true;
        this.label = label;
    }

    /**
     * Konstruktor dla klawiszy niefunkcyjnych.
     * @param value Wartość klawisza np. <i>"a"</i>.
     * @param label Nazwa klawisza.
     */
    public KeyInfo(@NonNull String value, @NonNull KeyLabel label) {
        this.value = value;
        this.isFunctional = false;
        this.label = label;
    }

    /**
     * Zwraca informację czy klawisz jest funkcyjny.
     * @return true, jeśli jest, false w przeciwnym wypadku.
     */
    public boolean isFunctional() {
        return isFunctional;
    }

}
