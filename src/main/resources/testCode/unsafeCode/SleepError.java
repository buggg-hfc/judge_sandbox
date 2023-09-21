/**
 * 无限睡眠（阻塞程序执行）
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        long TEN_SECOND = 10 * 1000;
        Thread.sleep(TEN_SECOND);
        System.out.println("睡完了");
    }
}
