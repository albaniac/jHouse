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
  void write(DSCIT100DataFrame frame) throws DSCIT100FrameLayerException;
  void setCallbackHandler(DSCIT100FrameLayerAsyncCallback handler);
}
