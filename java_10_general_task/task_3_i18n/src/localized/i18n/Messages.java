package localized.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Provides the texts of the user interface in the selected language.
 * The texts live in resources/location/messages*.properties.
 */
public class Messages {

    public static final String BUNDLE_NAME = "location.messages";
    public static final List<Locale> SUPPORTED = List.of(
            Locale.ENGLISH, Locale.forLanguageTag("uk"), Locale.GERMAN);

    private static final Logger LOG = Logger.getLogger(Messages.class.getName());

    // Without this control, a request for English on a computer whose own language is
    // Ukrainian would silently fall back to messages_uk instead of the default file.
    private static final ResourceBundle.Control NO_FALLBACK =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private Locale locale;
    private ResourceBundle bundle;

    public Messages(Locale initial) {
        setLocale(initial);
    }

    public final void setLocale(Locale newLocale) {
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, newLocale, NO_FALLBACK);
        locale = newLocale;
        LOG.info("Interface language set to " + newLocale.toLanguageTag());
    }

    public Locale locale() {
        return locale;
    }

    /** Text for the key; {0}, {1}... in the text are replaced by the arguments. */
    public String get(String key, Object... args) {
        try {
            String pattern = bundle.getString(key);
            return args.length == 0 ? pattern : new MessageFormat(pattern, locale).format(args);
        } catch (MissingResourceException e) {
            LOG.warning("Missing text for key '" + key + "' in " + locale.toLanguageTag());
            return "!" + key + "!";
        }
    }

    /** Name of a language written in that language itself, e.g. "Deutsch". */
    public static String languageName(Locale language) {
        String name = language.getDisplayLanguage(language);
        return name.substring(0, 1).toUpperCase(language) + name.substring(1);
    }
}
