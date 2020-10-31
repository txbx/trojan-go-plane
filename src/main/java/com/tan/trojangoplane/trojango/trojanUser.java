package com.tan.trojangoplane.trojango;

import com.tan.trojangoplane.model.pojo.User;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import trojan.api.Api;
import trojan.api.TrojanServerServiceGrpc;

import java.util.Iterator;
import java.util.List;

/**
 * @author : tan
 * @date: 2020/10/31 20:38
 * @description :
 */
public class trojanUser {
    @Value("${trojangoserver.host}")
    private static final String host =null;
    @Value("${trojangoserver.port}")
    private static final int port = 0;

    public List<User> findAll(){
        //获取所有用户列表
        io.grpc.Channel channel = NettyChannelBuilder.forAddress(host,port)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        Api.ListUsersRequest req = Api.ListUsersRequest.newBuilder().build();
        Iterator<Api.ListUsersResponse> listUsersResponseIterator = TrojanServerServiceGrpc.newBlockingStub(channel).listUsers(req);
        while (listUsersResponseIterator.hasNext()){
            String s = listUsersResponseIterator.next().toString();
            //切割字符串获取字段值，set到user
            System.out.println(s);
        }

        return null;

    }

}
