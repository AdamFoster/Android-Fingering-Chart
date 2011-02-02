package net.adamfoster.android.finger;

import java.io.IOException;

import net.adamfoster.android.finger.beans.BaseKey;
import net.adamfoster.android.finger.beans.Fingering;
import net.adamfoster.android.finger.beans.Instrument;
import net.adamfoster.android.finger.beans.Note;
import net.adamfoster.android.finger.beans.NoteSet;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Chart extends Activity implements OnItemSelectedListener
{
    private static final int MODE_DOWN = 1;
    private static final int MODE_TRILLDOWN = 2;
    private static final int MODE_TRILLUP = 3;
    private static final int MODE_RING_DOWN = 4;
    
    private String mInstrumentName;
    private String mType;
    
    private Instrument mInstrument;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        Bundle extras = getIntent().getExtras();
        mInstrumentName = (extras != null 
                ? extras.getString(Menu.INSTRUMENT_NAME)
                : "Flute");
        mType = (extras != null 
                ? extras.getString(Menu.TYPE_NAME)
                : "Notes"
                );
        
        mInstrument = parse();
        
        FingerSurface fs = (FingerSurface) findViewById(R.id.SurfaceView01);
        //fs.setBackgroundDrawable(this.getResources().getDrawable(mInstrument.image));
        fs.setInstrument(mInstrument);
        populateType();
    }

    private void populateType()
    {
        Spinner s = (Spinner) findViewById(R.id.spin_type);
        ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for (NoteSet t : mInstrument.sets)
        {
            aa.add(t.name);
        }
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(aa);
        s.setOnItemSelectedListener(this);
        s.setSelection(0);
    }
    
    private void populateNotes(String type)
    {
        Spinner s = (Spinner) findViewById(R.id.spin_note);
        ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for (NoteSet t : mInstrument.sets)
        {
            if (t.name.equals(type))
            {
                for (Note note : t.notes)
                {
                    aa.add(note.name);
                }
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(aa);
                s.setOnItemSelectedListener(this);
                s.setSelection(0);                
            }
        }
    }
        
    private void populateFingerings(String type, String note)
    {
        Spinner s = (Spinner) findViewById(R.id.spin_fingering);
        ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        for (NoteSet t : mInstrument.sets)
        {
            if (t.name.equals(type))
            {
                for (Note n : t.notes)
                {
                    if (n.name.equals(note))
                    {
                        for (Fingering f : n.fingerings)
                        {
                            aa.add(f.name);
                        }
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        s.setAdapter(aa);
                        s.setOnItemSelectedListener(this);
                        s.setSelection(0);                
                    }
                }
            }
        }
    }
    
    private void showFingering(String type, String note, String fingering)
    {
        FingerSurface fs = (FingerSurface) findViewById(R.id.SurfaceView01);
        
        for (NoteSet t : mInstrument.sets)
        {
            if (t.name.equals(type))
            {
                for (Note n : t.notes)
                {
                    if (n.name.equals(note))
                    {
                        for (Fingering f : n.fingerings)
                        {
                            if (f.name.equals(fingering))
                            {
                                if (f.comment != null && f.comment.length()>1)
                                {
                                    Toast.makeText(this, f.comment, Toast.LENGTH_SHORT).show();
                                }
                                fs.drawFingering(mInstrument.baseKeys, f);
                            }
                        }
                    }
                }
            }
        }
    }



    private Instrument parse()
    {
        XmlResourceParser xrp = null;
        Instrument ins = new Instrument();
        
        //temp
        NoteSet ns = null;
        Note note = null;
        Fingering fingering = null;
        int mode = MODE_DOWN;
        
        try
        {
            //int xmlid = this.getResources().getIdentifier(mInstrument.toLowerCase(), "xml", "net.adamfoster.android.finger");
            xrp = this.getResources().getXml(R.xml.flute);
            
            while(xrp.getEventType() != XmlResourceParser.END_DOCUMENT)
            {
                if (xrp.getEventType() == XmlResourceParser.START_TAG)
                {
                    String currentTag = xrp.getName();
                    if (currentTag.equals("instrument"))
                    {
                        //String instrumentName = xrp.getAttributeValue(null, "name");
                        ins.name = xrp.getAttributeValue(null, "name");
                        ins.image = xrp.getAttributeResourceValue(null, "image", 0);
                    }
                    else if (currentTag.equals("baseKey"))
                    {
                        BaseKey bk = new BaseKey();
                        bk.name = xrp.getAttributeValue(null, "name");
                        bk.positionx = xrp.getAttributeFloatValue(null, "positionx", 0);
                        bk.positiony = xrp.getAttributeFloatValue(null, "positiony", 0);
                        bk.type = BaseKey.parseType(xrp.getAttributeValue(null, "type"));
                        bk.radius = xrp.getAttributeFloatValue(null, "radius", 0);
                        bk.width = xrp.getAttributeFloatValue(null, "width", 0);
                        bk.height = xrp.getAttributeFloatValue(null, "height", 0);
                        bk.imglocation = xrp.getAttributeValue(null, "imglocation");
                        ins.baseKeys.add(bk);
                    }
                    else if (currentTag.equals("set"))
                    {
                        ns = new NoteSet();
                        ns.name = xrp.getAttributeValue(null, "name");
                    }
                    else if (currentTag.equals("note"))
                    {
                        note = new Note();
                        note.name = xrp.getAttributeValue(null, "name");
                    }
                    else if (currentTag.equals("fingering"))
                    {
                        fingering = new Fingering();
                        fingering.name = xrp.getAttributeValue(null, "name");
                        fingering.comment = xrp.getAttributeValue(null, "comment");
                    }
                    else if (currentTag.equals("keysDown"))
                    {
                        mode = MODE_DOWN;
                    }
                    else if (currentTag.equals("keysTrillDown"))
                    {
                        mode = MODE_TRILLDOWN;
                    }
                    else if (currentTag.equals("keysTrillUp"))
                    {
                        mode = MODE_TRILLUP;
                    }
                    else if (currentTag.equals("ringsDown"))
                    {
                        mode = MODE_RING_DOWN;
                    }
                    else if (currentTag.equals("key"))
                    {
                        switch (mode)
                        {
                            case MODE_DOWN:
                                fingering.keysDowns.add(xrp.nextText());
                                break;
                            case MODE_TRILLDOWN:
                                fingering.keysTrillDowns.add(xrp.nextText());
                                break;
                            case MODE_TRILLUP:
                                fingering.keysTrillUp.add(xrp.nextText());
                                break;
                            case MODE_RING_DOWN:
                                fingering.ringsDowns.add(xrp.nextText());
                                break;
                        }
                    }
                }
                else if (xrp.getEventType() == XmlResourceParser.END_TAG)
                {
                    String currentTag = xrp.getName();
                    if (currentTag.equals("fingering"))
                    {
                        note.fingerings.add(fingering);
                    }
                    else if (currentTag.equals("note"))
                    {
                        ns.notes.add(note);
                    }
                    else if (currentTag.equals("set"))
                    {
                        ins.sets.add(ns);
                    }
                }
                    
                xrp.next();
            }
        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
            Log.e("Help", "Help me2");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("Help", "Help me");
        }
        finally
        {
            if (xrp != null)
            {
                xrp.close(); 
            }
        }
        
        return ins;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        if (parent.getId() == R.id.spin_type)
        {
            //Toast.makeText(parent.getContext(), "Type " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
            populateNotes(parent.getItemAtPosition(pos).toString());
        }
        else if (parent.getId() == R.id.spin_note)
        {
            //Toast.makeText(parent.getContext(), "Note " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
            Spinner type = (Spinner) findViewById(R.id.spin_type);
            populateFingerings(type.getSelectedItem().toString(), parent.getItemAtPosition(pos).toString());
        }
        else if (parent.getId() == R.id.spin_fingering)
        {
            //Toast.makeText(parent.getContext(), "Fingering " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
            Spinner type = (Spinner) findViewById(R.id.spin_type);
            Spinner note = (Spinner) findViewById(R.id.spin_note);
            showFingering(type.getSelectedItem().toString(), note.getSelectedItem().toString(), parent.getItemAtPosition(pos).toString());
        }
        else
        {
            Toast.makeText(parent.getContext(), "No idea " + view.getId(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        // do nothing
    }
}
