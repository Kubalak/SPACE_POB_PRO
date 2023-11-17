package pl.psk.termdemo.model.keys;

import lombok.NonNull;
import lombok.Value;

@Value
public class KeyInfo {
    String value;
    boolean isFunctional;
    KeyLabel label;

    /**
     * Constructor for functional keys
     * @param label - label of key
     */
    public KeyInfo( KeyLabel label) {
        this.value = "";
        this.isFunctional = true;
        this.label = label;
    }

    /**
     * Constructor for non-functional keys
     * @param value - value of key
     */
    public KeyInfo(@NonNull String value, @NonNull KeyLabel label) {
        this.value = value;
        this.isFunctional = false;
        this.label = label;
    }

    public boolean isFunctional() {
        return isFunctional;
    }

}
