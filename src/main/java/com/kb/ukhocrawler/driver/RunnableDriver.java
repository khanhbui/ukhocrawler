package com.kb.ukhocrawler.driver;

import java.io.IOException;

public abstract class RunnableDriver implements Runnable {
    protected abstract void download() throws IOException;
    protected abstract void onError();

    public void run() {
        try {
            download();
        } catch (IOException e) {
            this.onError();
            e.printStackTrace();
        }
    }
}
