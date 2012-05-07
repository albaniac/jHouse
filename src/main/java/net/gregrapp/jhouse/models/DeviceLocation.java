/**
 * 
 */
package net.gregrapp.jhouse.models;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Device location model
 * 
 * @author Greg Rapp
 * 
 */

@Entity
@Table(name = "device_locations")
public class DeviceLocation
{
  
  private Set<DeviceBean> devices;
  private String floor;
  private Long id;
  private String room;

  /**
   * Devices in this location
   * 
   * @return the devices
   */
  @OneToMany(mappedBy = "location")
  public Set<DeviceBean> getDevices()
  {
    return devices;
  }

  /**
   * Location floor
   * 
   * @return the floor
   */
  @Column
  public String getFloor()
  {
    return floor;
  }

  /**
   * @return the id
   */
  @Id
  @GeneratedValue
  public Long getId()
  {
    return id;
  }

  /**
   * Location room
   * 
   * @return the room
   */
  @Column
  public String getRoom()
  {
    return room;
  }

  /**
   * @param devices
   *          the devices to set
   */
  public void setDevices(Set<DeviceBean> devices)
  {
    this.devices = devices;
  }

  /**
   * @param floor
   *          the floor to set
   */
  public void setFloor(String floor)
  {
    this.floor = floor;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @param room
   *          the room to set
   */
  public void setRoom(String room)
  {
    this.room = room;
  }
}
