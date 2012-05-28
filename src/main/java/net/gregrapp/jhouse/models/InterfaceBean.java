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
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

/**
 * Interface bean data model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "interface_beans")
public class InterfaceBean
{

  private Map<String, String> properties;
  private boolean enabled;
  private Long id;
  private String klass;

  /**
   * Add property
   * 
   * @param key
   *          property name
   * @param value
   *          property value
   */
  public void addProperty(String key, String value)
  {
    this.properties.put(key, value);
  }

  /**
   * Get Map of config options for the bean
   * 
   * @return the config
   */
  @ElementCollection
  @CollectionTable(name="interface_properties")
  @MapKeyColumn(name="property")
  @Column(name="value")
  public Map<String, String> getProperties()
  {
    return properties;
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
   * The fully qualified class name for this transport
   * 
   * @return the class
   */
  @Column(nullable = false)
  public String getKlass()
  {
    return klass;
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
   * Set Map of properties for the bean
   * 
   * @param properties
   *          the properties to set
   */
  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
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
   * The fully qualified class name for this interface
   * 
   * @param klass
   *          the class name to set
   */
  public void setKlass(String klass)
  {
    this.klass = klass;
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
    builder.append("InterfaceBean [enabled=");
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
