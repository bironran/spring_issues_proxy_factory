package com.rb.springissues;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LoggerBase {
    private static long startTime = System.currentTimeMillis();
    private static StringBuilder sb = new StringBuilder();
    private static Map<String, Integer> timesConstructed = new HashMap<>();
    private static int totalConstructions = 0;

    public LoggerBase() {
        Integer times = timesConstructed.compute(getClass().getCanonicalName(), (s, oldValue) -> {
            if (oldValue == null) {
                return 1;
            } else {
                return oldValue + 1;
            }
        });
        totalConstructions++;
        log("Constructor (times: " + times + ", total constructions " + totalConstructions + ")");
    }

    private String name() {
        return getClass().getSimpleName();
    }

    public static void logPlain(String message, Object... objects) {
        int i=0;
        int index = 0;
        while((index = message.indexOf("{}", i)) > -1) {
            message = message.substring(0, index) + objects[i] + message.substring(index+2, message.length());
            i++;
        }
        sb.append("+" + (System.currentTimeMillis() - startTime) + ": " + message + "\n");
    }


    public void log(String message) {
        sb.append("+" + (System.currentTimeMillis() - startTime) + ": (" + name() + "): " + message + "\n");
    }

    public void logSet(String name, Object object) {
        log("Setting " + name + " - actual class " + object.getClass().getSimpleName());
    }

    public static void print() {
        System.out.println(sb.toString());
    }

    public void nop() {
        log("Called NOP");
    }

}
