package delay;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: JDK
 * @ClassName: DelayQueueTest
 * @Author: cg
 * @CreateDate: 2019-12-18 14:48
 * @Version: 1.0
 * Copyright: Copyright (c) 2019
 */
public class DelayQueueTest {

    public static void main(String[] args) {
        DelayQueue<Message> messageDelayQueue = new DelayQueue<>();
        Message msg1 = new Message(1, "消息1", 1000);
        Message msg2 = new Message(5, "消息2", 5000);
        messageDelayQueue.offer(msg1);
        messageDelayQueue.offer(msg2);
        new Thread(new Consumer(messageDelayQueue)).start();
    }
}

@Data
class Message implements Delayed {

    private int id;
    private String body;
    private long executeTime;

    public Message(int id,
                   String body,
                   long delayTime) {
        this.id = id;
        this.body = body;
        this.executeTime = TimeUnit.MILLISECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return TimeUnit.MILLISECONDS.convert(this.executeTime- System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        Message message = (Message) o;
        return this.executeTime - message.getExecuteTime() > 0 ? 1 : -1;
    }

}

@AllArgsConstructor
class Consumer implements Runnable {

    private DelayQueue<Message> messageDelayQueue;

    @Override
    public void run() {
        for (;;) {
            try {
                Message message = messageDelayQueue.take();
                System.out.println(String.format("消费消息：执行时间【%d】| 消息内容【%s】", message.getExecuteTime(), message.getBody()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
