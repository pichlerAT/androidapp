package fry.oldschool.utils;

public class Logger {

    public static final int MaxLogs = 200;

    protected static int index = 0;

    protected static String[] logs = new String[MaxLogs];

    static {
        logs[0] = "Logger" ;
    }

    public static void Log(String log) {
        ++index;
        if(index == MaxLogs) {
            index = 0;
        }
        logs[index] = log;
    }

    public static String getString() {
        String log = logs[index];

        for(int i=index+1; i!=index; ++i) {
            if(i == MaxLogs) {
                i = 0;
            }
            if(logs[i] == null) {
                continue;
            }
            log += "\n" + logs[i];
        }

        return log;
    }

}
