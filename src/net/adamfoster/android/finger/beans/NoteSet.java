package net.adamfoster.android.finger.beans;

import java.util.ArrayList;
import java.util.List;

public class NoteSet
{
    public String name;
    public List<Note> notes;
    
    public NoteSet()
    {
        notes = new ArrayList<Note>();
    }
}
