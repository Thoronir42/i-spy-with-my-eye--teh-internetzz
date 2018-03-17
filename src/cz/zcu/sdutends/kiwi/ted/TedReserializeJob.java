package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.utils.AdvancedIO;

import java.util.List;

public class TedReserializeJob extends IrJob {

    @Override
    public boolean execute() {
        String from = "./storage/ted/talks";
        String to = "./storage/ted/talks-plain";

        this.ensureDirectoriesExist(to);

        AdvancedIO<Talk> taio = new AdvancedIO<>(new TalkSedes());

        List<Talk> talks = taio.loadFromDirectory(from);

        for (Talk talk : talks) {
            if (!taio.save(to + "/" + talk.getUrl() + ".txt", talk)) {
                System.err.println("Failed writing " + talk.getUrl());
            }
        }

        return true;
    }
}
