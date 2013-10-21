package com.kb.ukhocrawler.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kb.ukhocrawler.service.voa.VoaAsItIsService;
import com.kb.ukhocrawler.utils.Util;

public class VoaController extends Controller {
    public void start(String[] args) throws IOException {
        String input = args[1];
        String output = args[2];
        int totalPages = args.length > 3 ? Integer.parseInt(args[3]) : 1;

        Util.print("input: %s", input);
        Util.print("output: %s", output);
        Util.print("totalPages: %d", totalPages);

        ExecutorService searchExecutor = Executors.newFixedThreadPool(2);
        List<VoaAsItIsService> searchers = new ArrayList<VoaAsItIsService>();

        VoaAsItIsService searcher = new VoaAsItIsService(totalPages, output);
        searchExecutor.execute(searcher);
        searchers.add(searcher);

        searchExecutor.shutdown();
        while (!searchExecutor.isTerminated()) {
        }
    }
}
