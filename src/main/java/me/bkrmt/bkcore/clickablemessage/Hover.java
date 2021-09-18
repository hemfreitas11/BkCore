package me.bkrmt.bkcore.clickablemessage;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;

public class Hover {
    private HoverEvent.Action action;
    private BaseComponent[] value;

    public Hover(HoverEvent.Action action, BaseComponent[] value) {
        this.action = action;
        this.value = value;
    }

    public HoverEvent.Action getAction() {
        return action;
    }

    public void setAction(HoverEvent.Action action) {
        this.action = action;
    }

    public BaseComponent[] getValue() {
        return value;
    }

    public void setValue(BaseComponent[] value) {
        this.value = value;
    }
}
