package defects;

import javafx.concurrent.Task;

// doesn't work with Wrapping

public class Buggy02 {

    static class MyTask extends Task<Long> {
        @Override protected Long call() throws Exception {
            return 1L;
        }
    }
    
    public static void main(String... args) {
        Task<Long> u=new MyTask();
        u.getProgress();  // should work via Proxy

        MyTask t=new MyTask();
        t.getProgress();  // won't work: inherited from java library
    }
}
