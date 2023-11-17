package pl.psk.termdemo.model.color;

import lombok.Getter;

@Getter
public enum ANSIColors {

    /**
     * Colors for text
     */
    TEXT_BLACK("\033[30m"),
    TEXT_RED("\033[31m"),
    TEXT_GREEN("\033[32m"),
    TEXT_YELLOW("\033[33m"),
    TEXT_BLUE("\033[34m"),
    TEXT_MAGENTA("\033[35m"),
    TEXT_CYAN("\033[36m"),
    TEXT_WHITE("\033[37m"),
    TEXT_BRIGHT_BLACK("\033[90m"),
    TEXT_BRIGHT_RED("\033[91m"),
    TEXT_BRIGHT_GREEN("\033[92m"),
    TEXT_BRIGHT_YELLOW("\033[93m"),
    TEXT_BRIGHT_BLUE("\033[94m"),
    TEXT_BRIGHT_MAGENTA("\033[95m"),
    TEXT_BRIGHT_CYAN("\033[96m"),
    TEXT_BRIGHT_WHITE("\033[97m"),

    /**
     * Colors for background
     */
    BG_BLACK("\033[40m"),
    BG_RED("\033[41m"),
    BG_GREEN("\033[42m"),
    BG_YELLOW("\033[43m"),
    BG_BLUE("\033[44m"),
    BG_MAGENTA("\033[45m"),
    BG_CYAN("\033[46m"),
    BG_WHITE("\033[47m"),
    BG_BRIGHT_BLACK("\033[100m"),
    BG_BRIGHT_RED("\033[101m"),
    BG_BRIGHT_GREEN("\033[102m"),
    BG_BRIGHT_YELLOW("\033[103m"),
    BG_BRIGHT_BLUE("\033[104m"),
    BG_BRIGHT_MAGENTA("\033[105m"),
    BG_BRIGHT_CYAN("\033[106m"),
    BG_BRIGHT_WHITE("\033[107m"),

    /**
     * Other colors
     */
    RESET("\033[0m");

    private final String code;

    ANSIColors(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get xterm color for text
     * @param color - color code 0-255
     *              (<a href="https://jonasjacek.github.io/colors/">...</a>)
     * @return - xterm color
     */
    public static String getXtermTextColor(int color) {
        return "\033[38;5;" + color + "m";
    }

    /**
     * Get xterm color for background
     * @param color - color code 0-255
     *              (<a href="https://jonasjacek.github.io/colors/">...</a>)
     * @return - xterm color
     */
    public static String getXtermBgColor(int color) {
        return "\033[48;5;" + color + "m";
    }
}
