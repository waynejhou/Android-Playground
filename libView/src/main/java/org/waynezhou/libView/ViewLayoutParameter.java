package org.waynezhou.libView;

import android.view.View;
import android.view.ViewGroup;

public class ViewLayoutParameter {
    private final View view;
    private final ViewGroup.MarginLayoutParams mlParams;
    public ViewLayoutParameter(View view){
        this.view = view;
        mlParams = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
    }

    public ViewLayoutParameter set(MarginLayoutParamsRunBlock block){
        block.run(mlParams);
        return this;
    }
    
    public int getTop(){
        return mlParams.topMargin;
    }
    public int getLeft(){
        return mlParams.leftMargin;
    }
    public int getBottom(){
        return mlParams.bottomMargin;
    }
    public int getRight(){
        return mlParams.rightMargin;
    }
    public int getWidth(){
        return mlParams.width;
    }
    public int getHeight(){
        return mlParams.height;
    }

    public ViewLayoutParameter hide(){
        mlParams.width = 0;
        mlParams.height = 0;
        mlParams.setMargins(0,0,0,0);
        return this;
    }

    public void requestLayout(){
        view.requestLayout();
    }

    @FunctionalInterface
    public interface LayoutParamsRunBlock{
        void run(ViewGroup.LayoutParams lParams);
    }

    @FunctionalInterface
    public interface MarginLayoutParamsRunBlock{
        void run(ViewGroup.MarginLayoutParams mlParams);
    }

}
