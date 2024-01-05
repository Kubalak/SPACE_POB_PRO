package pl.psk.termdemo.model.keys;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// TODO: Sprawdzić działanie z programami innymi niż PuTTY.
public class KeyboardHandler {

    // Logger for debugging and trace information
    private static final Logger logger = LoggerFactory.getLogger(KeyboardHandler.class);

    // Map to hold key codes and corresponding KeyInfo
    private final Map<String, KeyInfo> keys = new HashMap<>();

    public KeyboardHandler() {
        initKeys();
    }

    public void initKeys() {
        addKey(new KeyInfo(KeyLabel.CTRL_A), 1);
        addKey(new KeyInfo(KeyLabel.CTRL_C), 3);
        addKey(new KeyInfo(KeyLabel.CTRL_D), 4);
        addKey(new KeyInfo(KeyLabel.CTRL_E), 5);
        addKey(new KeyInfo(KeyLabel.ARROW_UP), 27, 91, 65);
        addKey(new KeyInfo(KeyLabel.CTRL_ARROW_UP), 27, 79, 65);
        addKey(new KeyInfo(KeyLabel.ARROW_DOWN), 27, 91, 66);
        addKey(new KeyInfo(KeyLabel.CTRL_ARROW_DOWN), 27, 79, 66);
        addKey(new KeyInfo(KeyLabel.ARROW_RIGHT), 27, 91, 67);
        addKey(new KeyInfo(KeyLabel.CTRL_ARROW_RIGHT), 27, 79, 67);
        addKey(new KeyInfo(KeyLabel.ARROW_LEFT), 27, 91, 68);
        addKey(new KeyInfo(KeyLabel.CTRL_ARROW_LEFT), 27, 79, 68);
        addKey(new KeyInfo(KeyLabel.CTRL_ARROW_RIGHT_ALT), 27, 91, 49, 59, 53, 67);
        addKey(new KeyInfo(KeyLabel.CTRL_ARROW_LEFT_ALT), 27, 91, 49, 59, 53, 68);
        addKey(new KeyInfo(KeyLabel.CTRL_ARROW_UP_ALT),27, 91, 49, 59, 53, 65);
        addKey(new KeyInfo(KeyLabel.CTRL_ARROW_DOWN_ALT),27, 91, 49, 59, 53, 66);
        addKey(new KeyInfo(KeyLabel.F1), 27, 79, 80);
        addKey(new KeyInfo(KeyLabel.F2), 27, 79, 81);
        addKey(new KeyInfo(KeyLabel.F3), 27, 79, 82);
        addKey(new KeyInfo(KeyLabel.F4), 27, 79, 83);
        addKey(new KeyInfo(KeyLabel.F5), 27, 91, 49, 53, 126);
        addKey(new KeyInfo(KeyLabel.F6), 27, 91, 49, 55, 126);
        addKey(new KeyInfo(KeyLabel.F7), 27, 91, 49, 56, 126);
        addKey(new KeyInfo(KeyLabel.F8), 27, 91, 49, 57, 126);
        addKey(new KeyInfo(KeyLabel.F9), 27, 91, 50, 48, 126);
        addKey(new KeyInfo(KeyLabel.F10), 27, 91, 50, 49, 126);
        addKey(new KeyInfo(KeyLabel.F11), 27, 91, 50, 51, 126);
        addKey(new KeyInfo(KeyLabel.F12), 27, 91, 50, 52, 126);
        addKey(new KeyInfo(KeyLabel.ENTER), 13, 10);
        addKey(new KeyInfo(KeyLabel.ENTER_ALT), 13);
        addKey(new KeyInfo(KeyLabel.CTRL_B), 2);
        addKey(new KeyInfo(KeyLabel.CTRL_C), 3);
        addKey(new KeyInfo(KeyLabel.CTRL_F), 6);
        addKey(new KeyInfo(KeyLabel.CTRL_G), 7);
        addKey(new KeyInfo(KeyLabel.CTRL_H), 8);
        addKey(new KeyInfo(KeyLabel.CTRL_I), 9);
        addKey(new KeyInfo(KeyLabel.CTRL_J), 10);
        addKey(new KeyInfo(KeyLabel.CTRL_K), 11);
        addKey(new KeyInfo(KeyLabel.CTRL_L), 12);
        // addKey(new KeyInfo(KeyLabel.CTRL_M), 13); // Overlaps with Enter
        addKey(new KeyInfo(KeyLabel.CTRL_N), 14);
        addKey(new KeyInfo(KeyLabel.CTRL_O), 15);
        addKey(new KeyInfo(KeyLabel.CTRL_P), 16);
        addKey(new KeyInfo(KeyLabel.CTRL_Q), 17);
        addKey(new KeyInfo(KeyLabel.CTRL_R), 18);
        addKey(new KeyInfo(KeyLabel.CTRL_S), 19);
        addKey(new KeyInfo(KeyLabel.CTRL_T), 20);
        addKey(new KeyInfo(KeyLabel.CTRL_U), 21);
        addKey(new KeyInfo(KeyLabel.CTRL_V), 22);
        addKey(new KeyInfo(KeyLabel.CTRL_W), 23);
        addKey(new KeyInfo(KeyLabel.CTRL_X), 24);
        addKey(new KeyInfo(KeyLabel.CTRL_Y), 25);
        addKey(new KeyInfo(KeyLabel.CTRL_Z), 26);
        addKey(new KeyInfo(KeyLabel.ESC), 27);
        addKey(new KeyInfo(KeyLabel.CTRL_BACKSLASH), 28);
        addKey(new KeyInfo(KeyLabel.CTRL_CLOSE_BRACKET), 29);
        addKey(new KeyInfo(KeyLabel.CTRL_CARET), 30);
        addKey(new KeyInfo(KeyLabel.CTRL_UNDERSCORE), 31);
        addKey(new KeyInfo(" ", KeyLabel.SPACE), 32);
        addKey(new KeyInfo("!", KeyLabel.EXCLAMATION), 33);
        addKey(new KeyInfo("\"", KeyLabel.DOUBLE_QUOTE), 34);
        addKey(new KeyInfo("#", KeyLabel.HASH), 35);
        addKey(new KeyInfo("$", KeyLabel.DOLLAR), 36);
        addKey(new KeyInfo("%", KeyLabel.PERCENT), 37);
        addKey(new KeyInfo("&", KeyLabel.AMPERSAND), 38);
        addKey(new KeyInfo("'", KeyLabel.SINGLE_QUOTE), 39);
        addKey(new KeyInfo("(", KeyLabel.LEFT_PARENTHESIS), 40);
        addKey(new KeyInfo(")", KeyLabel.RIGHT_PARENTHESIS), 41);
        addKey(new KeyInfo("*", KeyLabel.ASTERISK), 42);
        addKey(new KeyInfo("+", KeyLabel.PLUS), 43);
        addKey(new KeyInfo(",", KeyLabel.COMMA), 44);
        addKey(new KeyInfo("-", KeyLabel.HYPHEN), 45);
        addKey(new KeyInfo(".", KeyLabel.PERIOD), 46);
        addKey(new KeyInfo("/", KeyLabel.SLASH), 47);
        addKey(new KeyInfo("0", KeyLabel.DIGIT_0), 48);
        addKey(new KeyInfo("1", KeyLabel.DIGIT_1), 49);
        addKey(new KeyInfo("2", KeyLabel.DIGIT_2), 50);
        addKey(new KeyInfo("3", KeyLabel.DIGIT_3), 51);
        addKey(new KeyInfo("4", KeyLabel.DIGIT_4), 52);
        addKey(new KeyInfo("5", KeyLabel.DIGIT_5), 53);
        addKey(new KeyInfo("6", KeyLabel.DIGIT_6), 54);
        addKey(new KeyInfo("7", KeyLabel.DIGIT_7), 55);
        addKey(new KeyInfo("8", KeyLabel.DIGIT_8), 56);
        addKey(new KeyInfo("9", KeyLabel.DIGIT_9), 57);
        addKey(new KeyInfo(":", KeyLabel.COLON), 58);
        addKey(new KeyInfo(";", KeyLabel.SEMICOLON), 59);
        addKey(new KeyInfo("<", KeyLabel.LESS_THAN), 60);
        addKey(new KeyInfo("=", KeyLabel.EQUALS), 61);
        addKey(new KeyInfo(">", KeyLabel.GREATER_THAN), 62);
        addKey(new KeyInfo("?", KeyLabel.QUESTION_MARK), 63);
        addKey(new KeyInfo("@", KeyLabel.AT), 64);
        addKey(new KeyInfo("A", KeyLabel.CAPITAL_A), 65);
        addKey(new KeyInfo("B", KeyLabel.CAPITAL_B), 66);
        addKey(new KeyInfo("C", KeyLabel.CAPITAL_C), 67);
        addKey(new KeyInfo("D", KeyLabel.CAPITAL_D), 68);
        addKey(new KeyInfo("E", KeyLabel.CAPITAL_E), 69);
        addKey(new KeyInfo("F", KeyLabel.CAPITAL_F), 70);
        addKey(new KeyInfo("G", KeyLabel.CAPITAL_G), 71);
        addKey(new KeyInfo("H", KeyLabel.CAPITAL_H), 72);
        addKey(new KeyInfo("I", KeyLabel.CAPITAL_I), 73);
        addKey(new KeyInfo("J", KeyLabel.CAPITAL_J), 74);
        addKey(new KeyInfo("K", KeyLabel.CAPITAL_K), 75);
        addKey(new KeyInfo("L", KeyLabel.CAPITAL_L), 76);
        addKey(new KeyInfo("M", KeyLabel.CAPITAL_M), 77);
        addKey(new KeyInfo("N", KeyLabel.CAPITAL_N), 78);
        addKey(new KeyInfo("O", KeyLabel.CAPITAL_O), 79);
        addKey(new KeyInfo("P", KeyLabel.CAPITAL_P), 80);
        addKey(new KeyInfo("Q", KeyLabel.CAPITAL_Q), 81);
        addKey(new KeyInfo("R", KeyLabel.CAPITAL_R), 82);
        addKey(new KeyInfo("S", KeyLabel.CAPITAL_S), 83);
        addKey(new KeyInfo("T", KeyLabel.CAPITAL_T), 84);
        addKey(new KeyInfo("U", KeyLabel.CAPITAL_U), 85);
        addKey(new KeyInfo("V", KeyLabel.CAPITAL_V), 86);
        addKey(new KeyInfo("W", KeyLabel.CAPITAL_W), 87);
        addKey(new KeyInfo("X", KeyLabel.CAPITAL_X), 88);
        addKey(new KeyInfo("Y", KeyLabel.CAPITAL_Y), 89);
        addKey(new KeyInfo("Z", KeyLabel.CAPITAL_Z), 90);
        addKey(new KeyInfo("[", KeyLabel.LEFT_SQUARE_BRACKET), 91);
        addKey(new KeyInfo("\\", KeyLabel.BACKSLASH), 92);
        addKey(new KeyInfo("]", KeyLabel.RIGHT_SQUARE_BRACKET), 93);
        addKey(new KeyInfo("^", KeyLabel.CARET), 94);
        addKey(new KeyInfo("_", KeyLabel.UNDERSCORE), 95);
        addKey(new KeyInfo("`", KeyLabel.BACKTICK), 96);
        addKey(new KeyInfo("a", KeyLabel.SMALL_A), 97);
        addKey(new KeyInfo("b", KeyLabel.SMALL_B), 98);
        addKey(new KeyInfo("c", KeyLabel.SMALL_C), 99);
        addKey(new KeyInfo("d", KeyLabel.SMALL_D), 100);
        addKey(new KeyInfo("e", KeyLabel.SMALL_E), 101);
        addKey(new KeyInfo("f", KeyLabel.SMALL_F), 102);
        addKey(new KeyInfo("g", KeyLabel.SMALL_G), 103);
        addKey(new KeyInfo("h", KeyLabel.SMALL_H), 104);
        addKey(new KeyInfo("i", KeyLabel.SMALL_I), 105);
        addKey(new KeyInfo("j", KeyLabel.SMALL_J), 106);
        addKey(new KeyInfo("k", KeyLabel.SMALL_K), 107);
        addKey(new KeyInfo("l", KeyLabel.SMALL_L), 108);
        addKey(new KeyInfo("m", KeyLabel.SMALL_M), 109);
        addKey(new KeyInfo("n", KeyLabel.SMALL_N), 110);
        addKey(new KeyInfo("o", KeyLabel.SMALL_O), 111);
        addKey(new KeyInfo("p", KeyLabel.SMALL_P), 112);
        addKey(new KeyInfo("q", KeyLabel.SMALL_Q), 113);
        addKey(new KeyInfo("r", KeyLabel.SMALL_R), 114);
        addKey(new KeyInfo("s", KeyLabel.SMALL_S), 115);
        addKey(new KeyInfo("t", KeyLabel.SMALL_T), 116);
        addKey(new KeyInfo("u", KeyLabel.SMALL_U), 117);
        addKey(new KeyInfo("v", KeyLabel.SMALL_V), 118);
        addKey(new KeyInfo("w", KeyLabel.SMALL_W), 119);
        addKey(new KeyInfo("x", KeyLabel.SMALL_X), 120);
        addKey(new KeyInfo("y", KeyLabel.SMALL_Y), 121);
        addKey(new KeyInfo("z", KeyLabel.SMALL_Z), 122);
        addKey(new KeyInfo("{", KeyLabel.LEFT_CURLY_BRACE), 123);
        addKey(new KeyInfo("|", KeyLabel.VERTICAL_BAR), 124);
        addKey(new KeyInfo("}", KeyLabel.RIGHT_CURLY_BRACE), 125);
        addKey(new KeyInfo("~", KeyLabel.TILDE), 126);
        addKey(new KeyInfo(KeyLabel.DELETE), 127);
        addKey(new KeyInfo(KeyLabel.ENTER), 13, 10);
        addKey(new KeyInfo(KeyLabel.FUN_INSERT), 27, 91, 50, 126);
        addKey(new KeyInfo(KeyLabel.FUN_DELETE), 27, 91, 51, 126);
        addKey(new KeyInfo(KeyLabel.FUN_HOME), 27, 91, 72);
        addKey(new KeyInfo(KeyLabel.FUN_END), 27, 91, 70);
        addKey(new KeyInfo(KeyLabel.FUN_PAGE_UP), 27, 91, 53, 126);
        addKey(new KeyInfo(KeyLabel.FUN_PAGE_DOWN), 27, 91, 54, 126);
        addKey(new KeyInfo(KeyLabel.INTERNAL_WIN_RESIZE), 255,255,0,255,255);

    }

    /**
     * Convert array of key codes to a string key.
     *
     * @param arr Array of key codes
     * @return String representation
     */
    private String arrayToKey(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Add a new key and its information to the map.
     *
     * @param info     KeyInfo object
     * @param keyCodes Key codes
     */
    private void addKey(KeyInfo info, int... keyCodes) {
        keys.put(arrayToKey(keyCodes), info);
    }

    /**
     * Retrieve KeyInfo for given key codes.
     *
     * @param keyCodes Array of key codes
     * @return KeyInfo object or null if not found
     */
    public KeyInfo getKeyInfo(int[] keyCodes) {
        logger.trace("Searching for key: " + Arrays.toString(keyCodes));
        return keys.getOrDefault(arrayToKey(keyCodes), null);
    }
}