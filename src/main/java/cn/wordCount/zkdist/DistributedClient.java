package cn.wordCount.zkdist;

import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by changjun.zhang on 2017/7/13.
 * 分布式服务的客户端，监听服务器的列表的状态
 */
public class DistributedClient {
    /**
     * 注意，一定要与zk的配置文件中的绑定的服务一致
     */
    private static final String connectString = "spark01:2181,spark02:2181,spark03:2181";
    private static final int sessionTimeout = 2000;
    private static final String parentNode = "/servers";
    // 注意:加volatile的意义：为了保证多线程工作时多个线程操作的为同一个对象
    private volatile List<String> serverList;

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
                try {
                    //重新更新服务器列表，并且在方法中注册对父节点监听
                    getServerList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取服务器信息列表
     * @throws Exception
     */
    public void getServerList() throws Exception {

        // 获取服务器子节点信息，并且对父节点进行监听
        List<String> children = zk.getChildren(parentNode, true);

        // 先创建一个局部的list来存服务器信息
        List<String> servers = new ArrayList<String>();
        for (String child : children) {
            // child只是子节点的节点名
            byte[] data = zk.getData(parentNode + "/" + child, false, null);
            servers.add(new String(data));
        }
        // 把servers赋值给成员变量serverList，已提供给各业务线程使用
        serverList = servers;

        //打印服务器列表
        System.out.println(serverList);

    }

    /**
     * 业务功能
     *
     * @throws InterruptedException
     */
    public void handleBussiness() throws InterruptedException {
        System.out.println("client start working.....");
        Thread.sleep(Long.MAX_VALUE);
    }


    public static void main(String[] args) throws Exception {

        // 获取zk连接
        DistributedClient client = new DistributedClient();
        client.getConnect();
        // 获取servers的子节点信息（并监听），从中获取服务器信息列表
        client.getServerList();

        // 业务线程启动
        client.handleBussiness();

    }
}
