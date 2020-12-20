
package com.easycloudbooks.easycloudbooks.structure;

/**
 * {@link DrawerItem} which should only display a divider.
 */
public class DrawerHeaderItem extends DrawerItem {
    public DrawerHeaderItem() {
        setIsHeader(true);
    }


    /**
     * Sets a title to the header
     *
     * @param title Title to set
     */
    public DrawerItem setTitle(String title) {
        setTextPrimary(title);
        return this;
    }

    /**
     * Gets the title of the header
     *
     * @return Title of the header
     */
    public String getTitle() {
        return getTextPrimary();
    }

    /**
     * Gets whether the header has a title set to it
     *
     * @return True if the header has a title set to it, false otherwise.
     */
    public boolean hasTitle() {
        return hasTextPrimary();
    }


    /**
     * Removes the title from the header
     */
    public DrawerItem removeTitle() {
        removeTextPrimary();
        return this;
    }
}
