/**
 * 
 */
package net.gregrapp.jhouse.models;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 * Transport bean data model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "TRANSPORT_BEANS")
public class TransportBean
{

  private List<String> config;
  private boolean enabled;
  private Long id;
  private String klass;
  
  /**
   * Add configuration option
   * 
   * @param config
   */
  public void addConfig(String config)
  {
    this.config.add(config);
  }

  /**
   * The configuration options for this transport
   * 
   * @return the key
   */
  @ElementCollection
  @CollectionTable(name="transport_bean_config")
  @OrderColumn
  @Column
  public List<String> getConfig()
  {
    return config;
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
   * The configuration options for this transport
   * 
   * @param key
   *          the key to set
   */
  public void setConfig(List<String> config)
  {
    this.config = config;
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
   * The fully qualified class name for this transport
   * 
   * @param klass
   *          the class name to set
   */
  public void setKlass(String klass)
  {
    this.klass = klass;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("TransportBean [config=");
    builder.append(config);
    builder.append(", enabled=");
    builder.append(enabled);
    builder.append(", id=");
    builder.append(id);
    builder.append(", klass=");
    builder.append(klass);
    builder.append("]");
    return builder.toString();
  }


}
