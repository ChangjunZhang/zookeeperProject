package cn.wordCount.zkdist;

/**
 * Created by changjun.zhang on 2017/7/13.
 * 线程测试类
 */
public class ThreadTest {
    public static void main(String[] args) {

        System.out.println("主线程开始了");

        Thread thread = new Thread(new Runnable() {
            public void run() {
                System.out.println("线程开始了");
                while(true){

                }
            }
        });
        thread.setDaemon(true);//设置为守护线程，主线程退出后子线程继续运行
        thread.start();

    }
}
