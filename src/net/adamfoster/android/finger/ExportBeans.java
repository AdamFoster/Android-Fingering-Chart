package net.adamfoster.android.finger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import net.adamfoster.android.finger.beans.BaseKey;
import net.adamfoster.android.finger.beans.Fingering;
import net.adamfoster.android.finger.beans.Instrument;
import net.adamfoster.android.finger.beans.Note;
import net.adamfoster.android.finger.beans.NoteSet;
import android.os.Environment;
import android.util.Log;

public class ExportBeans
{
    public void export(Instrument ins)
    {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
    
        if (Environment.MEDIA_MOUNTED.equals(state)) 
        {
            // We can read and write the media
            mExternalStorageAvailable = true;
            mExternalStorageAvailable = mExternalStorageWriteable = true;
            Log.i("Export", "Writable");
        } 
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
        {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
            Log.i("Export", "Readable");
        } 
        else 
        {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
            Log.i("Export", "Not Available");
        }
        
        if (mExternalStorageAvailable && mExternalStorageWriteable)
        {
            File file = Environment.getExternalStorageDirectory();
            //File f2 = new File(f.getAbsolutePath() + "/Android/data/net.adamfoster.android.finger/files");
            //f2.mkdirs();
            File f3 = new File(file.getAbsolutePath() + "/export.xml");
            try
            {
                f3.createNewFile();
                FileOutputStream fos = new FileOutputStream(f3);
                PrintStream ps = new PrintStream(fos);
                
                //fos.write(new String("Test").getBytes());
                /*
                ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                ps.println("<ins:instrument \n" + 
                           "    xmlns:ins=\"http://www.adamfoster.net/instrument\" \n" + 
                           "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                           "    xsi:schemaLocation=\"http://www.adamfoster.net/instrument instrument.xsd\" \n" +
                           "    xsi:noNamespaceSchemaLocation=\"instrument.xsd\" \n" +
                           "    name=\"Flute\" image=\"@drawable/flutedark\" >");
                
                ps.println("    <ins:baseKeys>");
                for (BaseKey bk : ins.baseKeys)
                {
                    ps.println("    <ins:baseKey name=\"" + bk.name + "" + ">");
                        
                }
                ps.println("");
                ps.println("");
                ps.println("");
                ps.println("    </ins:baseKeys>");
                
                
                //end ins
                ps.println("</ins:instrument>");
                // */
                for (NoteSet ns : ins.sets)
                {
                    ps.println("<set name=\"" + ns.name +"\">");
                    for (Note n : ns.notes)
                    {
                        ps.println("    <note name=\"" + n.name +"\">");
                        for (Fingering f : n.fingerings)
                        {
                            ps.println("        <fingering name=\"" + f.name + "\" comment=\"" + f.comment + "\" ");
                            if (f.keysDowns.size() > 0 ) ps.println("            keyDowns=\"" + zip(f.keysDowns) + "\" ");
                            if (f.keysTrillDowns.size() > 0 ) ps.println("            keyTrillDowns=\"" + zip(f.keysTrillDowns) + "\" ");
                            if (f.keysTrillUp.size() > 0 ) ps.println("            keyTrillUps=\"" + zip(f.keysTrillUp) + "\" ");
                            if (f.ringsDowns.size() > 0 ) ps.println("            ringsDown=\"" + zip(f.ringsDowns) + "\" ");
                            if (f.ringsTrillDowns.size() > 0 ) ps.println("            ringsTrillDowns=\"" + zip(f.ringsTrillDowns) + "\" ");
                            if (f.ringsTrillUps.size() > 0 ) ps.println("            ringsTrillUps=\"" + zip(f.ringsTrillUps) + "\" ");
                            ps.println("        />");
                        }
                        ps.println("    </note>");
                    }
                    ps.println("</set>");
                }
                
                ps.close();
                Log.e("Export", "Success");
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Export", "Failed");
            }
        }
    }
    
    private String zip(List<String> keys)
    {
        StringBuffer sb = new StringBuffer();
        for (String s : keys)
        {
            sb.append(s);
            sb.append(",");
        }
        
        return sb.deleteCharAt(sb.length()-1).toString();
    }
}
