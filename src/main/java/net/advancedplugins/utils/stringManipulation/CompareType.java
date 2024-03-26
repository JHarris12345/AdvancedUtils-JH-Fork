package net.advancedplugins.utils.stringManipulation;

public enum CompareType {
    CONTAINS {
        @Override
        public boolean compare(String first, String second) {
            return first.contains(second);
        }
    },
    EQUALS {
        @Override
        public boolean compare(String first, String second) {
            return first.equalsIgnoreCase(second);
        }
    },
    STARTS_WITH {
        @Override
        public boolean compare(String first, String second) {
            return first.startsWith(second);
        }
    },
    ENDS_WITH {
        @Override
        public boolean compare(String first, String second) {
            return first.endsWith(second);
        }
    },
    REGEX {
        @Override
        public boolean compare(String first, String second) {
            return first.matches(second);
        }
    };

    public boolean compare(String first, String second) {return false;};
}
