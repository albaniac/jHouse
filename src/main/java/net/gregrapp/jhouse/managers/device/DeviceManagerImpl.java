/**
 * 
 */
package net.gregrapp.jhouse.managers.device;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import net.gregrapp.jhouse.device.classes.DeviceClass;
import net.gregrapp.jhouse.device.types.Device;

/**
 * @author Greg Rapp
 *
 */
public class DeviceManagerImpl implements DeviceManager
{
  @Autowired
  Device[] devices;

  /**
   * 
   */
  public DeviceManagerImpl()
  {
    
  }  

  public Device getDeviceForId(int deviceId)
  {
    for (Device device : devices)
    {
      if (device.getDeviceId() == deviceId)
      {
        return device;
      }
    }
    return null;
  }
  
  public String[] getDeviceClassesForDevice(Device device)
  {
    List<String> klasses = new ArrayList<String>();
    
    for (Class<?> iface : device.getClass().getInterfaces())
    {
      if (DeviceClass.class.isAssignableFrom(iface))
      {
        klasses.add(iface.getSimpleName());
      }
    }
   return klasses.toArray(new String[0]); 
  }
}
