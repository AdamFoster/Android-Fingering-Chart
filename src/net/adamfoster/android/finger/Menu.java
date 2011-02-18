package net.adamfoster.android.finger;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class Menu extends Activity 
{
    public static final String INSTRUMENT_NAME = "INSTRUMENT_NAME";
    public static final String TYPE_NAME = "TYPE_NAME";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView info = (TextView) this.findViewById(R.id.InfoWindow);
        
        XmlResourceParser instrumentsXml = null;
        ArrayList<String> instrumentsArray = new ArrayList<String>();
        
        try
        {
            instrumentsXml = this.getResources().getXml(R.xml.instruments);
            
            while(instrumentsXml.getEventType() != XmlResourceParser.END_DOCUMENT)
            {
                if (instrumentsXml.getEventType() == XmlResourceParser.START_TAG)
                {
                    String currentTag = instrumentsXml.getName();
                    if (currentTag.equals("instrument"))
                    {
                        String instrumentName = instrumentsXml.getAttributeValue(null, "name");
                        info.setText("Found: " + instrumentName);
                        instrumentsArray.add(instrumentName); 
                    }
                }
                
                instrumentsXml.next();
            }
            
            Spinner s = (Spinner) findViewById(R.id.InstrumentSelector);
            s.setAdapter(new ArrayAdapter<String>(this, R.layout.instrument_row, instrumentsArray));
        }
        catch (XmlPullParserException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            info.setText(e.getClass().getName() + ": " + e.getMessage());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            info.setText(e.getClass().getName() + ": " + e.getMessage());
            Log.e("Help", "Help me");
        }
        finally
        {
            if (instrumentsXml != null)
            {
                instrumentsXml.close(); 
            }
        }
        
    }
    
    public void buttonClick(View view)
    {
        Intent i = new Intent(this, Chart.class);
        i.putExtra(INSTRUMENT_NAME, R.xml.flute);
        //i.putExtra(TYPE_NAME, R.id.ButtonTrills);
        startActivity(i);
    }
}