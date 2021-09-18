package me.bkrmt.bkcore.clickablemessage;

import me.bkrmt.bkcore.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Button {
    private String text;
    private Hover hover;
    private String command;
    private String placeholder;
    private TextComponent textComponent;

    public Button(String text, Hover hover, String command, String placeholder) {
        this.text = Utils.translateColor(text);
        this.hover = hover;
        this.command = command.contains("/") ? command : "/" + command;
        this.placeholder = placeholder;
    }

    private TextComponent buildComponent() {
        TextComponent component = new TextComponent(getText());
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand()));
        component.setHoverEvent(new HoverEvent(getHover().getAction(), getHover().getValue()));
        return component;
    }

    public TextComponent getTextComponent() {
        return buildComponent();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = Utils.translateColor(text);
    }

    public Hover getHover() {
        return hover;
    }

    public void setHover(Hover hover) {
        this.hover = hover;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command.contains("/") ? command : "/" + command;
    }
}
