package me.bkrmt.bkcore.message;

public enum InternalMessages {
    NOCONFIG("The config file was not found, generating a new one..."),
    NOLANG("The language \"{0}\" was not found in the \"lang\" folder, using the english file instead (en_US.yml)"),
    UNKNOWNERROR("An unnexpected error occured, disabling the plugin..."),
    ESSCOPYHOME("[{0}] New homes in Essentials detected, importing..."),
    ESSCOPYWARPS("[{0}] New warps in Essentials detected, importing..."),
    ESSCOPYDONE("[{0}] Finished importing."),
    MATERIALNOTFOUND("The material {0} was not found... Using dirt instead."),
    INCOMPATIBLEVERSION("Server versions bellow 1.8 are not supported and will NOT work, you have been warned");

    private final String message;

    InternalMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
