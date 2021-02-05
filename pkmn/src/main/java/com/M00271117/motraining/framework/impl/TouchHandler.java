package com.M00271117.motraining.framework.impl;

import java.util.List;

import android.view.View.OnTouchListener;

import com.M00271117.motraining.framework.Input.TouchEvent;

public interface TouchHandler extends OnTouchListener {
    public boolean isTouchDown(int pointer);
    
    public int getTouchX(int pointer);
    
    public int getTouchY(int pointer);
    
    public List<TouchEvent> getTouchEvents();
}
