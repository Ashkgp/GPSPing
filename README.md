#GPSPing

###Reads the GPS Locations and pings it to the specified server and port

GPS location is retrieved using LocationManager class and thus is compatible with Android 4.0 ICS.

GPS needs to be pre configured and network access correctly setup to allow app access to network and GPS location services.

At the very beginning GPS last know location is transmitted and thus first data shouldn't be used for the tracking.

App asks for an IP and port to transmit data to and connects to it using BufferWriter class.

BufferWriter object is returned to the location class and data is written on the server everytime there is a location change of the phone.