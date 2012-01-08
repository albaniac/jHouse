/**
 * 
 */
package net.gregrapp.jhouse.interfaces;

import java.util.HashMap;

/**
 * @author Greg Rapp
 *
 */
public interface NodeInterface
{
  /**
   * @return node information, key is the node address and value contains node attributes
   */
  public HashMap<String, HashMap<String,String>> getNodes();
}
