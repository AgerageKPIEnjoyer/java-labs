package controller;

import model.Translator;
import view.TranslatorView;

/**
 * Coordinates the Translator model with the TranslatorView: reads menu
 * choices from the view, operates on the dictionary/translation logic,
 * and hands results back to the view to display.
 */
public class TranslatorController {

    private final TranslatorView view;
    private final Translator translator;

    public TranslatorController(TranslatorView view, Translator translator) {
        this.view = view;
        this.translator = translator;
    }

    public void run() {
        boolean running = true;
        while (running) {
            view.printMenu();
            String choice = view.readMenuChoice();
            switch (choice) {
                case "1":
                    addWordPair();
                    break;
                case "2":
                    translatePhrase();
                    break;
                case "3":
                    view.displayDictionary(translator.getDictionary());
                    break;
                case "0":
                    running = false;
                    view.displayMessage("Goodbye!");
                    break;
                default:
                    view.displayMessage("Unknown option.\n");
            }
        }
        view.close();
    }

    private void addWordPair() {
        String[] pair = view.promptWordPair();
        String english = pair[0];
        String ukrainian = pair[1];

        if (english.isEmpty() || ukrainian.isEmpty()) {
            view.displayMessage("Both words are required - nothing was added.\n");
            return;
        }

        boolean replacing = translator.containsWord(english);
        translator.addWord(english, ukrainian);
        view.displayMessage((replacing ? "Updated: " : "Added: ") + english + " -> " + ukrainian + "\n");
    }

    private void translatePhrase() {
        String phrase = view.promptPhrase();
        String translated = translator.translate(phrase);
        view.displayTranslation(phrase, translated);
    }
}
