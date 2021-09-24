package org.waynezhou.libView;

import androidx.constraintlayout.widget.ConstraintSet;

public class ConnectConstraint {
    private final ConstraintSet set;

    public ConnectConstraint(ConstraintSet set) {
        this.set = set;
    }

    public From layout(int id, int side) {
        return new From(set, id, side);
    }
    public Preset layout(int id) {
        return new Preset(set, id);
    }

    public static class From {
        private final ConstraintSet set;
        private final int fromId;
        private final int fromSide;

        public From(ConstraintSet set, int fromId, int fromSide) {
            this.set = set;
            this.fromId = fromId;
            this.fromSide = fromSide;
        }

        public void to(int id, int side) {
            set.connect(fromId, fromSide, id, side);
        }
    }

    public static class Preset {
        private final ConstraintSet set;
        private final int fromId;
        public Preset(ConstraintSet set, int fromId) {
            this.set = set;
            this.fromId = fromId;
        }
        public void toParentHorizonSide(){
            set.connect(fromId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(fromId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        }
        public void toParentVerticalSide(){
            set.connect(fromId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(fromId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        }
        public void toParentTop(){
            set.connect(fromId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        }
        public void toParentBottom(){
            set.connect(fromId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        }
        public void toParentStart(){
            set.connect(fromId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        }
        public void toParentEnd(){
            set.connect(fromId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        }
        public void toParent(){
            set.connect(fromId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(fromId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(fromId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.connect(fromId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        }
        public void above(int id){
            set.connect(fromId, ConstraintSet.BOTTOM, id, ConstraintSet.TOP);
        }
        public void below(int id){
            set.connect(fromId, ConstraintSet.TOP, id, ConstraintSet.BOTTOM);
        }
        public void left(int id){
            set.connect(fromId, ConstraintSet.END, id, ConstraintSet.START);
        }
        public void right(int id){
            set.connect(fromId, ConstraintSet.START, id, ConstraintSet.END);
        }
        public void horizonBetween(int id1, int id2){
            set.connect(fromId, ConstraintSet.START, id1, ConstraintSet.END);
            set.connect(fromId, ConstraintSet.END, id2, ConstraintSet.START);
        }
        public void verticalBetween(int id1, int id2){
            set.connect(fromId, ConstraintSet.TOP, id1, ConstraintSet.BOTTOM);
            set.connect(fromId, ConstraintSet.BOTTOM, id2, ConstraintSet.TOP);
        }
        public void hide(){
            set.connect(fromId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(fromId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(fromId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(fromId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        }
    }
}

