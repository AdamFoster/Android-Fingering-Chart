package net.adamfoster.android.finger;

import net.adamfoster.android.finger.beans.BaseKey;
import net.adamfoster.android.finger.beans.Fingering;
import net.adamfoster.android.finger.beans.Instrument;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class FingerSurface extends SurfaceView implements Callback
{
    private SurfaceHolder mSurfaceHolder;
    private Instrument mInstrument;
    private Paint mKeyDownPaint;
    private Paint mTrillDownPaint;
    private Paint mTrillUpPaint;
    private Paint mBaseKeyPaint;
    private Fingering mFingering;
    

    public FingerSurface(Context context)
    {
        super(context);
    }

    public FingerSurface(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }
    
    public void setInstrument(Instrument instrument)
    {
        mInstrument = instrument;
        mKeyDownPaint = new Paint();
        mKeyDownPaint.setColor(this.getResources().getColor(R.color.keyDown));
        mTrillDownPaint = new Paint();
        mTrillDownPaint.setColor(this.getResources().getColor(R.color.keyTrillDown));
        mTrillUpPaint = new Paint();
        mTrillUpPaint.setColor(this.getResources().getColor(R.color.keyTrillUp));
        mBaseKeyPaint = new Paint();
        mBaseKeyPaint.setColor(this.getResources().getColor(R.color.baseKey));
        mBaseKeyPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub

    }

    public void drawFingering(Fingering f)
    {
        mFingering = f;
        
        Canvas c = null;
        try
        {
            Paint p = new Paint();
            p.setColor(this.getResources().getColor(R.color.keyDown));
            
            c = getHolder().lockCanvas(null);
            getHolder().addCallback(this);
            
            float canvasRatio = c.getWidth() / (float) c.getHeight();
            
            //draw instrument
            Drawable bg = this.getResources().getDrawable(mInstrument.image);
            float bgRatio = bg.getIntrinsicWidth() / (float) bg.getIntrinsicHeight();
            
            int w, h; // width, height
            int l, d; // left, down offset
            float ratio;
            if (canvasRatio > bgRatio) // canvas is wider than image
            {
                h = c.getHeight();
                w = (int) (h * bgRatio);
                l = (c.getWidth() - w) / 2;
                d = 0;
                ratio = c.getWidth() / (float) w;
            }
            else // canvas is taller than image
            {
                w = c.getWidth();
                h = (int) (w / bgRatio);
                l = 0;
                d = (c.getHeight() - h) / 2;
                ratio = c.getHeight() / (float) h;
            }
            bg.setBounds(new Rect(l, d, w+l, h+d));
            bg.draw(c);
         
            
            // outlines
            for (BaseKey bk : mInstrument.baseKeys)
            {
                drawKeyOutline(c, bk, mBaseKeyPaint, bg.getBounds(), bg.getBounds().height());
            }
            
            // active keys
            for (BaseKey bk : mInstrument.baseKeys)
            {
                if (f.keysDowns.contains(bk.name))
                {
                    drawKey(c, bk, mKeyDownPaint, bg.getBounds(), bg.getBounds().height());
                }
                else if (f.keysTrillDowns.contains(bk.name))
                {
                    drawKey(c, bk, mTrillDownPaint, bg.getBounds(), bg.getBounds().height());
                }
                else if (f.keysTrillUp.contains(bk.name))
                {
                    drawKey(c, bk, mTrillUpPaint, bg.getBounds(), bg.getBounds().height());
                }
                
                //half down keys need to be rendered separately
                if (f.keysHalfDowns.contains(bk.name))
                {
                    drawHalfKey(c, bk, mKeyDownPaint, bg.getBounds(), bg.getBounds().height());
                }
                
                //rings need to be rendered separately
                if (f.ringsDowns.contains(bk.name))
                {
                    drawRing(c, bk, mKeyDownPaint, bg.getBounds(), bg.getBounds().height());
                }
                else if (f.ringsTrillDowns.contains(bk.name))
                {
                    drawRing(c, bk, mTrillDownPaint, bg.getBounds(), bg.getBounds().height());
                }
                else if (f.ringsTrillUps.contains(bk.name))
                {
                    drawRing(c, bk, mTrillUpPaint, bg.getBounds(), bg.getBounds().height());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally 
        {
            if (c != null) 
            {
                getHolder().unlockCanvasAndPost(c);
            }
        }
    }

    private void drawKey(Canvas c, BaseKey bk, Paint p, Rect imgBounds, int scale)
    {
        switch (bk.type)
        {
            case BaseKey.TYPE_CIRCLE:
                c.drawCircle(imgBounds.left + imgBounds.width()*bk.positionx, 
                        imgBounds.top + imgBounds.height()*bk.positiony, 
                        bk.radius*scale, 
                        p);
                break;
            case BaseKey.TYPE_RECTANGLE:
                c.drawRect(imgBounds.left + imgBounds.width()*bk.positionx, 
                        imgBounds.top + imgBounds.height()*bk.positiony, 
                        imgBounds.left + imgBounds.width()*bk.positionx + bk.width*scale, 
                        imgBounds.top + imgBounds.height()*bk.positiony + bk.height*scale, 
                        p);
                break;
            case BaseKey.TYPE_ROUNDRECT:
                // FIX c.drawRoundRect(new RectF(c.getWidth()*bk.positionx, c.getHeight()*bk.positiony, c.getWidth()*bk.positionx+bk.width, c.getHeight()*bk.positiony+bk.height), bk.rx, bk.ry, p);
                break;
            case BaseKey.TYPE_IMAGE:
                //to be implemented
                break;
        }
    }

    private void drawHalfKey(Canvas c, BaseKey bk, Paint p, Rect imgBounds, int scale)
    {
        switch (bk.type)
        {
            case BaseKey.TYPE_CIRCLE:
                //c.drawCircle(imgBounds.left + imgBounds.width()*bk.positionx, 
                //        imgBounds.top + imgBounds.height()*bk.positiony, 
                //        bk.radius*scale, 
                //        p);
                
                c.drawArc(new RectF(imgBounds.left + imgBounds.width()*bk.positionx - bk.radius*scale,
                        imgBounds.top + imgBounds.height()*bk.positiony - bk.radius*scale, 
                        imgBounds.left + imgBounds.width()*bk.positionx + bk.radius*scale,
                        imgBounds.top + imgBounds.height()*bk.positiony + bk.radius*scale), 
                        90, 270, false, p);
                break;
            case BaseKey.TYPE_RECTANGLE:
                c.drawRect(imgBounds.left + imgBounds.width()*bk.positionx, 
                        imgBounds.top + imgBounds.height()*bk.positiony, 
                        (float)(imgBounds.left + imgBounds.width()*bk.positionx + bk.width*scale/1.8), 
                        imgBounds.top + imgBounds.height()*bk.positiony + bk.height*scale, 
                        p);
                break;
            case BaseKey.TYPE_ROUNDRECT:
                // FIX c.drawRoundRect(new RectF(c.getWidth()*bk.positionx, c.getHeight()*bk.positiony, c.getWidth()*bk.positionx+bk.width, c.getHeight()*bk.positiony+bk.height), bk.rx, bk.ry, p);
                break;
            case BaseKey.TYPE_IMAGE:
                //to be implemented
                break;
        }
    }

    private void drawRing(Canvas c, BaseKey bk, Paint p, Rect imgBounds, int scale)
    {
        Paint.Style s = p.getStyle();
        float sw = p.getStrokeWidth();
        p.setStyle(Paint.Style.STROKE);
        
        switch (bk.type)
        {
            case BaseKey.TYPE_CIRCLE:
                p.setStrokeWidth((float) (scale*bk.radius*.6));
                c.drawCircle(imgBounds.left + imgBounds.width()*bk.positionx, 
                        imgBounds.top + imgBounds.height()*bk.positiony, 
                        (float)(bk.radius*scale*0.7), 
                        p);
                break;
            case BaseKey.TYPE_RECTANGLE:
                p.setStrokeWidth((float) (scale*Math.min(bk.width, bk.height)*.3));

                c.drawRect(imgBounds.left + imgBounds.width()*bk.positionx, 
                        imgBounds.top + imgBounds.height()*bk.positiony, 
                        imgBounds.left + imgBounds.width()*bk.positionx + bk.width*scale, 
                        imgBounds.top + imgBounds.height()*bk.positiony + bk.height*scale, 
                        p);
                break;
            case BaseKey.TYPE_ROUNDRECT:
                // FIX c.drawRoundRect(new RectF(c.getWidth()*bk.positionx, c.getHeight()*bk.positiony, c.getWidth()*bk.positionx+bk.width, c.getHeight()*bk.positiony+bk.height), bk.rx, bk.ry, p);
                break;
            case BaseKey.TYPE_IMAGE:
                //to be implemented
                break;
        }
        
        p.setStyle(s);
        p.setStrokeWidth(sw);
    }
    
    private void drawKeyOutline(Canvas c, BaseKey bk, Paint p, Rect imgBounds, int scale)
    {
        switch (bk.type)
        {
            case BaseKey.TYPE_CIRCLE:
                c.drawCircle(imgBounds.left + imgBounds.width()*bk.positionx, 
                        imgBounds.top + imgBounds.height()*bk.positiony, 
                        bk.radius*scale, 
                        p);
                break;
            case BaseKey.TYPE_RECTANGLE:
                c.drawRect(imgBounds.left + imgBounds.width()*bk.positionx, 
                        imgBounds.top + imgBounds.height()*bk.positiony, 
                        imgBounds.left + imgBounds.width()*bk.positionx + bk.width*scale, 
                        imgBounds.top + imgBounds.height()*bk.positiony + bk.height*scale, 
                        p);
                break;
            case BaseKey.TYPE_ROUNDRECT:
                // FIX c.drawRoundRect(new RectF(c.getWidth()*bk.positionx, c.getHeight()*bk.positiony, c.getWidth()*bk.positionx+bk.width, c.getHeight()*bk.positiony+bk.height), bk.rx, bk.ry, p);
                break;
            case BaseKey.TYPE_IMAGE:
                //to be implemented
                break;
        }
    }
}

