package de.rwth.idsg.barti.server;

import de.intarsys.security.smartcard.card.CardSystemMonitor;
import de.intarsys.security.smartcard.card.EnumCardState;
import de.intarsys.security.smartcard.card.ICard;
import de.intarsys.security.smartcard.card.ICardTerminal;
import de.intarsys.security.smartcard.card.standard.StandardCardSystem;
import de.intarsys.security.smartcard.pcsc.PCSCContextFactory;
import de.intarsys.security.smartcard.smartcardio.SmartcardioProvider;
import lombok.extern.log4j.Log4j2;

import java.security.Security;
import java.util.concurrent.TimeUnit;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class Test {
    public static void main(final String... args) throws InterruptedException {
        Security.insertProviderAt(new SmartcardioProvider(), 1);
        final StandardCardSystem cardSystem = new StandardCardSystem(PCSCContextFactory.get());
        final CardSystemMonitor cardSystemMonitor = new CardSystemMonitor(cardSystem);
        cardSystemMonitor.addCardSystemListener(new CardSystemMonitor.ICardSystemListener() {
            @Override
            public void onCardChanged(final ICard card) {
                final EnumCardState state = card.getState();
                if (state.isNotConnected()) {
                    log.error("Card is not connected (any more)!");
                }
                // card state INVALID only via ICard#dispose()
                if (!state.isInvalid()) {
                    return;
                }
                // can we do anything about it?
            }

            @Override
            public void onCardInserted(final ICard card) {
                log.error("Card inserted: {} [{}]", card, card.getCardTerminal().getName());
            }

            @Override
            public void onCardRemoved(final ICard card) {
                log.error("Card removed: {} [{}]", card, card.getCardTerminal().getName());
            }

            @Override
            public void onCardTerminalConnected(final ICardTerminal terminal) {
                log.error("CardTerminal connected: {}", terminal.getName());
            }

            @Override
            public void onCardTerminalDisconnected(final ICardTerminal terminal) {
                log.error("CardTerminal disconnected: {}", terminal.getName());
            }
        });
        cardSystemMonitor.start();
        TimeUnit.DAYS.sleep(1);
    }
}
