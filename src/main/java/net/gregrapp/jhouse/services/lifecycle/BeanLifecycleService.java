package net.gregrapp.jhouse.services.lifecycle;

public interface BeanLifecycleService
{
  public static final String DEVICE_BEAN_NAME_PREFIX = "DEVICE:";
  public static final String DRIVER_BEAN_NAME_PREFIX = "DRIVER:";
  public static final String INTERFACE_BEAN_NAME_PREFIX = "INTERFACE:";
  public static final String TRANSPORT_BEAN_NAME_PREFIX = "TRANSPORT:";

  public void init();

  public abstract void instantiateDeviceBeans();

  public abstract void instantiateDriverBeans();

  public abstract void instantiateInterfaceBeans();

  public abstract void instantiateTransportBeans();

}