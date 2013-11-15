package com.kb.ukhocrawler.service;

public abstract class RunnableService implements Runnable {
    protected abstract void download() throws Exception;
    protected abstract void onError();

    public void run() {
        try {
            download();
        } catch (Exception e) {
            this.onError();
            e.printStackTrace();
        }
    }
}
