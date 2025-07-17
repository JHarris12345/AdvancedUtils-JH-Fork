package net.advancedplugins.utils.commands.argument;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FutureArgument<T> {
    @Getter private final T object;
    private final Runnable finishRunnable;

    public void finish() {
        this.finishRunnable.run();
    }
}
