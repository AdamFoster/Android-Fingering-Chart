package net.adamfoster.android.finger.beans;

public class BaseKey
{
    public static final int TYPE_CIRCLE = 1;
    public static final int TYPE_RECTANGLE = 2;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_ROUNDRECT = 4;
    public static final int TYPE_OVAL = 5;
    public static final int TYPE_NULL = 0;
    public static final int TYPE_UNKNOWN = -1;
    
    public String name;
    public float positionx;
    public float positiony;
    public int type;
    public float radius;
    public float width;
    public float height;
    public float angle;
    public String imglocation;
    public float rx;
    public float ry;
    
    public BaseKey()
    {
        // TODO Auto-generated constructor stub
    }

    public static int parseType(String attributeValue)
    {
        if (attributeValue == null)
        {
            return TYPE_NULL;
        }
        else if (attributeValue.toLowerCase().equals("circle"))
        {
            return TYPE_CIRCLE;
        }
        else if (attributeValue.toLowerCase().equals("rectangle"))
        {
            return TYPE_RECTANGLE;
        }
        else if (attributeValue.toLowerCase().equals("oval"))
        {
            return TYPE_OVAL;
        }
        else if (attributeValue.toLowerCase().equals("image"))
        {
            return TYPE_IMAGE;
        }
        else 
        {
            return TYPE_UNKNOWN;
        }
    }
}