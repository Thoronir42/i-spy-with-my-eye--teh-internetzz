/*
  Copyright (c) 2014, Michal Konkol
  All rights reserved.
 */
package cz.zcu.kiv.nlp.ir.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Konkol
 */
public class AdvancedTokenizer implements Tokenizer {
    private final String regex;

    public AdvancedTokenizer() {
        this(createRegex());
    }

    public AdvancedTokenizer(String regex) {
        this.regex = regex;
    }

    @Override
    public String[] tokenize(String text) {
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<>();

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String token = text.substring(start, end);
            words.add(token);
        }

        String[] ws = new String[words.size()];
        ws = words.toArray(ws);

        return ws;
    }


    private static String createRegex() {
        List<String> parts = new ArrayList<>();

        parts.add("((\\w+\\*+\\w+)|(\\w+\\*+)|(\\*+\\w+))"); // * cenzura
        parts.add("(\\d{1,2}\\.\\d{1,2}\\.(\\d{2,4})?)"); // datum
        parts.add("(\\d{1,3}:\\d{2})"); // time stamp
        parts.add("\\d+[.,](\\d+)"); // cislo
        parts.add("(</?.*?>)"); // html
        parts.add("(https?|ftp):\\/\\/([\\w\\d-]+\\.?)+((\\/)([\\w\\d-]+))*\\/?(\\?[\\w\\d-]+(=[\\w\\d-]+)?)"); // url
        parts.add("(\\([\\w\\s]+)\\)"); // zvukove efekty

        parts.add("([\\p{Punct}])"); // tecky a sracky
        parts.add("([\\p{L}\\d]+)"); // slova

        return String.join("|", parts);
    }

    @Override
    public String toString() {
        return "AdvancedTokenizer{regex='" + regex + "\'}";
    }
}
