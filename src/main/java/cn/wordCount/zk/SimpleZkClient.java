package cn.wordCount.zk;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by changjun.zhang on 2017/7/13.
 * 测试代码，测试zk的增删改查
 * 实际场景应捕获所有异常并处理
 */
public class SimpleZkClient {

    /**
     * 注意，一定要与zk的配置文件中的绑定的服务一致,主机名和ip不能相互替换
     */
    private static final String connectString = "spark01:2181,spark02:2181,spark03:2181";
    private static final int sessionTimeout = 2000;

    ZooKeeper zkClient = null;

    @Before
    private void init() throws Exception {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent event) {
                // 收到事件通知后的回调函数（应该是我们自己的事件处理逻辑）
                System.out.println(event.getType() + "---" + event.getPath());

                /**
                 * ###注意：默认的监听器只会监听一次事件变化，要想一直监听需要在第一次监听回调函数处理完后继续注册下次监听
                 */
                try {
                    zkClient.getChildren("/", true);
                } catch (Exception e) {
                }
            }
        });

    }

    /**
     * 数据的增删改查
     *
     * @throws InterruptedException
     * @throws KeeperException
     */

    // 创建数据节点到zk中
    @Test
    public void testCreate() throws KeeperException, InterruptedException {
        // 参数1：要创建的节点的路径 参数2：节点的数据 参数3：节点的权限 参数4：节点的类型
        String nodeCreated = zkClient.create("/eclipse", "hellozk".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //上传的数据可以是任何类型，但都要转成byte[]
    }

    //判断zNode是否存在
    @Test
    public void testExist() throws Exception{
        Stat stat = zkClient.exists("/eclipse", false);
        System.out.println(stat==null?"not exist":"exist");
    }

    // 获取子节点
    @Test
    public void getChildren() throws Exception {
        List<String> children = zkClient.getChildren("/", true);//true表示调用zkClient的监听器
        for (String child : children) {
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);//为了测试程序监听，否则程序运行完成后会自动退出
    }

    //获取zNode的数据
    @Test
    public void getData() throws Exception {

        byte[] data = zkClient.getData("/eclipse", false, null);//false表示不监听
        System.out.println(new String(data));

    }

    //删除zNode
    @Test
    public void deleteZnode() throws Exception {

        //参数2：指定要删除的版本，-1表示删除所有版本
        zkClient.delete("/eclipse", -1);


    }
    //删除zNode
    @Test
    public void setData() throws Exception {

        zkClient.setData("/app1", "hellohellohello".getBytes(), -1);

        byte[] data = zkClient.getData("/app1", false, null);
        System.out.println(new String(data));

    }


}
