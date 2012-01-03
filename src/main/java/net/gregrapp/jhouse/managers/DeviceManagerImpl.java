/**
 * 
 */
package net.gregrapp.jhouse.managers;

import net.gregrapp.jhouse.device.classes.DeviceClass;
import net.gregrapp.jhouse.device.types.Device;

/**
 * @author Greg Rapp
 *
 */
public class DeviceManagerImpl
{
  @Autowired
  Device[] devices;

  /**
   * 
   */
  public DeviceManagerImpl()
  {
    
  }  

  public String[] getDeviceClassesForDevice(Device device)
  {
    List<String> klasses = new ArrayList<String>();
    
    for (Class<?> iface : device.getClass().getInterfaces())
    {
      if (iface.isInstance(DeviceClass))
      {
        klasses.add(iface.getName());
      }
    }
   return klasses.toArray(new String[0]); 
  }
}
