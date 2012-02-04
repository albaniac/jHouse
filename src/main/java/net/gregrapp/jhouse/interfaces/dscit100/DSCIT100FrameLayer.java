/**
 * 
 */
package net.gregrapp.jhouse.interfaces.dscit100;

/**
 * @author Greg Rapp
 * 
 */
public interface DSCIT100FrameLayer
{
  void destroy();

  void setCallbackHandler(DSCIT100FrameLayerAsyncCallback handler);

  void write(DSCIT100DataFrame frame) throws DSCIT100FrameLayerException;
}
