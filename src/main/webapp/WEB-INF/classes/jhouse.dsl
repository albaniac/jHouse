[consequence][]Log : "{message}"=System.out.println("{message}");
[condition][]Device {id} value is {value}=$d : Device(id == {id}, value == {value})
[condition][]Device {id} value changed to {value}=dv : DeviceValueEvent(id == {id}, value == {value}, changed == true)
[condition][]Device {id} value set to {value}=$dv : DeviceValueEvent(id == {id}, value == {value}, changed == false)
[consequence][]Turn on binary switch {id}=dm.getDriver({id},BinarySwitch.class).setOn();
[consequence][]Turn off binary switch {id}=dm.getDriver({id},BinarySwitch.class).setOff();
