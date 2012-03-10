/**
 * 
 */
package net.gregrapp.jhouse.models;

import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

/**
 * Driver bean data model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "DRIVER_BEANS")
public class DriverBean
{

  private InterfaceBean driverInterface;
  private boolean enabled;
  private Long id;
  private String klass;
  private Map<String, String> properties;
  
  /**
   * Add property
   * 
   * @param key property name
   * @param value property value
   */
  public void addProperty(String key, String value)
  {
    this.properties.put(key, value);
  }

  /**
   * Get the driver interface utilized by this bean
   * 
   * @return the driver interface 
   */
  @ManyToOne
  public InterfaceBean getDriverInterface()
  {
    return driverInterface;
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
   * The fully qualified class name for this driver interface
   * 
   * @return the class
   */
  @Column(nullable = false)
  public String getKlass()
  {
    return klass;
  }

  /**
   * Properties of driver bean
   * 
   * @return the properties
   */
  @ElementCollection
  @CollectionTable(name="driver_bean_properties")
  @MapKeyColumn(name="property")
  @Column(name="value")
  public Map<String, String> getProperties()
  {
    return properties;
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
   * Set the driver interface utilized by this bean
   * 
   * @param driverInterface the driver interface to set
   */
  public void setDriverInterface(InterfaceBean driverInterface)
  {
    this.driverInterface = driverInterface;
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
   * The fully qualified class name for this driver interface
   * 
   * @param klass
   *          the class name to set
   */
  public void setKlass(String klass)
  {
    this.klass = klass;
  }

  /**
   * Properties to be set on driver bean
   * 
   * @param properties the properties to set
   */
  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DriverBean [driverInterface=");
    builder.append(driverInterface);
    builder.append(", enabled=");
    builder.append(enabled);
    builder.append(", id=");
    builder.append(id);
    builder.append(", klass=");
    builder.append(klass);
    builder.append(", properties=");
    builder.append(properties);
    builder.append("]");
    return builder.toString();
  }
}
