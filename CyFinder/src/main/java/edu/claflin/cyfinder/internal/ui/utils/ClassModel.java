package edu.claflin.cyfinder.internal.ui.utils;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Represents a ListModel, a ComboBoxModel, and a ListCellRenderer all in one.
 */
public class ClassModel extends DefaultComboBoxModel<Class> implements ListCellRenderer
{

    /**
     * The Class data to use.
     */
    private final Class[] data;
    /**
     * A default renderer for making the majority of the component.
     */
    private DefaultListCellRenderer dLCR = new DefaultListCellRenderer();

    /**
     * Initialize the object.
     *
     * @param data the Class array containing the options.
     */
    public ClassModel(Class[] data)
    {
        this.data = data;
    }

    /**
     * {@inheritDoc }
     *
     * @return an integer indicating the size of the data.
     */
    @Override
    public int getSize()
    {
        return data.length;
    }

    /**
     * {@inheritDoc }
     *
     * @param index the integer index of the object queried.
     * @return the Class object at that index.
     */
    @Override
    public Class getElementAt(int index)
    {
        return data[index];
    }

    /**
     * {@inheritDoc } Overwrites the label information to be the simple class name.
     *
     * @param list         the JList being rendered.
     * @param value        the value of the object.
     * @param index        the index of the object.
     * @param isSelected   a boolean indicating if the object is selected.
     * @param cellHasFocus a boolean indicating if the cell has focus.
     * @return a JComponent rendering of the cell (JLabel).
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus)
    {
        JLabel listLabel = (JLabel) dLCR.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Class selectedClass = (Class) value;
        String original = selectedClass.getSimpleName();
        String spaced = Pattern.compile("([a-z])([A-Z])").matcher(original).replaceAll("$1 $2");
        listLabel.setText(spaced);

        return listLabel;
    }
}
