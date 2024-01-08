package pl.psk.termdemo.model.color;

import lombok.Getter;

/**
 * Kody kolorów tekstu oraz tła.
 */
@Getter
public enum ANSIColors {

    /**
     * Tekst czarny.
     */
    TEXT_BLACK("\033[30m"),
    /**
     * Tekst czerwony.
     */
    TEXT_RED("\033[31m"),
    /**
     * Tekst zielony.
     */
    TEXT_GREEN("\033[32m"),
    /**
     * Tekst żółty.
     */
    TEXT_YELLOW("\033[33m"),
    /**
     * Tekst niebieski.
     */
    TEXT_BLUE("\033[34m"),
    /**
     * Tekst magenta.
     */
    TEXT_MAGENTA("\033[35m"),
    /**
     * Tekst cyjan.
     */
    TEXT_CYAN("\033[36m"),
    /**
     * Tekst biały.
     */
    TEXT_WHITE("\033[37m"),
    /**
     * Tekst czarny jasny.
     */
    TEXT_BRIGHT_BLACK("\033[90m"),
    /**
     * Tekst czerwony jasny.
     */
    TEXT_BRIGHT_RED("\033[91m"),
    /**
     * Tekst zielony jasny.
     */
    TEXT_BRIGHT_GREEN("\033[92m"),
    /**
     * Tekst żółty jasny.
     */
    TEXT_BRIGHT_YELLOW("\033[93m"),
    /**
     * Tekst niebieski jasny.
     */
    TEXT_BRIGHT_BLUE("\033[94m"),
    /**
     * Tekst magenta jasny.
     */
    TEXT_BRIGHT_MAGENTA("\033[95m"),
    /**
     * Tekst cyjan jasny.
     */
    TEXT_BRIGHT_CYAN("\033[96m"),
    /**
     * Tekst biały jasny.
     */
    TEXT_BRIGHT_WHITE("\033[97m"),

    /**
     * Tło czarne.
     */
    BG_BLACK("\033[40m"),
    /**
     * Tło czerwone.
     */
    BG_RED("\033[41m"),
    /**
     * Tło zielone.
     */
    BG_GREEN("\033[42m"),
    /**
     * Tło żółte.
     */
    BG_YELLOW("\033[43m"),
    /**
     * Tło niebieskie.
     */
    BG_BLUE("\033[44m"),
    /**
     * Tło magenta.
     */
    BG_MAGENTA("\033[45m"),
    /**
     * Tło cyjan.
     */
    BG_CYAN("\033[46m"),
    /**
     * Tło białe.
     */
    BG_WHITE("\033[47m"),
    /**
     * Tło czarne jasne.
     */
    BG_BRIGHT_BLACK("\033[100m"),
    /**
     * Tło czerwone jasne.
     */
    BG_BRIGHT_RED("\033[101m"),
    /**
     * Tło zielone jasne.
     */
    BG_BRIGHT_GREEN("\033[102m"),
    /**
     * Tło żółte jasne.
     */
    BG_BRIGHT_YELLOW("\033[103m"),
    /**
     * Tło niebieskie jasne.
     */
    BG_BRIGHT_BLUE("\033[104m"),
    /**
     * Tło magenta jasne.
     */
    BG_BRIGHT_MAGENTA("\033[105m"),
    /**
     * Tło cyjan jasne.
     */
    BG_BRIGHT_CYAN("\033[106m"),
    /**
     * Tło białe jasne.
     */
    BG_BRIGHT_WHITE("\033[107m"),

    /**
     * Resetuje efekty koloru (tło i tekst).
     */
    RESET("\033[0m");

    /**
     * Reprezentuje kolor.
     */
    private final String code;

    /**
     * Domyślny konstruktor
     * @param code Kolor.
     */
    ANSIColors(String code) {
        this.code = code;
    }

    /**
     * Pozwala na odczytanie kodu koloru.
     * @return Kod koloru w postaci String.
     */
    public String getCode() {
        return code;
    }
}
