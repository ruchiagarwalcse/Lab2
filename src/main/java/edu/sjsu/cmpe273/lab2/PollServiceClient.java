package edu.sjsu.cmpe273.lab2;

import com.google.common.util.concurrent.SettableFuture;

import io.grpc.ChannelImpl;
import edu.sjsu.cmpe273.lab2.PollServiceGrpc.PollServiceBlockingStub;
import edu.sjsu.cmpe273.lab2.PollServiceGrpc.PollServiceStub;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Sample client code that makes gRPC calls to the server.
 */
public class PollServiceClient {
    private static final Logger logger = Logger.getLogger(PollServiceClient.class.getName());

    private final ChannelImpl channel;
    private final PollServiceBlockingStub blockingStub;
    private final PollServiceStub asyncStub;

    public PollServiceClient(String host, int port) {
        channel = NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        blockingStub = PollServiceGrpc.newBlockingStub(channel);
        asyncStub = PollServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
    }


    public void createPoll(String moderatorId, String question, String startedAt, String expiredAt, String[] choice) {
        if (choice == null || choice.length < 2){
            new RuntimeException("Choice must have two items in it.");
        }
        try {
            logger.info("Creating a new poll for moderator " + moderatorId);
            PollRequest request = PollRequest.newBuilder()
                    .setModeratorId(moderatorId)
                    .setQuestion(question)
                    .setStartedAt(startedAt)
                    .setExpiredAt(expiredAt)
                    .addChoice(choice[0])
                    .addChoice(choice[1]).build();
            PollResponse response = blockingStub.createPoll(request);
            logger.info("Created a new poll with id = " + response.getId());
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "RPC failed", e);
            return;
        }
    }


   public static void main(String[] args) throws Exception {
       PollServiceClient client = new PollServiceClient("localhost", 50051);
        try {
            String moderatorId = "1";
            String question = "What type of smartphone do you have?";
            String startedAt = "2015-02-23T13:00:00.000Z";
            String expiredAt = "2015-02-24T13:00:00.000Z";
            String[] choice = new String[] {"Android", "iPhone"};
            client.createPoll(moderatorId, question, startedAt, expiredAt, choice);
        } finally {
            client.shutdown();
        }
    }

}