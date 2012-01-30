/**
 * 
 */
package net.gregrapp.jhouse.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Config data model
 * 
 * @author Greg Rapp
 *
 */
@Entity
@Table(name = "CONFIG")
public class Config
{

  @Id
  @GeneratedValue
  private Long id;
  
  @Column(name = "namespace", nullable=false)
  private String namespace;
  
  @Column(name = "opt", nullable=false)
  private String opt;
  
  @Column(name = "val")
  private String val;
  
  /**
   * Unique ID
   * 
   * @return unique ID
   */
  public Long getId() {
      return id;
  }

  /**
   * The namespace for the associated key
   * 
   * @return the namespace
   */
  public String getNamespace()
  {
    return namespace;
  }

  /**
   * The namespace for the associated key
   * 
   * @param namespace the namespace to set
   */
  public void setNamespace(String namespace)
  {
    this.namespace = namespace;
  }

  /**
   * The configuration key
   * 
   * @return the key
   */
  public String getOpt()
  {
    return opt;
  }

  /**
   * The configuration key
   * 
   * @param key the key to set
   */
  public void setOpt(String opt)
  {
    this.opt = opt;
  }

  /**
   * The configuration value
   * 
   * @return the value
   */
  public String getVal()
  {
    return val;
  }

  /**
   * The configuration value
   * 
   * @param value the value to set
   */
  public void setVal(String val)
  {
    this.val = val;
  }
}
