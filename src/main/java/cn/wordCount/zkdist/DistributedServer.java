package cn.wordCount.zkdist;

import org.apache.zookeeper.*;

/**
 * Created by changjun.zhang on 2017/7/13.
 * 分布式服务系统的服务
 */
public class DistributedServer {

    /**
     * 注意，一定要与zk的配置文件中的绑定的服务一致
     */
    private static final String connectString = "spark01:2181,spark02:2181,spark03:2181";
    private static final int sessionTimeout = 2000;
    private static final String parentNode = "/servers";


    private ZooKeeper zk = null;
    /**
     * 创建到zk的客户端连接
     *
     * @throws Exception
     */
    public void getConnect() throws Exception {

        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent event) {
                // 收到事件通知后的回调函数（应该是我们自己的事件处理逻辑）
                System.out.println(event.getType() + "---" + event.getPath());
               /* try {
                    zk.getChildren("/", true);
                } catch (Exception e) {
                }*/
            }
        });
    }

    /**
     * 向zk集群注册服务器信息
     * @param hostname
     * @throws Exception
     */
    public void registerServer(String hostname) throws Exception {
        String create = zk.create(parentNode + "/server", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + "is online.." + create);

    }

    /**
     * 业务功能
     *
     * @throws InterruptedException
     */
    public void handleBussiness(String hostname) throws InterruptedException {
        System.out.println(hostname + "start working.....");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws Exception {

        // 获取zk连接
        DistributedServer server = new DistributedServer();
        server.getConnect();

        // 利用zk连接注册服务器信息
        server.registerServer(args[0]);

        // 启动业务功能
        server.handleBussiness(args[0]);

    }
}
