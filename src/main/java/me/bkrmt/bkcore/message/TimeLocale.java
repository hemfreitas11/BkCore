package me.bkrmt.bkcore.message;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;

public class TimeLocale {
    private final BkPlugin plugin;

    public TimeLocale(BkPlugin plugin) {
        this.plugin = plugin;
    }

    public String getYears(boolean capitalize, boolean plural) {
        return capitalize ?
                Utils.capsFirst(cleanInput(plugin.getLangFile().get("info.time.years." + (plural ? "plural" : "single")))) :
                cleanInput(plugin.getLangFile().get("info.time.years." + (plural ? "plural" : "single")));
    }

    public String getAnd() {
        return cleanInput(plugin.getLangFile().get("info.time.and"));
    }

    public String getMonths(boolean capitalize, boolean plural) {
        return capitalize ?
                Utils.capsFirst(cleanInput(plugin.getLangFile().get("info.time.months." + (plural ? "plural" : "single")))) :
                cleanInput(plugin.getLangFile().get("info.time.months." + (plural ? "plural" : "single")));
    }

    public String getWeeks(boolean capitalize, boolean plural) {
        return capitalize ?
                Utils.capsFirst(cleanInput(plugin.getLangFile().get("info.time.weeks." + (plural ? "plural" : "single")))) :
                cleanInput(plugin.getLangFile().get("info.time.weeks." + (plural ? "plural" : "single")));
    }

    public String getDays(boolean capitalize, boolean plural) {
        return capitalize ?
                Utils.capsFirst(cleanInput(plugin.getLangFile().get("info.time.days." + (plural ? "plural" : "single")))) :
                cleanInput(plugin.getLangFile().get("info.time.days." + (plural ? "plural" : "single")));
    }

    public String getHours(boolean capitalize, boolean plural) {
        return capitalize ?
                Utils.capsFirst(cleanInput(plugin.getLangFile().get("info.time.hours." + (plural ? "plural" : "single")))) :
                cleanInput(plugin.getLangFile().get("info.time.hours." + (plural ? "plural" : "single")));
    }

    public String getMinutes(boolean capitalize, boolean plural) {
        return capitalize ?
                Utils.capsFirst(cleanInput(plugin.getLangFile().get("info.time.minutes." + (plural ? "plural" : "single")))) :
                cleanInput(plugin.getLangFile().get("info.time.minutes." + (plural ? "plural" : "single")));
    }

    public String getSeconds(boolean capitalize, boolean plural) {
        return capitalize ?
                Utils.capsFirst(cleanInput(plugin.getLangFile().get("info.time.seconds." + (plural ? "plural" : "single")))) :
                cleanInput(plugin.getLangFile().get("info.time.seconds." + (plural ? "plural" : "single")));
    }

    private String cleanInput(String text) {
        return text.trim()
                .replace(" ", "")
                .replace(",", "")
                .replace(".", "");
    }
}
