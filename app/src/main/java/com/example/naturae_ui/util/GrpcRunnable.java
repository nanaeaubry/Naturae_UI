package com.example.naturae_ui.util;

import com.examples.naturaeproto.ServerRequestsGrpc;

/**
 * Implement to define a GrpcRunnable that returns all the logs
 */
public interface GrpcRunnable {
    String run(
            ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub,
            ServerRequestsGrpc.ServerRequestsStub asyncStub
    ) throws Exception;
}