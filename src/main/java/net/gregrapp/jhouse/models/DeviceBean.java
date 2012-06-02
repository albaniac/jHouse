/**
 * 
 */
package net.gregrapp.jhouse.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Device bean data model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "device_beans", uniqueConstraints = { @UniqueConstraint(columnNames = {
    "driver_id", "driverIndex" }) })
public class DeviceBean
{

  private DriverBean driver;
  private int driverIndex;
  private boolean enabled;
  private Long id;
  private String klass;
  private DeviceLocation location;
  private String name;

  /**
   * Get the driver utilized by this bean
   * 
   * @return the driver interface
   */
  @ManyToOne
  public DriverBean getDriver()
  {
    return driver;
  }

  /**
   * Driver index for this device
   * 
   * @return the index
   */
  @Column(nullable = false)
  public int getDriverIndex()
  {
    return driverIndex;
  }

  /**
   * Unique ID
   * 
   * @return unique ID
   */
  @Id
  @GeneratedValue
  public Long getId()
  {
    return id;
  }

  /**
   * The fully qualified class name for this device
   * 
   * @return the class
   */
  @Column(nullable = false)
  public String getKlass()
  {
    return klass;
  }

  /**
   * @return the location
   */
  @ManyToOne
  @JoinColumn(name = "location_id")
  public DeviceLocation getLocation()
  {
    return location;
  }

  /**
   * Get device name
   * 
   * @return the name
   */
  @Column
  public String getName()
  {
    return name;
  }

  /**
   * @return enabled
   */
  @Column
  public boolean isEnabled()
  {
    return enabled;
  }

  /**
   * @param driver
   *          the driver to set
   */
  public void setDriver(DriverBean driver)
  {
    this.driver = driver;
  }

  /**
   * Driver index for this device
   * 
   * @param index
   *          the index to set
   */
  public void setDriverIndex(int driverIndex)
  {
    this.driverIndex = driverIndex;
  }

  /**
   * @param enabled
   */
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }

  /**
   * Unique ID
   * 
   * @param id
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * Set the driver utilized by this bean
   * 
   * @param driverInterface
   *          the driver to set
   */
  /*
  public void setInterface(DriverBean driver)
  {
    this.driver = driver;
  }
*/
  /**
   * The fully qualified class name for this device
   * 
   * @param klass
   *          the class name to set
   */
  public void setKlass(String klass)
  {
    this.klass = klass;
  }

  /**
   * @param location
   *          the location to set
   */
  public void setLocation(DeviceLocation location)
  {
    this.location = location;
  }

  /**
   * Set device name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DeviceBean [id=");
    builder.append(id);
    builder.append(", name=");
    builder.append(name);
    builder.append(", driver=");
    builder.append(driver);
    builder.append(", enabled=");
    builder.append(enabled);
    builder.append(", index=");
    builder.append(driverIndex);
    builder.append(", klass=");
    builder.append(klass);
    builder.append(", location=");
    builder.append(location);
    builder.append("]");
    return builder.toString();
  }
}
