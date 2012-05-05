/**
 * 
 */
package net.gregrapp.jhouse.interfaces.envisalink2ds;

/**
 * @author Greg Rapp
 * 
 */
public interface Envisalink2DSFrameLayer
{
  void destroy();

  void setCallbackHandler(Envisalink2DSFrameLayerAsyncCallback handler);

  void write(Envisalink2DSDataFrame frame)
      throws Envisalink2DSFrameLayerException;
}
