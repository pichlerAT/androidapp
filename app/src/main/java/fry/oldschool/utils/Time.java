package fry.oldschool.utils;

public abstract class Time {

    public static class Int  {

        public int time;

        public Int(int time) {
            this.time = time;
        }

        public Int(Int time) {
            this(time.time);
        }

        public void add(int time) {
            this.time += time;
        }

        public void add(Int time) {
            add(time.time);
        }

        public void add(Short time) {
            add(time.time);
        }

        public String getString() {
            if(time > 1440) {
                int days = time/1440;
                int t = time%1440;
                return ( days + "days, " + (t/60) + "hours, " + (t%60) + "minutes" );
            }
            return ( time/60 + ":" + time%60 );
        }

        public Int copy() {
            return new Int(time);
        }

    }

    public static class Short {

        public short time;

        public Short(short time) {
            this.time = time;
        }

        public Short(Short time) {
            this(time.time);
        }

        public void add(short time) {
            this.time += time;
        }

        public void add(Short time) {
            add(time.time);
        }

        public int add(int time) {
            time += this.time;
            this.time = (short)(time%1440);
            return (time/1440);
        }

        public int add(Int time) {
            return add(time.time);
        }

        public String getString() {
            return ( time/60 + ":" + time%60 );
        }

        public Short copy() {
            return new Short(time);
        }
    }

}