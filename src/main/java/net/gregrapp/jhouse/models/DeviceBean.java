/**
 * 
 */
package net.gregrapp.jhouse.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "device_beans", 
       uniqueConstraints = { @UniqueConstraint(columnNames = { "driver_id", "valueIndex" }) })
public class DeviceBean
{

  private DriverBean driver;
  private boolean enabled;
  private Long id;
  private int valueIndex;
  private String klass;
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
   * Driver value index for this device
   * 
   * @return the index
   */
  public int getValueIndex()
  {
    return valueIndex;
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
   * Get device name
   * 
   * @return the name
   */
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
   * @param driver the driver to set
   */
  public void setDriver(DriverBean driver)
  {
    this.driver = driver;
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
   * Driver value index for this device
   * 
   * @param index the index to set
   */
  public void setValueIndex(int valueIndex)
  {
    this.valueIndex = valueIndex;
  }

  /**
   * Set the driver utilized by this bean
   * 
   * @param driverInterface the driver to set
   */
  public void setInterface(DriverBean driver)
  {
    this.driver = driver;
  }

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
   * Set device name
   * 
   * @param name the name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DeviceBean [driver=");
    builder.append(driver);
    builder.append(", enabled=");
    builder.append(enabled);
    builder.append(", id=");
    builder.append(id);
    builder.append(", klass=");
    builder.append(klass);
    builder.append(", name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }
}
