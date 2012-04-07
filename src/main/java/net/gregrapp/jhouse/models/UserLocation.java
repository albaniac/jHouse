/**
 * 
 */
package net.gregrapp.jhouse.models;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * User role model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "user_locations")
public class UserLocation
{

  private double altitude;
  private double course;
  private double horizontalAccuracy;
  private Long id;
  private double latitude;
  private double longitude;
  private double speed;
  private Calendar timestamp;
  private User user;
  private double verticalAccuracy;

  /**
   * @return the altitude
   */
  @Column
  public double getAltitude()
  {
    return altitude;
  }

  /**
   * @return the course
   */
  @Column
  public double getCourse()
  {
    return course;
  }

  /**
   * @return the horizontalAccuracy
   */
  @Column
  public double getHorizontalAccuracy()
  {
    return horizontalAccuracy;
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
   * @return the latitude
   */
  @Column(nullable = false)
  public double getLatitude()
  {
    return latitude;
  }

  /**
   * @return the longitude
   */
  @Column(nullable = false)
  public double getLongitude()
  {
    return longitude;
  }

  /**
   * @return the speed
   */
  @Column
  public double getSpeed()
  {
    return speed;
  }

  /**
   * @return the timestamp
   */
  @Temporal(value = TemporalType.TIMESTAMP)
  public Calendar getTimestamp()
  {
    return timestamp;
  }

  /**
   * @return the user
   */
  @ManyToOne
  public User getUser()
  {
    return user;
  }

  /**
   * @return the verticalAccuracy
   */
  @Column
  public double getVerticalAccuracy()
  {
    return verticalAccuracy;
  }

  /**
   * @param altitude
   *          the altitude to set
   */
  public void setAltitude(double altitude)
  {
    this.altitude = altitude;
  }

  /**
   * @param course
   *          the course to set
   */
  public void setCourse(double course)
  {
    this.course = course;
  }

  /**
   * @param horizontalAccuracy
   *          the horizontalAccuracy to set
   */
  public void setHorizontalAccuracy(double horizontalAccuracy)
  {
    this.horizontalAccuracy = horizontalAccuracy;
  }

  /**
   * @param id
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(double latitude)
  {
    this.latitude = latitude;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(double longitude)
  {
    this.longitude = longitude;
  }

  /**
   * @param speed
   *          the speed to set
   */
  public void setSpeed(double speed)
  {
    this.speed = speed;
  }

  /**
   * @param timestamp
   *          the timestamp to set
   */
  public void setTimestamp(Calendar timestamp)
  {
    this.timestamp = timestamp;
  }

  /**
   * @param user
   *          the user to set
   */
  public void setUser(User user)
  {
    this.user = user;
  }

  /**
   * @param verticalAccuracy
   *          the verticalAccuracy to set
   */
  public void setVerticalAccuracy(double verticalAccuracy)
  {
    this.verticalAccuracy = verticalAccuracy;
  }

}
