package net.adamfoster.android.finger.beans;

import java.util.ArrayList;
import java.util.List;

public class Instrument
{
    public String name;
    public int image;
    public List<BaseKey> baseKeys;
    public List<NoteSet> sets;
    
    public Instrument()
    {
        name = null;
        baseKeys = new ArrayList<BaseKey>();
        sets = new ArrayList<NoteSet>();
    }
}