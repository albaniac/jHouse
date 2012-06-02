[consequence][]Log : "{message}"=System.out.println("{message}");
[condition][]Device {id} value is {value}=$d : Device(id == {id}, value == {value})
[condition][]Device {id} value changed to {value}=$dve : DeviceValueEvent(id == {id}, value == {value}, changed == true)
[condition][]Device {id} value set to {value}=$dve : DeviceValueEvent(id == {id}, value == {value}, changed == false)
[condition][]Device {id} is off=$d : Device(id == {id}, value == 0)
[condition][]Device {id} changed to off=$dve : DeviceValueEvent(id == {id}, value == 0, changed == true)
[condition][]Device {id} set to off=$dve : DeviceValueEvent(id == {id}, value == 0, changed == false)
[condition][]Device {id} is on=$d : Device(id == {id}, value == 255)
[condition][]Device {id} changed to on=$dve : DeviceValueEvent(id == {id}, value == 255, changed == true)
[condition][]Device {id} set to on=$dve : DeviceValueEvent(id == {id}, value == 255, changed == false)
[consequence][]Turn on binary switch {id}=device.getDriver({id},BinarySwitch.class).setOn();
[consequence][]Turn off binary switch {id}=device.getDriver({id},BinarySwitch.class).setOff();
[condition][]Security zone device {id} changed to open=$dve : DeviceValueEvent(id == {id}, (value & 1) == 1, changed == true)
[condition][]Security zone device {id} changed to closed=$dve : DeviceValueEvent(id == {id}, (value & 1) == 0, changed == true)
[consequence][]Send email to "{recipient}" with subject "{subject}" and message "{message}"=email.send("{recipient}","{subject}","{message}");
[consequence][]Send email to "{recipient}" with subject "{subject}"=email.send("{recipient}","{subject}","");
[condition][]Is Sunset=TimeEvent(eventType == TimeEventType.SUNSET)
[condition][]Is Sunrise=TimeEvent(eventType == TimeEventType.SUNRISE)
[condition][]Is Noon=TimeEvent(eventType == TimeEventType.NOON)
[consequence][]Send APNs message to user {id} with text "{body}" and sound "{sound}"=apns.send({id}, "{body}", "{sound}");
[consequence][]Send APNs message to user {id} with text "{body}" and badge {badge}=apns.send({id}, "{body}", {badge});
[consequence][]Send APNs message to user {id} with text "{body}" and sound=apns.send({id}, "{body}", "default");
[consequence][]Send APNs message to user {id} with text "{body}"=apns.send({id}, "{body}");
