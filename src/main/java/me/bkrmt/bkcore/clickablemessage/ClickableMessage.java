package me.bkrmt.bkcore.clickablemessage;

import me.bkrmt.bkcore.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ClickableMessage {
    private final TextComponent messageComponent;
    private final List<Player> messageReceivers;
    private final List<Button> buttons;
    private final List<String> messageLines;

    public ClickableMessage(List<String> messageLines) {
        this.messageLines = messageLines;
        messageComponent = new TextComponent("");
        messageReceivers = new ArrayList<>();
        buttons = new ArrayList<>();
    }

    public ClickableMessage addButton(Button button) {
        buttons.add(button);
        return this;
    }

    public TextComponent getMessageComponent() {
        return messageComponent;
    }

    public List<Player> getMessageReceivers() {
        return messageReceivers;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public List<String> getMessageLines() {
        return messageLines;
    }

    public ClickableMessage addReceiver(Player player) {
        messageReceivers.add(player);
        return this;
    }

    public ClickableMessage addReceivers(Player[] players) {
        Collections.addAll(messageReceivers, players);
        return this;
    }

    public ClickableMessage sendMessage() {
        messageReceivers.forEach(player -> player.spigot().sendMessage(messageComponent));
        return this;
    }

    public ClickableMessage buildMessage() {
        Iterator<String> iterator = messageLines.listIterator();
        while (iterator.hasNext()) {
            String line = Utils.fixLineColor(Utils.translateColor(iterator.next()));
            List<Button> containingButtons = getContainingButtons(line);
            if (containingButtons.size() > 0) {
                String[] parts = line.split(" ");
                StringBuilder builder = new StringBuilder();
                for (int c = 0; c < parts.length; c++) {
                    boolean addedComponent = false;
                    for (Button containingButton : containingButtons) {
                        if (parts[c].contains(containingButton.getPlaceholder())) {
                            appendComponent(containingButton.getTextComponent(), builder);
                            builder = new StringBuilder();
                            addedComponent = true;
                        }
                    }
                    if (!addedComponent) builder.append(Utils.translateColor(parts[c] + " "));
                    if (c == parts.length - 1) {
                        messageComponent.addExtra(builder.toString());
                        messageComponent.addExtra(" ");
                    }
                }
            } else {
                messageComponent.addExtra(line);
            }
            if (iterator.hasNext()) messageComponent.addExtra("\n");
        }
        return this;
    }
    private List<Button> getContainingButtons(String text) {
       return buttons.stream().filter(button -> text.contains(button.getPlaceholder())).collect(Collectors.toList());
    }

    private void appendComponent(TextComponent button, StringBuilder builder) {
        messageComponent.addExtra(builder.toString());
        messageComponent.addExtra(button);
        messageComponent.addExtra(" ");
    }
}
