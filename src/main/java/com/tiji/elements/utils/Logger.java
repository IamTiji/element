package com.tiji.elements.utils;

import com.tiji.elements.Game;
import org.lwjgl.opengl.GL43;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Logger {
    private static boolean detailedCloseMessage = false;

    public enum Severity {
        DBG("\u001B[3m\u001B[90m"),
        INF(""                   ),
        WRN("\u001B[33m"         ),
        ERR("\u001B[91m\u001B[1m");

        public final String formatter;
        Severity(String formatter) {
            this.formatter = formatter;
        }
    }

    public static class STDIOAttachment extends PrintStream {
        private final Severity severity;
        StringBuilder buffer = new StringBuilder();
        public STDIOAttachment(PrintStream out, Severity severity) {
            super(out);
            this.severity = severity;
        }

        @Override
        public void println(String s) {
            Logger.log(buffer.toString() + s, severity);
            buffer.setLength(0);
        }

        @Override
        public void println(Object x) {
            println(String.valueOf(x));
        }

        @Override
        public void print(String s) {
            if (s == null) return;
            for (char c : s.toCharArray()) {
                if (c == '\n') {
                    Logger.log(buffer.toString(), severity);
                    buffer.setLength(0);
                } else {
                    buffer.append(c);
                }
            }
        }

        @Override
        public void print(Object obj) {
            print(String.valueOf(obj));
        }
    }

    public static void init() {
        info("Replacing stderr...");

        System.setErr(new STDIOAttachment(System.err, Severity.ERR));
    }

    private static void log(String message, Severity severity, Object... args) {
        String formattedMessage = "%s[%s] %s: %s\u001B[0m".formatted(
                severity.formatter,
                getFormattedTime(),
                severity.name(),
                String.format(message, args)
        );
        if (severity == Severity.ERR) {
            detailedCloseMessage = true;
        }

        if (severity == Severity.DBG && Game.isDevelopment) {
            System.out.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }
    }

    private static String getFormattedTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static void debug(String message, Object... args) {
        log(message, Severity.DBG, args);
    }

    public static void info(String message, Object... args) {
        log(message, Severity.INF, args);
    }

    public static void warning(String message, Object... args) {
        log(message, Severity.WRN, args);
    }

    public static void error(String message, Object... args) {
        log(message, Severity.ERR, args);
    }

    public static void close() {
        if (detailedCloseMessage) {
            debug("Writing this because run had unhandled exception...");
            debug(""); // newline
            HashMap<String, String> sysinfo = new HashMap<>();

            sysinfo.put("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            sysinfo.put("Java Version", System.getProperty("java.version"));
            sysinfo.put("Java Vendor", System.getProperty("java.vendor"));
            sysinfo.put("CPU Architecture", System.getProperty("os.arch"));
            sysinfo.put("Total Memory", String.format("%,d MB", Runtime.getRuntime().totalMemory() / (1024 * 1024)));
            sysinfo.put("Free Memory", String.format("%,d MB", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

            String gpuName;
            if (Game.window == null) gpuName = "(GL not init yet)";
            else gpuName = GL43.glGetString(GL43.GL_RENDERER);
            sysinfo.put("GPU", gpuName);

            for (Map.Entry<String, String> entry : sysinfo.entrySet()) {
                debug("System info: %s - %s", entry.getKey(), entry.getValue());
            }
        }
    }
}
