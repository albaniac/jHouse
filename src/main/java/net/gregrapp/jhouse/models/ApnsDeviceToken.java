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

/**
 * Apple Push Notification service device tokens
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "apns_device_tokens")
public class ApnsDeviceToken
{
  private String description;
  private boolean enabled;
  private Long id;
  private String token;
  private User user;

  /**
   * Token description
   * 
   * @return the description
   */
  @Column
  public String getDescription()
  {
    return description;
  }

  /**
   * Unique ID
   * 
   * @return the id
   */
  @Id
  @GeneratedValue
  public Long getId()
  {
    return id;
  }

  /**
   * APNs device token
   * 
   * @return the token
   */
  @Column(nullable = false)
  public String getToken()
  {
    return token;
  }

  /**
   * User that owns this token
   * 
   * @return the user
   */
  @ManyToOne
  @JoinColumn(name = "user_id")
  public User getUser()
  {
    return user;
  }

  /**
   * Token enabled status
   * 
   * @return the enabled
   */
  @Column
  public boolean isEnabled()
  {
    return enabled;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * @param enabled
   *          the enabled to set
   */
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
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
   * @param token
   *          the token to set
   */
  public void setToken(String token)
  {
    this.token = token;
  }

  /**
   * @param user
   *          the user to set
   */
  public void setUser(User user)
  {
    this.user = user;
  }

}
