package com.mentalfrostbyte.jello.gui.unmapped;

/**
 * Interface for handling various GUI events.
 */
public interface IGuiEventListener {

    /**
     * Called when a character is typed.
     *
     * @param var1 The character that was typed.
     */
    void charTyped(char var1);

    /**
     * Called when a key is pressed.
     *
     * @param var1 The key code that was pressed.
     */
    void keyPressed(int var1);

    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param mouseButton The button of the mouse that was clicked.
     * @return True if the event was handled, false otherwise.
     */
    boolean onClick(int mouseX, int mouseY, int mouseButton);

    /**
     * Called when a second mouse button is clicked.
     *
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param mouseButton The button of the mouse that was clicked.
     */
    void onClick2(int mouseX, int mouseY, int mouseButton);

    /**
     * Called when a third mouse button is clicked.
     *
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param mouseButton The button of the mouse that was clicked.
     */
    void onClick3(int mouseX, int mouseY, int mouseButton);

    /**
     * Called when a float event occurs.
     *
     * @param var1 The float value associated with the event.
     */
    void voidEvent3(float var1);
}
