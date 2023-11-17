package pl.psk.termdemo.uimanager;

import pl.psk.termdemo.model.color.ANSIColors;

public class UIProgressBar implements UIComponent {
    private int x, y, width, height;
    private int zIndex;
    private UIManager uiManager;
    private double progress;  // Progress between 0 and 1
    private String bgColor = ANSIColors.BG_BLUE.getCode();
    private String fgColor = ANSIColors.BG_GREEN.getCode();  // Foreground color for the progress

    private String textColor = ANSIColors.TEXT_WHITE.getCode();  // Text color for the percentage

    public UIProgressBar(int x, int y, int width, int height, int zIndex, UIManager uiManager) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.uiManager = uiManager;
        this.progress = 0.0;
    }

    public void setProgress(double progress) {
        this.progress = Math.min(Math.max(progress, 0.0), 1.0);  // Clamps progress between 0 and 1
    }

    @Override
    public void draw(UIManager uiManager) {
        TUIScreen screen = uiManager.getScreen();
        int filledWidth = (int) Math.round(width * progress);  // Rounded to the nearest integer

        // Draw progress
        for (int i = 0; i < filledWidth; i++) {
            screen.addPixelToLayer(x + i, y, zIndex, new ScreenCell(' ', "", fgColor));
        }

        // Draw background for the remaining part
        for (int i = filledWidth; i < width; i++) {
            screen.addPixelToLayer(x + i, y, zIndex, new ScreenCell(' ', "", bgColor));
        }

        // Draw percentage text in the middle
        String percentageText = String.format("%.0f%%", progress * 100);  // Rounded to the nearest integer
        int textStart = x + (width - percentageText.length()) / 2;

        for (int i = 0; i < percentageText.length(); i++) {
            int pos = textStart + i;
            ScreenCell existingCell = (pos < filledWidth)
                    ? new ScreenCell(percentageText.charAt(i), textColor, fgColor)
                    : new ScreenCell(percentageText.charAt(i), textColor, bgColor);
            screen.addPixelToLayer(pos, y, zIndex, existingCell);
        }
    }









@Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public boolean isInside(int mouseX, int mouseY) {
        return false;  // ProgressBar is not interactive
    }

    @Override
    public void performAction() {
        // ProgressBar does not have any action
    }

    @Override
    public void show() {
        uiManager.addComponentToScreen(this);
    }

    @Override
    public void hide() {
        uiManager.removeComponent(this);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setActive(boolean active) {
        // No action required as ProgressBar is not interactive
    }

    @Override
    public boolean isActive() {
        return false;  // ProgressBar is not interactive
    }

    @Override
    public void highlight() {
        // No action required as ProgressBar is not interactive
    }

    @Override
    public void resetHighlight() {
        // No action required as ProgressBar is not interactive
    }

    @Override
    public boolean isInteractable() {
        return false;  // ProgressBar is not interactive
    }
}
