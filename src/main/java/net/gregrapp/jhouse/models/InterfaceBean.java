/**
 * 
 */
package net.gregrapp.jhouse.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Interface bean data model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "INTERFACE_BEANS")
public class InterfaceBean
{

  private boolean enabled;
  private Long id;
  private String klass;
  private TransportBean transport;
  
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
   * Get the transport utilized by this bean
   * 
   * @return the transport 
   */
  @OneToOne
  public TransportBean getTransport()
  {
    return transport;
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

  /**
   * Set the transport utilized by this bean
   * 
   * @param transport the transport to set
   */
  public void setTransport(TransportBean transport)
  {
    this.transport = transport;
  }

  /* (non-Javadoc)
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
    builder.append(", transport=");
    builder.append(transport);
    builder.append("]");
    return builder.toString();
  }
}
