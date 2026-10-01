import controller.TranslatorController;
import model.Translator;
import view.TranslatorView;

public class Main {
    public static void main(String[] args) {
        TranslatorView view = new TranslatorView();
        Translator translator = new Translator(); // dictionary pre-populated in its constructor
        TranslatorController controller = new TranslatorController(view, translator);
        controller.run();
    }
}
