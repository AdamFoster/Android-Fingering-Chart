package net.adamfoster.android.finger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.adamfoster.android.finger.beans.BaseKey;
import net.adamfoster.android.finger.beans.Fingering;
import net.adamfoster.android.finger.beans.Instrument;
import net.adamfoster.android.finger.beans.Note;
import net.adamfoster.android.finger.beans.NoteSet;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Chart extends Activity implements OnItemSelectedListener
{
    private int mInstrumentXml;
    
    private Instrument mInstrument;
    private GestureDetector gd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        Bundle extras = getIntent().getExtras();
        mInstrumentXml = (extras != null 
                ? extras.getInt(Menu.INSTRUMENT_NAME, R.xml.flute)
                : R.xml.flute);
        
        mInstrument = parse();
        
        FingerSurface fs = (FingerSurface) findViewById(R.id.SurfaceView01);
        fs.setInstrument(mInstrument);
        
        gd = new GestureDetector(new MyGestureDetector());

        populateType();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return gd.onTouchEvent(event);// && super.onTouchEvent(event);
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
                                String c = "";
                                if (f.comment != null && f.comment.length()>1 && !f.comment.equalsIgnoreCase("basic") && !f.comment.equalsIgnoreCase("basic."))
                                {
                                    //Toast.makeText(this, f.comment, Toast.LENGTH_SHORT).show();
                                    c = " - " + f.comment;
                                }
                                TextView tv = (TextView) findViewById(R.id.chart_title);
                                tv.setText(n.name + " : " + f.name + c);
                                tv.setMovementMethod(ScrollingMovementMethod.getInstance());
                                fs.drawFingering(f);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu)
    {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        for (String s : getResources().getStringArray(R.array.instruments))
        {
            menu.add(s);
        }
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        String s = item.getTitle().toString();
        
        if (s != null)
        {
            int id = getResources().getIdentifier(s.toLowerCase(), "xml", this.getClass().getPackage().getName());
            
            if (id != 0)
            {
                //TODO: load id here!
                
                
                return true;
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private Instrument parse()
    {
        XmlResourceParser xrp = null;
        Instrument ins = new Instrument();
        
        //temp
        NoteSet ns = null;
        Note note = null;
        Fingering fingering = null;
        
        try
        {
            xrp = this.getResources().getXml(mInstrumentXml);
            
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
                        
                        fingering.keysDowns = uzip(xrp.getAttributeValue(null, "keysDown"));
                        fingering.keysTrillDowns = uzip(xrp.getAttributeValue(null, "keysTrillDown"));
                        fingering.keysTrillUp = uzip(xrp.getAttributeValue(null, "keysTrillUp"));
                        fingering.ringsDowns = uzip(xrp.getAttributeValue(null, "ringsDown"));
                        fingering.ringsTrillDowns = uzip(xrp.getAttributeValue(null, "ringsTrillDown"));
                        fingering.ringsTrillUps = uzip(xrp.getAttributeValue(null, "ringsTrillUp"));
                        fingering.keysHalfDowns = uzip(xrp.getAttributeValue(null, "keysHalfDown"));
                    }
                    /*
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
                    else if (currentTag.equals("ringsTrillDown"))
                    {
                        mode = MODE_RING_TRILLDOWN;
                    }
                    else if (currentTag.equals("ringsTrillUp"))
                    {
                        mode = MODE_RING_TRILLUP;
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
                            case MODE_RING_TRILLDOWN:
                                fingering.ringsTrillDowns.add(xrp.nextText());
                                break;
                            case MODE_RING_TRILLUP:
                                fingering.ringsTrillUps.add(xrp.nextText());
                                break;
                        }
                    }
                    // */
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

    private List<String> uzip(String k)
    {
        List<String> l = new ArrayList<String>();
        
        if (k != null && k.length() > 0)
        {
            String[] ka = k.split(",");
            for (String s : ka)
            {
                l.add(s.trim());
            }
        }
        
        return l;
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

    public void nextFingering()
    {
        Spinner fingering = (Spinner) findViewById(R.id.spin_fingering);
        int current = fingering.getSelectedItemPosition();
        int max = fingering.getCount();
        current++;
        if (current == max)
        {
            current = 0;
        }
        fingering.setSelection(current);
    }
    
    public void previousFingering()
    {
        Spinner fingering = (Spinner) findViewById(R.id.spin_fingering);
        int current = fingering.getSelectedItemPosition();
        int max = fingering.getCount();
        current--;
        if (current == -1)
        {
            current = max-1;
        }
        fingering.setSelection(current);
    }
    
    public void nextNote()
    {
        Spinner note = (Spinner) findViewById(R.id.spin_note);
        int current = note.getSelectedItemPosition();
        int max = note.getCount();
        current++;
        if (current == max)
        {
            current = 0;
        }
        note.setSelection(current);
    }

    public void previousNote()
    {
        Spinner note = (Spinner) findViewById(R.id.spin_note);
        int current = note.getSelectedItemPosition();
        int max = note.getCount();
        current--;
        if (current == -1)
        {
            current = max-1;
        }
        note.setSelection(current);
    }

    
    // Gestures
    
    class MyGestureDetector extends SimpleOnGestureListener
    {
        private static final float V_LIMIT = 400;
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY)
        {
            //Log.i("Fling", velocityX + ", " + velocityY);
            
            if (Math.abs(velocityX) > Math.abs(velocityY))
            {
                // horizontal
                if (Math.abs(velocityX) > V_LIMIT)
                {
                    if (velocityX > 0)
                    {
                        //Log.i("Fling", "L -> R");
                        previousFingering();
                    }
                    else
                    {
                        //Log.i("Fling", "R -> L");
                        nextFingering();
                    }
                    return true;
                }
            }
            else
            {
                // vertical
                if (Math.abs(velocityY) > V_LIMIT)
                {
                    if (velocityY > 0)
                    {
                        //Log.i("Fling", "T -> B");
                        previousNote();
                    }
                    else
                    {
                        //Log.i("Fling", "B -> T");
                        nextNote();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        // TODO Auto-generated method stub
    }
}
