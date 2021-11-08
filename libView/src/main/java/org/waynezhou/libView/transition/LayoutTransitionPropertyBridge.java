package org.waynezhou.libView.transition;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public abstract class LayoutTransitionPropertyBridge {
    protected LayoutTransitionPropertyBridge() {
    }
    
    public abstract float get(View view);
    
    public abstract void set(View view, float value);
    
    protected static class Width extends LayoutTransitionPropertyBridge {
        
        @Override
        public float get(View view) {
            return view.getWidth();
        }
        
        @Override
        public void set(View view, float value) {
            view.getLayoutParams().width = Math.round(value);
        }
    }
    
    protected static class Height extends LayoutTransitionPropertyBridge {
        
        @Override
        public float get(View view) {
            return view.getHeight();
        }
        
        @Override
        public void set(View view, float value) {
            view.getLayoutParams().height = Math.round(value);
        }
    }
    
    protected static class Left extends LayoutTransitionPropertyBridge {
        
        @Override
        public float get(View view) {
            return view.getLeft();
        }
        
        @Override
        public void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = Math.round(value);
        }
    }
    
    protected static class Right extends LayoutTransitionPropertyBridge {
        
        @Override
        public float get(View view) {
            return view.getRight();
        }
        
        @Override
        public void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = Math.round(value);
        }
    }
    
    protected static class Top extends LayoutTransitionPropertyBridge {
        
        @Override
        public float get(View view) {
            return view.getTop();
        }
        
        @Override
        public void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = Math.round(value);
        }
    }
    
    protected static class Bottom extends LayoutTransitionPropertyBridge {
        
        @Override
        public float get(View view) {
            return view.getBottom();
        }
        
        @Override
        public void set(View view, float value) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin = Math.round(value);
        }
    }
    
    protected static class Gravity extends LayoutTransitionPropertyBridge {
        
        @Override
        public float get(View view) {
            if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                return ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity;
            } else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                return ((FrameLayout.LayoutParams) view.getLayoutParams()).gravity;
            }
            assert false;
            return 0f;
        }
        
        @Override
        public void set(View view, float value) {
            if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) view.getLayoutParams()).gravity = (int) value;
            } else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) view.getLayoutParams()).gravity = (int) value;
            }
            assert false;
        }
    }
    
    protected static class ScaleX extends LayoutTransitionPropertyBridge {
        @Override
        public float get(View view) {
            return view.getScaleX();
        }
        
        @Override
        public void set(View view, float value) {
            view.setScaleX(value);
        }
    }
    
    protected static class ScaleY extends LayoutTransitionPropertyBridge {
        
        @Override
        public float get(View view) {
            return view.getScaleY();
        }
        
        @Override
        public void set(View view, float value) {
            view.setScaleY(value);
        }
    }
    /**
     public static class Rotation extends LayoutTransitionPropertyBridge {
    
    @Override public float get(View view) {
    return view.getRotation();
    }
    
    @Override public void set(View view, float value) {
    view.setRotation(value);
    }
    }
     
     public static class PivotX extends LayoutTransitionPropertyBridge {
    
    @Override public float get(View view) {
    return view.getPivotX();
    }
    
    @Override public void set(View view, float value) {
    view.setPivotX(value);
    }
    }
     
     public static class PivotY extends LayoutTransitionPropertyBridge {
    
    @Override public float get(View view) {
    return view.getPivotY();
    }
    
    @Override public void set(View view, float value) {
    view.setPivotY(value);
    }
    }
     
     
     
     public static class Z extends LayoutTransitionPropertyBridge {
    
    @Override public float get(View view) {
    return view.getZ();
    }
    
    @Override public void set(View view, float value) {
    view.setZ(value);
    }
    }
     
     
     public static class TranslationX extends LayoutTransitionPropertyBridge {
     protected static void update(View view){
     Float value = temp.get(view.hashCode());
     if(value==null) return;
     view.setTranslationX(value);
     LogHelper.i(value);
     temp.remove(view.hashCode());
     }
     private static final Map<Integer, Float> temp = Collections.synchronizedMap(new HashMap<>());
     @Override public float get(View view) {
     if(temp.containsKey(view.hashCode())) return temp.get(view.hashCode());
     return view.getTranslationX();
     }
     
     @Override public void set(View view, float value) {
     temp.put(view.hashCode(), value);
     }
     }
     
     public static class TranslationY extends LayoutTransitionPropertyBridge {
     protected static void update(View view){
     Float value = temp.get(view.hashCode());
     if(value==null) return;
     view.setTranslationY(value);
     LogHelper.i(value);
     temp.remove(view.hashCode());
     }
     private static final Map<Integer, Float> temp = Collections.synchronizedMap(new HashMap<>());
     @Override public float get(View view) {
     if(temp.containsKey(view.hashCode())) return temp.get(view.hashCode());
     return view.getTranslationY();
     }
     
     @Override public void set(View view, float value) {
     temp.put(view.hashCode(), value);
     }
     }*/
}
