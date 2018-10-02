package top.infra.test;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.net.InetAddress;

@Slf4j
public abstract class NetUtils {

    private NetUtils() {
    }

    public static String getIpAddressQuietly() {
        String localAddress;

        final String envHostIpaddress = System.getenv("HOST_IPADDRESS");
        if (isNotBlank(envHostIpaddress)) {
            localAddress = envHostIpaddress;
        } else {
            final InetUtilsProperties inetUtilsProperties = new InetUtilsProperties();
            try (final InetUtils inetUtils = new InetUtils(inetUtilsProperties);) {
                final InetAddress firstNonLoopbackAddress = inetUtils.findFirstNonLoopbackAddress();

                if (firstNonLoopbackAddress != null) {
                    localAddress = firstNonLoopbackAddress.getHostAddress();
                } else {
                    try {
                        localAddress = InetAddress.getLocalHost().getHostAddress();
                    } catch (Exception ex) {
                        log.info("getIpAddressQuietly", "cant resolve localhost address", ex);
                        localAddress = "127.0.0.1";
                    }
                }
            }
        }

        return localAddress;
    }
}
