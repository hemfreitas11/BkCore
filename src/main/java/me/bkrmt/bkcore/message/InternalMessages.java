package me.bkrmt.bkcore.message;

import me.bkrmt.bkcore.BkPlugin;

public enum InternalMessages {
    VALIDATOR_START("{0} Checking the authorization for your IP address. Please wait, this might take a few seconds...", "{0} Checando a autorizacao do seu endereco IP. Por favor aguarde, isso pode demorar alguns segundos..."),
    PLUGIN_START("{0} Plugin started!", "{0} Plugin iniciado!"),
    VALIDATOR_ERROR("{0} This IP address is not authorized. Please authorize it on: {1}discord.gg/2MHgyjCuPc", "{0} Esse endereco IP nao esta autorizado. Por favor autorize em: {1}discord.gg/2MHgyjCuPc"),
    VALIDATOR_SUCCESS("{0} Your IP has been authorized!", "{0} Seu IP foi autorizado!"),
    VALIDATOR_NO_RESPONSE("{0} The validator has failed! Please report this as soon as possible on: {1}discord.gg/2MHgyjCuPc", "{0} O validador falhou! Por favor reporte isso o mais rapido possivel em: {1}discord.gg/2MHgyjCuPc"),
    NO_ECONOMY("{0} Vault/Economy plugin not found, disabling the plugin...", "{0} Vault/Plugin de economia nao encontrado, desativando o plugin!"),
    NO_CONSOLE_SENDER("{0} You can't use this command from the console!", "{0} Voce nao pode usar esse comando pelo console!"),
    NO_CONFIG("The config file was not found, generating a new one..."),
    NO_LANG("The language \"{0}\" was not found in the \"lang\" folder, using the english file instead (en_US.yml)"),
    UNKNOWN_ERROR("An unexpected error occurred, disabling the plugin..."),
    ESS_COPY_HOME("[{0}] New homes in Essentials detected, importing..."),
    ESS_COPY_WARPS("[{0}] New warps in Essentials detected, importing..."),
    ESS_COPY_DONE("[{0}] Finished importing."),
    MATERIAL_NOT_FOUND("The material {0} was not found... Using dirt instead."),
    INCOMPATIBLE_VERSION("Server versions bellow 1.8 are not supported and will NOT work, you have been warned");

    private final String[] message;

    InternalMessages(String message) {
        this.message = new String[1];
        this.message[0] = message;
    }

    InternalMessages(String enMessage, String brMessage) {
        message = new String[2];
        this.message[0] = enMessage;
        this.message[1] = brMessage;
    }

    public String getMessage() {
        return message[0];
    }

    public String getMessage(BkPlugin plugin) {
        if (plugin.getLangFile().getLanguage().equalsIgnoreCase("pt_br"))  return message[1];
        else return message[0];
    }

}
