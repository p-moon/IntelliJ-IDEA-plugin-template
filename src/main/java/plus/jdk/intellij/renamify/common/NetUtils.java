package plus.jdk.intellij.renamify.common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class NetUtils {

    public static int computeStringHash(String s) {
        int hashValue = 0;
        int prime = 31; // 常用的质数乘数
        for (int i = 0; i < s.length(); i++) {
            hashValue = (hashValue * prime + s.charAt(i)) % 65535;
        }
        // 确保哈希值在1000到65535之间
        if (hashValue < 1000) {
            hashValue += 1000;
        }
        return hashValue;
    }

    public static int getProjectAvailablePort(String name) {
        int projectPort = computeStringHash(name);
        return getProjectAvailablePort(name, projectPort);
    }

    public static int getProjectAvailablePort(String host, int port) {
        for (int i = port; i <= 65535; i++) {
            if(canTelnet(host, i, 200)) {
                continue;
            }
            return i;
        }
        throw new RuntimeException(
                "Can't bind to ANY port of " + host + ", please check config");
    }

    public static boolean canTelnet(String ip, int port, int timeout) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return socket.isConnected();
        } catch (Exception e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
