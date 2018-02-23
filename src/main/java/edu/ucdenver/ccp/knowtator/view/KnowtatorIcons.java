/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;

public class KnowtatorIcons {
    private static final String RELATIVE_PATH = "/icon/";

    private static final Integer MENU_ITEM_WIDTH = 20;
    private static final Integer MENU_ITEM_HEIGHT = 20;
    private static final String UGLY_ICON = "";

    public static final String RIGHT_ICON = "icons8-forward-50.png";
    public static final String LEFT_ICON = "icons8-back-50.png";
    public static final String NEXT_ICON = "icons8-right-filled-50.png";
    public static final String PREVIOUS_ICON = "icons8-left-filled-50.png";
    public static final String INCREASE_TEXT_SIZE_ICON = "icons8-Increase Font.png";
    public static final String DECREASE_TEXT_SIZE_ICON = "icons8-Decrease Font.png";
    public static final String GRAPH_VIEWER = "icons8-edit-node-50.png";
    public static final String ADD = "icons8-plus-50.png";
    public static final String REMOVE = "icons8-remove-50.png";


    private static HashMap<String, ImageIcon> iconMap = new HashMap<>();


    static {
        ImageIcon uglyIcon = loadIcon(UGLY_ICON);
        iconMap.put(UGLY_ICON, uglyIcon);
    }

    public static ImageIcon getIcon(String iconName) {
        ImageIcon imageIcon = iconMap.get(iconName);
        if (imageIcon == null) {
            imageIcon = loadIcon(iconName);
            iconMap.put(iconName, imageIcon);
        }
        Image img = imageIcon.getImage();
        Image newimg = img.getScaledInstance(MENU_ITEM_WIDTH, MENU_ITEM_HEIGHT, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }

    private static ImageIcon loadIcon(String iconName) {
        ImageIcon imageIcon = null;
        URL iconURL = KnowtatorIcons.class.getResource(RELATIVE_PATH + iconName);
        if (iconURL != null) {
            imageIcon = new ImageIcon(iconURL);
        }

        if (imageIcon == null) {
            imageIcon = iconMap.get(UGLY_ICON);
        }
        return imageIcon;
    }
}
