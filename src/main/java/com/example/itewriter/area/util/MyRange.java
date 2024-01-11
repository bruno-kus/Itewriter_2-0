package com.example.itewriter.area.util;



public class MyRange implements Comparable<MyRange> {

    public int start;
    public int end;

    public MyRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public int length() {
        return end - start;
    }
    public boolean overlaps(MyRange range) {
        return (this.end > range.start && this.end <= range.end) || (this.start >= range.start && this.start < range.end);
    } // czy teraz jest idealnie z równościami?
    public boolean contains(MyRange range) {
        return range.start > this.start && range.end < this.end;
    }
    public boolean contains(int i) { return i > this.start && i < this.end; }
    public boolean notContains(MyRange range) {
        return range.start < this.start || range.end > this.end;
    }
    public static boolean overlap(MyRange r1, MyRange r2) {
        return (r1.overlaps(r2));
    }

    @Override
    public int compareTo(MyRange that) {
        return compareStart(that);
    } // to jest w porządku!

    public int compareStart(MyRange that) {
        return Integer.compare(this.start, that.start);
    }
    public int compareEnd(MyRange that) {
//        System.out.println("MyRange::compareEnd");
        return Integer.compare(this.end, that.end);
    }
    @Override
    public String toString() {
        return "MyRange[" + start + ", "+ end + ")";
    }
}
