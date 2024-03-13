import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;

public class Ticket12 {
    public static void main(String[] args) throws Exception {
        // Token del bot Discord
        String token = "INSERISCI_IL_TOKEN_DEL_TUO_BOT";

        // Crea un'istanza del JDABuilder
        JDABuilder builder = JDABuilder.createDefault(token);

        // Aggiungi un event listener
        builder.addEventListeners(new TicketListener());

        // Avvia il bot
        builder.build();
    }
}

class TicketListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ottieni il messaggio
        Message message = event.getMessage();
        
        // Ottieni il contenuto del messaggio
        String content = message.getContentRaw();
        
        // Ottieni il canale in cui è stato inviato il messaggio
        MessageChannel channel = event.getChannel();
        
        // Se il messaggio inizia con "!newticket"
        if (content.startsWith("!newticket")) {
            // Crea un nuovo canale testuale per il ticket
            event.getGuild().createTextChannel("ticket-" + event.getAuthor().getName())
                .queue(ticketChannel -> {
                    // Imposta le autorizzazioni del canale
                    ticketChannel.createPermissionOverride(event.getGuild().getPublicRole())
                        .setDeny(Permission.VIEW_CHANNEL)
                        .queue();
                    ticketChannel.createPermissionOverride(event.getMember())
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
                    
                    // Invia un messaggio di conferma nel nuovo canale ticket
                    ticketChannel.sendMessage(event.getAuthor().getAsMention() + " Ticket creato. Un membro del team lo prenderà in carico al più presto.").queue();
                });
        }
        
        // Se il messaggio inizia con "!closeticket"
        else if (content.startsWith("!closeticket")) {
            // Controlla se il messaggio è stato inviato in un canale ticket
            if (channel.getName().startsWith("ticket-")) {
                // Elimina il canale ticket
                channel.delete().queue();
            } else {
                // Messaggio di errore se il comando viene eseguito in un canale non-ticket
                channel.sendMessage("Questo comando può essere eseguito solo nei canali ticket.").queue();
            }
        }
    }
}
