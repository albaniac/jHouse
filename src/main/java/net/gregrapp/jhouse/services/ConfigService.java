package net.gregrapp.jhouse.services;

public interface ConfigService
{

  /**
   * Get configuration option
   * 
   * @param namespace
   *          option namespace
   * @param key
   *          option key
   * @return
   */
  public String get(String namespace, String key);

  /**
   * Get configuration option from default namespace
   * 
   * @param key
   *          option key
   * @return
   */
  public String get(String key);

  /**
   * Set configuration option
   * 
   * @param namespace
   *          option namespace
   * @param key
   *          option key
   * @param value
   *          option value
   */
  public void set(String namespace, String key, String value); 
  
  /**
   * Set configuration option in default namespace
   * 
   * @param key
   *          option key
   * @param value
   *          option value
   */
  public void set(String key, String value); 

}