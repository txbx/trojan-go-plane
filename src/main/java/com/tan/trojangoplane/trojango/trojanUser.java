package com.tan.trojangoplane.trojango;

import com.tan.trojangoplane.model.pojo.User;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import trojan.api.Api;
import trojan.api.TrojanClientServiceGrpc;
import trojan.api.TrojanServerServiceGrpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : tan
 * @date: 2020/10/31 20:38
 * @description :
 */
@Component
public class trojanUser {
    private static final String host = "34.97.228.51";
    private static final int port = 2333;
    io.grpc.Channel channel = NettyChannelBuilder.forAddress(host,port)
            .negotiationType(NegotiationType.PLAINTEXT)
            .build();
    //异步请求存根
    private TrojanServerServiceGrpc.TrojanServerServiceStub trojanServerServiceStub;
    //同步请求存根
    private TrojanServerServiceGrpc.TrojanServerServiceBlockingStub trojanServerServiceBlockingStub;

    public List<User> findAll(){
        //获取所有用户列表
        Api.ListUsersRequest req = Api.ListUsersRequest.newBuilder().build();
        Iterator<Api.ListUsersResponse> listUsersResponseIterator =
                TrojanServerServiceGrpc.newBlockingStub(channel).listUsers(req);
        List<User> userList = new ArrayList<>();
        while (listUsersResponseIterator.hasNext()){
            String s = listUsersResponseIterator.next().toString();
            //去掉字符串中的换行空格
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(s);
            s = m.replaceAll("");

            User user = new User();

            //切割字符串获取字段值，set到user
            //hash:
            String hash = s.substring(s.indexOf("hash:")+6,s.indexOf("\"}traffic"));
            user.setPwhash(hash);

            //sumupload
            if(s.contains("upload_traffic")){
                String sumupload = s.substring(s.indexOf("upload_traffic:")+15,s.indexOf("download"));
                user.setSumupload(Long.parseLong(sumupload));
            }
            //sumdownload:
            if(s.contains("download_traffic")){
                String sumdownload = s.substring(s.indexOf("download_traffic:")+17,s.indexOf("}speed"));
                user.setSumdownload(Long.parseLong(sumdownload));
            }

            if(s.contains("speed_current{upload")||s.contains("speed_current{download")){
                //截取当前速度字段
                String current = s.substring(s.indexOf("speed_current{"),s.indexOf("speed_limit"));
                //currentupload:
                if(current.contains("upload")){
                    String currentupload = s.substring(s.indexOf("upload_speed:")+13,s.indexOf("download_speed"));
                    user.setCurrentupload(Long.parseLong(currentupload));
                }
                //currentdownload
                if(current.contains("download")){
                    String currentdownload = s.substring(s.indexOf("download_speed:")+15,s.indexOf("}"));
                    user.setCurrentdownload(Long.parseLong(currentdownload));
                }

            }


            //limit
            if(s.contains("speed_limit{upload")||s.contains("speed_limit{download")){
                String limit = s.substring(s.indexOf("speed_limit{upload_speed:"),s.indexOf("ip_limit"));
                //limitupload
                if(limit.contains("upload")){
                    String limitupload = limit.substring(limit.lastIndexOf("upload")+13,limit.lastIndexOf("download_speed"));
                    user.setLimitupload(Long.parseLong(limitupload));
                }
                //limitdownload
                if(limit.contains("download_speed")){
                    String limitdownload = limit.substring(limit.lastIndexOf("download")+15,limit.lastIndexOf("}"));
                    user.setLimitdownload(Long.parseLong(limitdownload));
                }
            }

            if(s.contains("ip_limit")){
                String limitip = s.substring(s.indexOf("ip_limit")+9,s.lastIndexOf("}"));
                user.setLimitip(Integer.parseInt(limitip));
            }
            userList.add(user);
        }
        System.out.println("用户对象列表集合："+userList);

        return userList;
    }

    //添加一个用户
    public void add(User user){
        //需要构建两个流，SetUsersRequest流和SetUsersResponse流
        Api.User protouser = Api.User.newBuilder().setPassword(user.getPassword()).build();
        Api.UserStatus userStatus = Api.UserStatus.newBuilder().setUser(protouser).build();
        Api.SetUsersRequest setUsersRequest = Api.SetUsersRequest.newBuilder()
                .setOperation(Api.SetUsersRequest.Operation.Add).setStatus(userStatus).build();

        StreamObserver<Api.SetUsersResponse> responseStreamObserver =
                new StreamObserver<Api.SetUsersResponse>() {
            @Override
            public void onNext(Api.SetUsersResponse setUsersResponse) {
                System.out.println("数据发送");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("数据出错");
            }

            @Override
            public void onCompleted() {
                System.out.println("传输完成");
            }
        };

        StreamObserver<Api.SetUsersRequest> requestStreamObserver =
                TrojanServerServiceGrpc.newStub(channel).setUsers(responseStreamObserver);

        requestStreamObserver.onNext(setUsersRequest);
    }


}