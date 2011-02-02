package net.adamfoster.android.finger.beans;

import java.util.ArrayList;
import java.util.List;

public class Fingering
{
    public String name;
    public String comment;
    
    public List<String> keysDowns;
    public List<String> keysTrillDowns;
    public List<String> keysTrillUp;
    public List<String> ringsDowns;
    
    public Fingering()
    {
        keysDowns = new ArrayList<String>();
        keysTrillDowns = new ArrayList<String>();
        keysTrillUp = new ArrayList<String>();
        ringsDowns = new ArrayList<String>();
    }
}
