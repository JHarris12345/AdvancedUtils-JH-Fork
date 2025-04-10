package net.advancedplugins.utils.trycatch;

import java.util.function.Consumer;

public class TryCatchUtil {
    public static <T> T tryOrDefault(ITryCatch<T> iface, T or, Consumer<Exception> catchHandler) {
        try {
            return iface.run();
        } catch (Exception e) {
            if(catchHandler != null) catchHandler.accept(e);
            else e.printStackTrace();
            return or;
        }
    }

    public static <T> T tryOrDefault(ITryCatch<T> iface, T or) {
        return tryOrDefault(iface,or,null);
    }

    public static <T> T tryAndReturn(ITryCatch<T> iface) {
        return tryOrDefault(iface,null);
    }

    public static void tryRun(ITryCatch<?> iface, Consumer<Exception> catchHandler) {
        tryOrDefault(iface, null,catchHandler);
    }

    public static void tryRun(ITryCatch<?> iface) {
        tryAndReturn(iface);
    }
}
