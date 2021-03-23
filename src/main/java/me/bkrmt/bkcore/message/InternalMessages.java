package me.bkrmt.bkcore.message;

public enum InternalMessages {
    VALIDATOR_START_EN("{0} Checking the authorization for your IP adress. Please wait..."),
    VALIDATOR_START_BR("{0} Checando a autorizacao do seu endereco IP. Por favor aguarde..."),
    PLUGIN_START_EN("{0} Plugin started!"),
    PLUGIN_START_BR("{0} Plugin iniciado!"),
    VALIDATOR_ERROR_EN("{0} This IP adress is not authorized. Please authorize it on: {1}discord.gg/2MHgyjCuPc"),
    VALIDATOR_ERROR_BR("{0} Esse endereco IP nao esta autorizado. Por favor autorize em: {1}discord.gg/2MHgyjCuPc"),
    VALIDATOR_SUCCESS_EN("{0} Your IP has been authorized!"),
    VALIDATOR_SUCCESS_BR("{0} Seu IP foi autorizado!"),
    VALIDATOR_NO_RESPONSE_EN("{0} The validator has failed! Please report this as soon as possible on: {1}discord.gg/2MHgyjCuPc"),
    VALIDATOR_NO_RESPONSE_BR("{0} O validador falhou! Por favor reporte isso o mais rapido possivel em: {1}discord.gg/2MHgyjCuPc"),
    NOCONFIG("The config file was not found, generating a new one..."),
    NOLANG("The language \"{0}\" was not found in the \"lang\" folder, using the english file instead (en_US.yml)"),
    UNKNOWNERROR("An unnexpected error occured, disabling the plugin..."),
    ESSCOPYHOME("[{0}] New homes in Essentials detected, importing..."),
    ESSCOPYWARPS("[{0}] New warps in Essentials detected, importing..."),
    ESSCOPYDONE("[{0}] Finished importing."),
    MATERIAL_NOT_FOUND("The material {0} was not found... Using dirt instead."),
    ERRORX9("The plugin encountered an error! Code: 2Q5f139z Join discord.gg/2MHgyjCuPc and report this as soon as possible"),
    INCOMPATIBLE_VERSION("Server versions bellow 1.8 are not supported and will NOT work, you have been warned");

    private final String message;

    InternalMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
