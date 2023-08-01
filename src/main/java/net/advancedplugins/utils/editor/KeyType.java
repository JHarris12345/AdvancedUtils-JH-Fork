package net.advancedplugins.utils.editor;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public enum KeyType {
    INTEGER {
        @Override
        public boolean validate(String input) {
            try {
                Integer.parseInt(input);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Object process(String input, ConfigurationSection section) {
            return Integer.parseInt(input);
        }

        @Override
        public String getFriendlyName() {
            return "Number";
        }
    },
    BOOLEAN {
        @Override
        public boolean validate(String input) {
            return "true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input);
        }

        @Override
        public Object process(String input, ConfigurationSection section) {
            return Boolean.parseBoolean(input);
        }

        @Override
        public String getFriendlyName() {
            return "true/false";
        }
    },
    LIST {
        @Override
        public boolean validate(String input) {
            // You can add additional validation logic here if necessary
            return true;
        }

        @Override
        public Object process(String input, ConfigurationSection section) {
            return input;
        }

        @Override
        public String getFriendlyName() {
            return "List";
        }
    },
    DOUBLE {
        @Override
        public boolean validate(String input) {
            try {
                Double.parseDouble(input);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public Object process(String input, ConfigurationSection section) {
            return Double.parseDouble(input);
        }

        @Override
        public String getFriendlyName() {
            return "Decimal number";
        }
    },
    KEY {
        @Override
        public boolean validate(String input) {
            return true;
        }

        @Override
        public Object process(String input, ConfigurationSection section) {
            section.createSection(input);
            return KeyType.KEY;
        }

        @Override
        public String getFriendlyName() {
            return "Config Section";
        }
    },
    ITEM {
        @Override
        public boolean validate(String input) {
            return true;
        }

        @Override
        public Object process(String input, ConfigurationSection section) {
            section.createSection(input);
            return KeyType.KEY;
        }

        @Override
        public String getFriendlyName() {
            return "Item";
        }
    },
    STRING {
        @Override
        public boolean validate(String input) {
            return true;
        }

        @Override
        public Object process(String input, ConfigurationSection section) {
            return input;
        }

        @Override
        public String getFriendlyName() {
            return "Text";
        }
    },

    MATERIAL {
        @Override
        public boolean validate(String input) {
            return Material.matchMaterial(input) != null;
        }

        @Override
        public Object process(String input, ConfigurationSection section) {
            return Material.matchMaterial(input).name();
        }

        @Override
        public String getFriendlyName() {
            return "Material";
        }
    };

    public abstract String getFriendlyName();

    public abstract boolean validate(String input);

    public abstract Object process(String input, ConfigurationSection section);

    public static KeyType getKeyType(Object value) {
        if (value instanceof Integer) {
            return KeyType.INTEGER;
        } else if (value instanceof Boolean) {
            return KeyType.BOOLEAN;
        } else if (value instanceof List) {
            return KeyType.LIST;
        } else if (value instanceof String) {
            return KeyType.STRING;
        } else if (value instanceof ConfigurationSection) {
            return KeyType.KEY;
        } else {
            return null;
        }
    }
}