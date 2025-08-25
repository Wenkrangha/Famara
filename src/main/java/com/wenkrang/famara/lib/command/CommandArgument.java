package com.wenkrang.famara.lib.command;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public abstract class CommandArgument {
    public abstract List<String> describe();
    public abstract boolean test(@NotNull String value);

    public static class StringArgument extends CommandArgument {
        private final String description;
        final boolean nullable;

        public StringArgument(String description, boolean nullable) {
            this.description = description;
            this.nullable = nullable;
        }
        @Override
        public List<String> describe() {
            return List.of('<' + description + '>' + (nullable ? "?" : ""));
        }

        @Override
        public boolean test(@NotNull String value) {
            return nullable || !value.isEmpty();
        }
    }

    public static final class IntArgument extends StringArgument {

        public IntArgument(String description, boolean nullable) {
            super(description, nullable);
        }

        @Override
        public boolean test(@NotNull String value) {
            return Pattern.compile("[0-9]+").matcher(value).matches() ||
                    (this.nullable && value.isEmpty());
        }
    }

    public static class FixedArgument extends CommandArgument {
        private final List<String> applicable;
        final boolean nullable;

        public FixedArgument(List<String> applicable, boolean nullable) {
            this.applicable = applicable;
            this.nullable = nullable;
        }

        @Override
        public List<String> describe() {
            return applicable;
        }

        @Override
        public boolean test(@NotNull String value) {
            return applicable.contains(value) || (nullable && value.isEmpty());
        }
    }

    public static final class WeakFixedArgument extends FixedArgument {
        public WeakFixedArgument(List<String> applicable, boolean nullable) {
            super(applicable, nullable);
        }

        @Override
        public boolean test(@NotNull String value) {
            return nullable || !value.isEmpty();
        }
    }
}
