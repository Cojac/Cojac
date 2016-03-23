package defects;

import javax.management.RuntimeErrorException;

import javafx.concurrent.Task;

// doesn't work with Wrapping

public class Buggy02 {

    static class MyTask extends Task<Long> {
        MyTask() {try {call(); } catch(Exception e) {throw new RuntimeException(e);}}
        @Override protected Long call() throws Exception {
            updateProgress(0.5, 0.5);  // won't work: inherited from java library
            return 1L;
        }
    }
    
    public static void main(String... args) throws Exception {
        Task<Long> u=new MyTask();
        u.getProgress();  // should work via Proxy

        MyTask t=new MyTask();
        t.getProgress();  // won't work: inherited from java library
    }
}
