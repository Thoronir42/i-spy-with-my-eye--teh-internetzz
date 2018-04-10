package cz.zcu.sdutends.kiwi.ted.serdes;

import cz.zcu.sdutends.kiwi.ted.model.TalkStructured;
import cz.zcu.sdutends.kiwi.ted.model.TranscriptBlock;
import cz.zcu.sdutends.kiwi.utils.SerDes;
import cz.zcu.sdutends.kiwi.utils.SerDesException;
import org.apache.log4j.Logger;

import java.time.LocalDate;

public class TalkStructuredSerDes extends SerDes<TalkStructured> {
    private static final Logger log = Logger.getLogger(TalkStructuredSerDes.class);

    private static final String SEP = "\n\0\n";

    private static final String[] months = {
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"
    };

    @Override
    public String serialize(TalkStructured talk) {
        String[] parts = {
                talk.getUrl(),
                talk.getTitle(),
                talk.getTalker(),
                joinDate(talk.getDateRecorded()),
                talk.getIntroduction(),
                joinTranscript(talk.getTranscript()),
        };

        return String.join(SEP, parts);
    }

    @Override
    public TalkStructured deserialize(String text) throws SerDesException {
        String[] parts = text.split(SEP);
        if (parts.length < 5) {
            throw new SerDesException("Invalid part count");
        }

        return new TalkStructured()
                .setUrl(parts[0])
                .setTitle(parts[1])
                .setTalker(parts[2])
                .setDateRecorded(parseDate(parts[3]))
                .setIntroduction(parts[4])
                .setTranscript(splitTranscript(parts[5]));


    }

    private String joinTranscript(TranscriptBlock[] blocks) {
        String[] parts = new String[blocks.length];

        for (int i = 0; i < blocks.length; i++) {
            int time = blocks[i].getTime();
            parts[i] = String.format("%02d:%02d %s", time / 60, time % 60, blocks[i].getText());
        }

        return String.join("\n", parts);
    }

    private TranscriptBlock[] splitTranscript(String transcript) throws SerDesException {
        String[] parts = transcript.split("\n");
        TranscriptBlock[] blocks = new TranscriptBlock[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                int time = 0;
                if ("0:00".equals(parts[i].substring(0, 4))) {
//                    log.warn("Transcript contains 0:00 timestamp");
                } else {
                    String minutes = parts[i].substring(0, 2);
                    String seconds = parts[i].substring(3, 5);

                    time = Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);
                }

//            System.out.println(minutes + ":" + seconds + " ~ " + time);

                blocks[i] = new TranscriptBlock(time, parts[i].substring(6));
            }
        } catch (NumberFormatException ex) {
            throw new SerDesException(ex);
        }

        return blocks;
    }

    private String joinDate(LocalDate date) {
        return months[date.getMonthValue() - 1] + " " + date.getYear();
    }

    private LocalDate parseDate(String dateStr) throws SerDesException {
        dateStr = dateStr.trim();
        String[] parts = dateStr.split(" ");

        int monthIndex = -1;
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(parts[0])) {
                monthIndex = i + 1;
                break;
            }
        }
        if (monthIndex == -1) {
            throw new SerDesException("Invalid date string: '" + dateStr + "'");
        }

        String year = parts[1].substring(0, 4);
        try {
            return LocalDate.of(Integer.parseInt(year), monthIndex, 1);
        } catch (NumberFormatException ex) {
            throw new SerDesException("Invalid year string: '" + year + "'");
        }
    }
}
