package cn.home1.test;

import static cn.home1.test.WaitForIt.waitForIt;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.spotify.docker.client.DockerClient.LogsParam.follow;
import static com.spotify.docker.client.DockerClient.LogsParam.stderr;
import static com.spotify.docker.client.DockerClient.LogsParam.stdout;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import top.infra.test.NetUtils;

@Slf4j
public final class EurekaTestContainer {

    private static final String DEFAULT_IMAGE = "cloudready/spring-cloud-eureka-server:2.0.1-SNAPSHOT";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final DockerClient dockerClient;
    private final String eurekaPortOnContainer;
    private final ServerSocket eurekaPortOnHost;
    private final String image;

    private final CopyOutputStream streamErr;
    private final CopyOutputStream streamOut;

    private String containerId;

    @SneakyThrows
    public EurekaTestContainer(final String image, final ServerSocket eurekaPort) {
        this.dockerClient = DefaultDockerClient.fromEnv().build();
        this.eurekaPortOnContainer = "8761";
        this.image = image;
        this.eurekaPortOnHost = eurekaPort;

        this.streamErr = new CopyOutputStream(System.err);
        this.streamOut = new CopyOutputStream(System.out);
    }

    public EurekaTestContainer(final ServerSocket eurekaPort) {
        this(DEFAULT_IMAGE, eurekaPort);
    }

    public EurekaTestContainer() {
        this(DEFAULT_IMAGE, EurekaTestContainer.freePort());
    }

    @SneakyThrows
    public static ServerSocket freePort() {
        return new ServerSocket(0);
    }

    @SneakyThrows
    private String createContainer() {
        log.info("pull {}", this.image);
        this.dockerClient.pull(this.image);

        log.info("create container from image {}", this.image);
        final PortBinding eurekaPortOnHost = PortBinding.of("0.0.0.0", this.eurekaPortOnHost.getLocalPort());
        log.info("using random port {} on host", this.eurekaPortOnHost);

        final Map<String, List<PortBinding>> portBindings = newLinkedHashMap();
        portBindings.put(this.eurekaPortOnContainer, newArrayList(eurekaPortOnHost));
        final HostConfig hostConfig = HostConfig.builder() //
            .portBindings(portBindings) //
            .build();

        final ContainerConfig containerConfig = ContainerConfig.builder() //
            //.cmd() //
            .env( //
                "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=" + this.getDefaultZone(), //
                "LOGGING_LEVEL_ROOT=INFO" //
            )
            .exposedPorts(newLinkedHashSet(newArrayList(this.eurekaPortOnContainer))) //
            .hostConfig(hostConfig) //
            .image(this.image) //
            .build();

        final String containerName = "testContainer_" + this.eurekaPortOnHost.getLocalPort();
        final ContainerCreation containerCreation = this.dockerClient.createContainer(containerConfig, containerName);
        return containerCreation.id();
    }

    private void waitForStart() {
        this.executorService.execute(() -> {
            log.info("trying to attach logs of container {}", containerId);
            try (final LogStream logStream = dockerClient.logs(containerId, stdout(), stderr(), follow())) {
                logStream.attach(streamOut, streamErr, true);
            } catch (final Exception ex) {
                log.info("error attach logs of container {}", containerId, ex);
            }
        });

        final int timeoutInSeconds = 60;

        this.streamOut.waitForLine(".+Started EurekaServer.+", timeoutInSeconds);

        log.info("wait for port {}, timeout in seconds {}", this.eurekaPortOnHost, timeoutInSeconds);
        if (!waitForIt(this.eurekaPortOnHost.getLocalPort(), timeoutInSeconds)) {
            this.remove();
            throw new RuntimeException("eureka service not reachable on port " + this.eurekaPortOnHost);
        }
        log.info("port {} ready", this.eurekaPortOnHost);
    }

    @SneakyThrows
    public void start() {
        final String containerId = this.createContainer();
        this.containerId = containerId;

        this.eurekaPortOnHost.close();

        log.info("start container {}", containerId);
        // Start container
        this.dockerClient.startContainer(containerId);

        this.waitForStart();
        log.info("container {} started", containerId);
    }

    public int getEurekaPort() {
        return this.eurekaPortOnHost.getLocalPort();
    }

    public String getDefaultZone() {
        final String ipAddress = NetUtils.getIpAddressQuietly();
        final int eurekaPort = this.getEurekaPort();
        return "http://user:user_pass@" + ipAddress + ":" + eurekaPort + "/eureka/";
    }

    public void remove() {
        log.info("remove container {}", this.containerId);

        try {
            // Kill container
            log.info("kill container {}", this.containerId);
            this.dockerClient.killContainer(this.containerId);
        } catch (final Exception ex) {
            log.info("error kill container {}", this.containerId, ex);
        }

        this.executorService.shutdown();

        // Inspect container
        final ContainerInfo containerInfo;
        try {
            containerInfo = this.dockerClient.inspectContainer(this.containerId);
        } catch (final Exception ex) {
            log.info("error inspect container {}", this.containerId, ex);
            throw new RuntimeException(ex);
        }

        try {
            // Remove container
            log.info("remove container {}", this.containerId);
            this.dockerClient.removeContainer(this.containerId);
        } catch (final Exception ex) {
            log.info("error remove container {}", this.containerId, ex);
        }

        containerInfo.mounts().forEach(mount -> {
            try {
                log.info("remove volume {}", mount.name());
                this.dockerClient.removeVolume(mount.name());
            } catch (final Exception ex) {
                log.info("error remove volume {}", mount, ex);
            }
        });

        // Close the docker client
        this.dockerClient.close();
    }
}
