/**
 * 
 */
package net.gregrapp.jhouse.services.event;

import org.drools.definition.rule.Rule;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Listen for Drools events
 * 
 * @author Greg Rapp
 * 
 */
public class DroolsEventListener extends DefaultAgendaEventListener
{
  private static final XLogger logger = XLoggerFactory
      .getXLogger(DroolsEventListener.class);

  @Override
  public void beforeActivationFired(final BeforeActivationFiredEvent event)
  {
    logger.entry(event);

    final Rule rule = event.getActivation().getRule();
    logger.debug("Firing rule [{}]", rule.getName());

    logger.exit();
  }
}
