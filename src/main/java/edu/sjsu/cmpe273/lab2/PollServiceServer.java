package edu.sjsu.cmpe273.lab2;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sample gRPC server that serve the Polling Service.
 */
public class PollServiceServer {
    private static final Logger logger = Logger.getLogger(PollServiceServer.class.getName());

    private final int port;
    private ServerImpl gRpcServer;
    private static String id="1ADC2FZ";

    public PollServiceServer(int port) {
            this.port = port;
    }

    public void start() {
        gRpcServer = NettyServerBuilder.forPort(port)
                .addService(PollServiceGrpc.bindService(new PollServiceImpl()))
                .build().start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                PollServiceServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {
        if (gRpcServer != null) {
            gRpcServer.shutdown();
        }
    }

    public static void main(String[] args) throws Exception {
        PollServiceServer server = new PollServiceServer(50051);
        server.start();
    }

    private static class PollServiceImpl implements PollServiceGrpc.PollService {

        @Override
        public void createPoll(PollRequest req, StreamObserver<PollResponse> responseObserver) {
            PollResponse reply = PollResponse.newBuilder().setId(id).build();
            responseObserver.onValue(reply);
            System.out.println("Created a new poll for moderator " + req.getModeratorId());
            responseObserver.onCompleted();
        }


    }
}
