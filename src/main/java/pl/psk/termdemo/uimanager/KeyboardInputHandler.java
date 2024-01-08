package pl.psk.termdemo.uimanager;

import pl.psk.termdemo.model.keys.KeyInfo;

/**
 * Interfejs do obsługi klawiatury.
 */
public interface KeyboardInputHandler {
    /**
     * Obsługuje wejście z klawiatury.
     * @param keyInfo Obiekt KeyInfo o wciśniętym klawiszu.
     */
    void handleKeyboardInput(KeyInfo keyInfo);
}
