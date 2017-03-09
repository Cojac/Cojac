package com.github.cojac.defects;

import javafx.concurrent.Task;

// didn't work with Wrapping
// fixed using proxy trick (maybe strong performance overhead)

public class Buggy02 {

    static class MyTask extends Task<Long> {
        @Override protected Long call() throws Exception {
            updateProgress(0.5, 0.5);  // won't work: inherited from java library
            return 1L;
        }
    }
    
    public static void main(String... args) throws Exception {
        MyTask t=new MyTask();
        Task<Long> u=t;
        u.getProgress();  // should work via Proxy
        t.getProgress();  // won't work: inherited from java library
    }
}
