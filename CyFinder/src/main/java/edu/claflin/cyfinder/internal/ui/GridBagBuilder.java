/* 
 * Copyright 2015 Charles Allen Schultz II.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.claflin.cyfinder.internal.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Contains UI Utilities.  Currently only one.
 * 
 * @author Charles Allen Schultz II
 * @version 1.0 June 10, 2015
 */
public class GridBagBuilder {

    /**
     * Creates a GridBagConstraints object.
     * 
     * @param gridx the integer representing the x-cell.
     * @param gridy the integer representing the y-cell.
     * @param gridwidth the integer representing the x thickness.
     * @param gridheight the integer representing the y thickness.
     * @param weightx the double representing the x weight.
     * @param weighty the double representing the y weight.
     * @param fill the Swing constant representing the fill type.
     * @param anchor the Swing constant representing the anchor type.
     * @param ipadx the x padding.
     * @param ipady the y padding.
     * @param insets the Insets object.
     * @return the constructed GridBagConstraints object.
     */
    public static GridBagConstraints getConstraints(int gridx, int gridy, 
            int gridwidth, int gridheight, double weightx, double weighty, 
            int fill, int anchor, int ipadx, int ipady, Insets insets) {
        GridBagConstraints bag = new GridBagConstraints();
        bag.gridx = gridx;
        bag.gridy = gridy;
        bag.gridwidth = gridwidth;
        bag.gridheight = gridheight;
        bag.weightx = weightx;
        bag.weighty = weighty;
        bag.fill = fill;
        bag.anchor = anchor;
        bag.ipadx = ipadx;
        bag.ipady = ipady;
        bag.insets = insets;
        return bag;
    }
}
