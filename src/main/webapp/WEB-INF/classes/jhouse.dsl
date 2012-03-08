[consequence][]Log : "{message}"=System.out.println("{message}");
[condition][]Device {id} value is {value}=$d : Device(id == {id}, value == {value})
[condition][]Device {id} value changed to {value}=dv : DeviceValueEvent(id == {id}, value == {value}, changed == true)
[condition][]Device {id} value set to {value}=$dv : DeviceValueEvent(id == {id}, value == {value}, changed == false)
[consequence][]Turn on binary switch {id}=dm.getDriver({id},BinarySwitch.class).setOn();
[consequence][]Turn off binary switch {id}=dm.getDriver({id},BinarySwitch.class).setOff();
[condition][]Security zone device {id} changed to open=$dve : DeviceValueEvent(id == {id}, (value & 1) == 1, changed == true)
[condition][]Security zone device {id} changed to closed=$dve : DeviceValueEvent(id == {id}, (value & 1) == 0, changed == true)
[consequence][]Send email to "{recipient}" with subject "{subject}" and message "{message}"=email.send("{recipient}","{subject}","{message}");
[consequence][]Send email to "{recipient}" with subject "{subject}"=email.send("{recipient}","{subject}","");
[condition][]Is Sunset=TimeEvent(eventType == TimeEventType.SUNSET)
[condition][]Is Sunrise=TimeEvent(eventType == TimeEventType.SUNRISE)
[condition][]Is Noon=TimeEvent(eventType == TimeEventType.NOON)
