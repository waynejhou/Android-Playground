package org.waynezhou.libUtil;

public class LinqInt extends Linq<Integer> {
    private static Integer[] convert(int[] arr) {
        Integer[] newArray = new Integer[arr.length];
        int i = 0;
        for (int value : arr) {
            newArray[i++] = value;
        }
        return newArray;
    }

    public LinqInt(int[] array) {
        super(convert(array));
    }
}
