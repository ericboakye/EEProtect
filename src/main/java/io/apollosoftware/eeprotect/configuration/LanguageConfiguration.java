package io.apollosoftware.eeprotect.configuration;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.lib.configuration.Configuration;
import io.apollosoftware.lib.lang.Message;

public class LanguageConfiguration extends Configuration<EEProtect> {

    public LanguageConfiguration() {
        super("language.yml");
    }

    public void afterLoad() {
        Message.load(conf);
    }

    public void onSave() {

    }
}
