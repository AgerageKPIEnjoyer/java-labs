import java.lang.reflect.Field;

/**
 * Overwrites the content of an existing String object through the private
 * fields of java.lang.String.
 *
 * Java 8 stores the text in "char[] value"; since Java 9 (compact strings) it is
 * "byte[] value" plus a "byte coder" flag, so both layouts are handled.
 **/
public final class StringMutator {

    private final Field value;
    private final Field coder;        // absent on Java 8
    private final Field hash;         // cached hash code
    private final Field hashIsZero;   // absent before Java 13

    public StringMutator() throws ReflectiveOperationException {
        value = open("value", true);
        coder = open("coder", false);
        hash = open("hash", false);
        hashIsZero = open("hashIsZero", false);
    }

    /** Layout of the internal array, e.g. "char[]" or "byte[]". */
    public String valueFieldType() {
        return value.getType().getSimpleName();
    }

    /** Makes {@code target} contain the text of {@code replacement} (length may differ). */
    public void replace(String target, String replacement) throws ReflectiveOperationException {
        if (value.getType() == char[].class) {
            value.set(target, replacement.toCharArray());
        } else {
            // Copy the internal representation of the replacement, so the encoding
            // (LATIN1 / UTF16) of the new text is always consistent.
            byte[] bytes = ((byte[]) value.get(replacement)).clone();
            coder.setByte(target, coder.getByte(replacement));
            value.set(target, bytes);
        }
        // The hash code is cached inside the String, so the old one must be dropped.
        if (hash != null) {
            hash.setInt(target, 0);
        }
        if (hashIsZero != null) {
            hashIsZero.setBoolean(target, false);
        }
    }

    private static Field open(String name, boolean required) throws ReflectiveOperationException {
        try {
            Field field = String.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            if (required) {
                throw e;
            }
            return null;
        }
    }
}
