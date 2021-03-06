package cz.zcu.sdutends.kiwi.ted.serdes;

import cz.zcu.sdutends.kiwi.ted.model.Talk;
import cz.zcu.sdutends.kiwi.utils.SerDes;
import cz.zcu.sdutends.kiwi.utils.SerDesException;

public class TalkSerDes extends SerDes<Talk> {
    private static final String SEP = "\n\0\n";

    @Override
    public String serialize(Talk talk) {
        String[] parts = {
                talk.getUrl(),
                talk.getTitle(),
                talk.getTalker(),
                talk.getDateRecorded(),
                talk.getIntroduction(),
                talk.getTranscript(),
        };

        return String.join(SEP, parts);
    }

    @Override
    public Talk deserialize(String text) throws SerDesException {
        String[] parts = text.split(SEP);
        try {
            return new Talk()
                    .setUrl(parts[0])
                    .setTitle(parts[1])
                    .setTalker(parts[2])
                    .setDateRecorded(parts[3])
                    .setIntroduction(parts[4])
                    .setTranscript(parts[5]);
        } catch (IndexOutOfBoundsException ex) {
            throw new SerDesException("Failed talk deserialization", ex);
        }

    }
}
