package edu.claflin.cyfinder.internal.ui.utils;

public class ComboItem {
    private String key;
    private int value;

    public ComboItem(String key, int value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return key;
    }

    public String getKey()
    {
        return key;
    }

    public int getValue()
    {
        return value;
    }
}
