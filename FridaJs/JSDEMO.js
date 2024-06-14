function getNowDate() {
    var myDate = new Date;
    var year = myDate.getFullYear();
    var mon = myDate.getMonth() + 1;
    var date = myDate.getDate();
    var hours = myDate.getHours();
    var minutes = myDate.getMinutes();
    var seconds = myDate.getSeconds();
    var now = year + "-" + mon + "-" + date + " " + hours + ":" + minutes + ":" + seconds;
    return now;
}

var startFlag = "【command-start】";
var endFlag = "【command-end】";
var separator = '_';

function start() {
    console.log('\r\n');
    console.log(startFlag);
}

function end() {
    console.log(endFlag);
}

function writeInfo(method, call, className, desc) {
    console.log("【time】" + separator + getNowDate());
    console.log("【method】" + separator + method);
    console.log("【call】" + separator + call);
    console.log("【class】" + separator + className);
    if (desc){
    console.log("【desc】" + separator + desc);
    }
    console.log("【stack】");

}


function Privacylist() {
    var locationManager = Java.use('android.location.LocationManager');
    var location = Java.use('android.location.Location');
    var telephonyManager = Java.use("android.telephony.TelephonyManager");

    var i = 1
    //imei
    Java.performNow(function () {
        var imeiInfo = Java.use('android.telephony.TelephonyManager');
        imeiInfo.getDeviceId.overload('int').implementation = function (a1) {
            start();
            var ret = this.getDeviceId(a1);
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getDeviceId(int)调用用户imei");
            writeInfo("getDeviceId(int)", "IMEI",'android.telephony.TelephonyManager','getDeviceId(int)调用用户imei');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return ret
        }
    });

    Java.performNow(function () {
        var imeiInfo = Java.use('android.telephony.TelephonyManager');
        //API level 26 获取单个IMEI的方法
        imeiInfo.getDeviceId.overload().implementation = function () {
            start();
            var ret = this.getDeviceId();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getDeviceId调用用户imei");
            writeInfo("getDeviceId", "IMEI",'android.telephony.TelephonyManager','getDeviceId调用用户imei');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return ret
        }
    });

    //地理位置
    Java.performNow(function () {
        var telephonyManager = Java.use('android.telephony.TelephonyManager');
        telephonyManager.getAllCellInfo.implementation = function () {
            start();
            var cellInfoList = this.getAllCellInfo();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getAllCellInfo调用获取地理位置");
            writeInfo("getAllCellInfo", "地理位置", 'android.telephony.TelephonyManager', '应用通过getAllCellInfo调用获取地理位置');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return cellInfoList;
        }
    });



    //imei
    Java.performNow(function () {
        var imeiInfo = Java.use('android.telephony.TelephonyManager');
        imeiInfo.getImei.overload().implementation = function () {
            start();
            var ret = this.getImei();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getImei调用系统imei");
            writeInfo("getImei", "IMEI",'android.telephony.TelephonyManager','getImei调用系统imei');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return ret
        }
    });
    
    //iccid
    Java.performNow(function () {
        var snInfo = Java.use('android.telephony.TelephonyManager');
        snInfo.getSimSerialNumber.overload().implementation = function () {
            start();
            var ret = this.getSimSerialNumber();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getSimSerialNumber获取Sim卡序列号");
            writeInfo("getSimSerialNumber", "Sim卡",'android.telephony.TelephonyManager','getSimSerialNumber调用Sim卡序列号');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return ret
        }
    });


    //imsi/iccid
    var TelephonyManager = Java.use("android.telephony.TelephonyManager");
    TelephonyManager.getSimSerialNumber.overload('int').implementation = function (p) {
        start();
        var temp = this.getSimSerialNumber(p);
        console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getSimSerialNumber获取Sim卡序列号");
        writeInfo("getSimSerialNumber", "Sim卡",'android.telephony.TelephonyManager','getSimSerialNumber调用Sim卡序列号');
        console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
        end();
        return temp;
    }

    //imsi
    Java.performNow(function () {
        telephonyManager.getSubscriberId.overload().implementation = function () {
            start();
            var subscriberId = this.getSubscriberId();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getSubscriberId调用用户IMSI");
            writeInfo("getSubscriberId", "IMSI", "android.telephony.TelephonyManager", "应用通过getSubscriberId调用用户IMSI");
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return subscriberId;
        }
    });


    //获取粗略定位
    Java.performNow(function () {
        var telephonyManager = Java.use('android.telephony.TelephonyManager');
        
        telephonyManager.getCellLocation.implementation = function () {
            start();
            var ret = this.getCellLocation();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getCellLocation方法调用用户位置信息");
            writeInfo("getCellLocation", "定位",'android.telephony.TelephonyManager', 'getCellLocation方法调用用户位置信息');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return ret;
        }
    });

    // Hook getNeighboringCellInfo 方法
    Java.performNow(function () {
        var telephonyManager = Java.use('android.telephony.TelephonyManager');

        telephonyManager.getNeighboringCellInfo.implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getNeighboringCellInfo方法获取附近基站信息");
            writeInfo("getNeighboringCellInfo", "定位", 'android.telephony.TelephonyManager', '应用通过getNeighboringCellInfo方法获取附近基站信息');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.getNeighboringCellInfo();
        }
    });

    // Hook getProvider 方法
    Java.performNow(function () {
        var locationManager = Java.use('android.location.LocationManager');

        locationManager.getProvider.overload('java.lang.String').implementation = function (name) {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getProvider方法获取位置提供程序的信息");
            writeInfo("getProvider", "定位", 'android.location.LocationManager', '应用通过getProvider方法获取位置提供程序的信息');
            // console.log("name: " + name);
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.getProvider(name);
        }
    });





    //SSID
    Java.performNow(function () {
        var WifiInfo = Java.use('android.net.wifi.WifiInfo');
        WifiInfo.getSSID.implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getMacAddress获取Wifi的SSID地址");
            writeInfo("SSID", "WiFi",'android.net.wifi.WifiInfo','getMacAddress获取Wifi的SSID地址');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.getSSID()
        }
    });

    //MEID
    Java.performNow(function () {
        var telephonyManager = Java.use('android.telephony.TelephonyManager');
        telephonyManager.getMeid.overload().implementation = function () {
            start();
            var meid = this.getMeid();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getMeid调用设备MEID号");
            writeInfo("getMeid", "MEID", 'android.telephony.TelephonyManager', '应用通过getMeid调用设备MEID号');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return meid;
        }
    });


    //Wifi
    Java.performNow(function () {
        var WifiInfo = Java.use('android.net.wifi.WifiInfo');
        WifiInfo.getIpAddress.overload().implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getIpAddress获取Wifi的IP地址");
            writeInfo("getIpAddress", "WiFi",'android.net.wifi.WifiInfo','getIpAddress获取Wifi的IP地址');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.getIpAddress()
        }
    });

     //Wifi信息
     Java.performNow(function () {
        var WifiInfoMan = Java.use('android.net.wifi.WifiManager');
        WifiInfoMan.getConnectionInfo.overload().implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getConnectionInfo获取wifi信息");
            writeInfo("getConnectionInfo", "WiFi",'android.net.wifi.WifiManager','getConnectionInfo获取wifi信息');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.getConnectionInfo()
        }
    });

    Java.performNow(function () {
        var WifiInfoMano = Java.use('android.net.wifi.WifiManager');
        WifiInfoMano.getConfiguredNetworks.overload().implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getConfiguredNetworks获取wifi信息");
            writeInfo("getConfiguredNetworks", "WiFi",'android.net.wifi.WifiManager','getConfiguredNetworks获取wifi信息');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.getConfiguredNetworks()
        }
    });

    Java.performNow(function () {
        var WifiInfoManonT = Java.use('android.net.wifi.WifiManager');
        WifiInfoManonT.getScanResults.overload().implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getScanResults获取wifi信息");
            writeInfo("getScanResults", "WiFi",'android.net.wifi.WifiManager','getScanResults获取wifi信息');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.WifiInfoManonT()
        }
    });

    Java.performNow(function () {
        var Networkip = Java.use('java.net.InetAddress');
        Networkip.getHostAddress.overload().implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getHostAddress获取网络IP");
            writeInfo("getHostAddress", "MAC",'java.net.InetAddress','getHostAddress获取网络IP');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.Networkip()
        }
    });

    //网络信息
    Java.performNow(function () {
        var Networkinfo_1 = Java.use('android.net.NetworkInfo');
        Networkinfo_1.isConnected.implementation = function () {
            start();
            var temp = this.isConnected()
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过isConnected获取网络是否连接");
            writeInfo("isConnected", "网络",'android.net.NetworkInfo','isConnected获取网络是否连接');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return temp;
        }
    });

    Java.performNow(function () {
        var Networkinfo_2 = Java.use('android.net.NetworkInfo');
        Networkinfo_2.isAvailable.implementation = function () {
            start();
            var temp = this.isAvailable();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过isConnected获取网络是否可用");
            writeInfo("isAvailable", "网络",'android.net.NetworkInfo','isConnected获取网络是否可用');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return temp;
        }
    });

    Java.performNow(function () {
        var Networkinfo_3 = Java.use('android.net.NetworkInfo');
        Networkinfo_3.getExtraInfo.implementation = function () {
            start();
            var temp = this.getExtraInfo();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getExtraInfo获取网络名称");
            writeInfo("getExtraInfo", "网络",'android.net.NetworkInfo','getExtraInfo获取网络名称');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return temp;
        }
    });

    Java.performNow(function () {
        var Networkinfo_4 = Java.use('android.net.NetworkInfo');
        Networkinfo_4.getTypeName.implementation = function () {
            start();
            var temp = this.getTypeName();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getTypeName获取网络类型名称");
            writeInfo("getTypeName", "网络",'android.net.NetworkInfo','getTypeName获取网络类型名称');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return temp;
        }
    });

    Java.performNow(function () {
        var Networkinfo_5 = Java.use('android.net.NetworkInfo');
        Networkinfo_5.getType.implementation = function () {
            start();
            var temp = this.getType();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getType获取网络类型");
            writeInfo("getType", "网络",'android.net.NetworkInfo','getType获取网络类型');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return temp;
        }
    });

    //BSSID
    Java.performNow(function () {
        var WifiInfo = Java.use('android.net.wifi.WifiInfo');
        WifiInfo.getBSSID.implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getBSSID获取Wifi的BSSID");
            writeInfo("BSSID", "WiFi",'android.net.wifi.WifiInfo','getBSSID获取Wifi的BSSID');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.getBSSID()
        }
    });

    //mac

    Java.performNow(function () {
        var macInfo = Java.use('android.net.wifi.WifiInfo');
        macInfo.getMacAddress.overload().implementation = function () {
            start();
            var ret = this.getMacAddress();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getMacAddress调用系统mac");
            writeInfo("getMacAddress", "MAC",'android.net.wifi.WifiInfo','getMacAddress调用系统mac');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return ret
        }
    });

    Java.performNow(function () {
        var macInfo = Java.use('java.net.NetworkInterface');
        macInfo.getHardwareAddress.overload().implementation = function () {
            start();
            var ret = this.getHardwareAddress();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getHardwareAddress调用系统mac");
            writeInfo("getHardwareAddress", "MAC",'java.net.NetworkInterface','getHardwareAddress调用系统mac');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return ret
        }
    });

    


    //安卓ID
    Java.performNow(function () {
        var CursorWrapper = Java.use('android.provider.Settings$Secure');
        CursorWrapper.getString.overload('android.content.ContentResolver','java.lang.String').implementation = function (a1,a2) {
            // 2023-09-14检查请求的键是否为 'android_id'，这是用于获取Android ID的键
            if (a2 && a2.toString() === "android_id") {
                start();
                console.log("【" + getNowDate() + "】" + i++ + ". 应用通过getString调用获取安卓ID");
                writeInfo("getString", "安卓ID",'android.provider.Settings$Secure','getString调用获取安卓ID');
                console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
                end();
            }
            return this.getString(a1, a2);
        }
    }

    //获取蓝牙设备信息
    try {
        var BluetoothDevice = Java.use("android.bluetooth.BluetoothDevice");

        //获取蓝牙设备名称
        BluetoothDevice.getName.overload().implementation = function () {
            var temp = this.getName();
            start();
            console.log("【"+ getNowDate() +"】"+ i++ +"获取蓝牙设备名称");
            writeInfo("getName", "蓝牙",'android.bluetooth.BluetoothDevice','获取蓝牙设备名称');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return temp;
        }

        // 获取蓝牙设备mac
        BluetoothDevice.getAddress.implementation = function () {
            var temp = this.getAddress();
            start();
            console.log("【"+ getNowDate() +"】"+ i++ +"获取蓝牙设备Mac");
            writeInfo("getAddress", "蓝牙",'android.bluetooth.BluetoothDevice','获取蓝牙设备Mac');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return temp;
        }
    } catch (e) {
        console.log(e)
    }
    try {
        var BluetoothAdapter = Java.use("android.bluetooth.BluetoothAdapter");

        //获取蓝牙设备名称
        BluetoothAdapter.getName.implementation = function () {
            var temp = this.getName();
            // console.log("获取蓝牙信息", "获取到的蓝牙设备名称: " + temp)
            start();
            console.log("【"+ getNowDate() +"】"+ i++ +"获取蓝牙设备名称");
            writeInfo("getName", "蓝牙",'android.bluetooth.BluetoothDevice','获取蓝牙设备名称');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return temp;
        };
    } catch (e) {
        console.log(e)
    }

    // Hook getAddress 方法
    //Android 8.0开始，getAddress()方法将始终返回02:00:00:00:00:00
    Java.performNow(function () {
        // var bluetoothAdapter = Java.use('android.bluetooth.BluetoothAdapter');

        BluetoothAdapter.getAddress.implementation = function () {
            start();
            console.log("【" + getNowDate() + "】" + i++ + ". 应用通过 getAddress 方法获取设备的蓝牙 MAC 地址");
            writeInfo("getAddress()", "Bluetooth MAC Address", 'android.bluetooth.BluetoothAdapter', '应用通过getAddress方法获取设备的蓝牙 MAC 地址');
            console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Throwable").$new()));
            end();
            return this.getAddress();
        }
    });

}

// Privacylist()

//蓝牙协议既有可能卡住某一些App，如果排除不出问题，将蓝牙Api注释掉
setImmediate(Privacylist)
// setTimeout(Privacylist,6000);