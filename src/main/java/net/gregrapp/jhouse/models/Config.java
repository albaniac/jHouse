/**
 * 
 */
package net.gregrapp.jhouse.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Config data model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "CONFIG",
    uniqueConstraints = { @UniqueConstraint(columnNames = { "namespace", "key" }) })
public class Config
{

  private Long id;
  private String namespace;
  private String key;
  private String value;

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
   * The namespace for the associated key
   * 
   * @return the namespace
   */
  @Column(nullable = false)
  public String getNamespace()
  {
    return namespace;
  }

  /**
   * The configuration key
   * 
   * @return the key
   */
  @Column(nullable = false)
  public String getKey()
  {
    return key;
  }

  /**
   * The configuration value
   * 
   * @return the value
   */
  @Column
  public String getValue()
  {
    return value;
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
   * The namespace for the associated key
   * 
   * @param namespace
   *          the namespace to set
   */
  public void setNamespace(String namespace)
  {
    this.namespace = namespace;
  }

  /**
   * The configuration key
   * 
   * @param key
   *          the key to set
   */
  public void setKey(String key)
  {
    this.key = key;
  }

  /**
   * The configuration value
   * 
   * @param value
   *          the value to set
   */
  public void setValue(String value)
  {
    this.value = value;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("Config [id=");
    builder.append(id);
    builder.append(", namespace=");
    builder.append(namespace);
    builder.append(", key=");
    builder.append(key);
    builder.append(", value=");
    builder.append(value);
    builder.append("]");
    return builder.toString();
  }
}
