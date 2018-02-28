package cz.zcu.kiv.nlp.ir.preprocessing;

import java.text.Normalizer;

public class AccentStripper implements PreProcessingOperation<String> {

    private static final String WITH_ACCENTS = "áÁčČďĎéÉěĚíÍňŇóÓřŘšŠťŤúÚůŮýÝžŽ";
    private static final String NONE_ACCENTS = "aAcCdDeEeEiInNoOrRsStTuUuUyYzZ";

    private final Mode mode;

    public AccentStripper(Mode mode) {

        this.mode = mode;
    }


    @Override
    public String apply(String value) {
        if (mode == Mode.Advanced) {
            return removeAdvanced(value);
        }
        return removeNaive(value);
    }


    private String removeNaive(String text) {
        for (int i = 0; i < WITH_ACCENTS.length(); i++) {
            text = text.replaceAll("" + WITH_ACCENTS.charAt(i), "" + NONE_ACCENTS.charAt(i));
        }
        return text;
    }

    public static String removeAdvanced(String text) {
        if (text == null) {
            return null;
        }

        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }


    public enum Mode {
        Naive, Advanced
    }
}
