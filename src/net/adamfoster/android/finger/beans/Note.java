package net.adamfoster.android.finger.beans;

import java.util.ArrayList;
import java.util.List;

public class Note
{
    public String name;
    public List<Fingering> fingerings;
    
    public Note()
    {
        fingerings = new ArrayList<Fingering>();
    }
}
