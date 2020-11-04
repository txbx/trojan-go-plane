package com.tan.trojangoplane.trojango;

import com.tan.trojangoplane.model.pojo.Result;
import com.tan.trojangoplane.model.pojo.User;
import com.tan.trojangoplane.trojango.callback.CallBack;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import trojan.api.Api;
import trojan.api.TrojanServerServiceGrpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


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

            User user = new User();

            Api.ListUsersResponse userNext = listUsersResponseIterator.next();
            Api.UserStatus userStatus = userNext.getStatus();

            String hash = userStatus.getUser().getHash();
            user.setPwhash(hash);

            long downloadTraffic = userStatus.getTrafficTotal().getDownloadTraffic();
            long uploadTraffic = userStatus.getTrafficTotal().getUploadTraffic();
            user.setSumdownload(downloadTraffic);
            user.setSumupload(uploadTraffic);

            long downloadSpeed = userStatus.getSpeedCurrent().getDownloadSpeed();
            long uploadSpeed = userStatus.getSpeedCurrent().getUploadSpeed();
            user.setCurrentdownload(downloadSpeed);
            user.setCurrentupload(uploadSpeed);

            long limitdownloadSpeed = userStatus.getSpeedLimit().getDownloadSpeed();
            long limituploadSpeed = userStatus.getSpeedLimit().getUploadSpeed();
            user.setLimitdownload(limitdownloadSpeed);
            user.setLimitupload(limituploadSpeed);

            int currentip = userStatus.getIpCurrent();
            int ipLimit = userStatus.getIpLimit();
            user.setCurrentip(currentip);
            user.setLimitip(ipLimit);

            userList.add(user);
        }
        return userList;
    }

    //根据密码获取指定用户对象
    public Result findbypw(CallBack callBack,String pw){
        //构建两个流，GetUsersRequest和GetUsersResponse
        //GetUsersResponse用于和服务器建立连接通道
        //先构建请求流
        Api.User protouser = Api.User.newBuilder().setPassword(pw).build();
        Api.GetUsersRequest getUsersRequest = Api.GetUsersRequest.newBuilder().setUser(protouser).build();

        //判断调用状态。在内部类中被访问，需要加final修饰
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        //构建响应流和服务端建立通道
        StreamObserver<Api.GetUsersResponse> responseStreamObserver =
                new StreamObserver<Api.GetUsersResponse>() {
                    @Override
                    public void onNext(Api.GetUsersResponse getUsersResponse) {
                        callBack.setGetUsersResponse(getUsersResponse);
                        countDownLatch.countDown();
                        //callBack.setResult();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("数据发送错误："+throwable.getMessage());
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("传输完成");
                        countDownLatch.countDown();
                    }
                };

        //打开通道进行数据传输
        StreamObserver<Api.GetUsersRequest> requestStreamObserver = TrojanServerServiceGrpc.newStub(channel).getUsers(responseStreamObserver);
        requestStreamObserver.onNext(getUsersRequest);

        try {
            //如果在规定时间内没有请求完，则让程序停止
            if(!countDownLatch.await(3, TimeUnit.SECONDS)){
                //1秒内没有返回结果，返回超时
                return new Result(false,"超时！");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Api.GetUsersResponse getUsersResponse = callBack.getGetUsersResponse();
        String info = getUsersResponse.getInfo();
        boolean success = getUsersResponse.getSuccess();
        Api.UserStatus status = getUsersResponse.getStatus();
        //实例化用户
        User user = new User();

        String hash = status.getUser().getHash();
        user.setPwhash(hash);

        long downloadTraffic = status.getTrafficTotal().getDownloadTraffic();
        long uploadTraffic = status.getTrafficTotal().getUploadTraffic();
        user.setSumdownload(downloadTraffic);
        user.setSumupload(uploadTraffic);

        long downloadSpeed = status.getSpeedCurrent().getDownloadSpeed();
        long uploadSpeed = status.getSpeedCurrent().getUploadSpeed();
        user.setCurrentdownload(downloadSpeed);
        user.setCurrentupload(uploadSpeed);

        long limitdownloadSpeed = status.getSpeedLimit().getDownloadSpeed();
        long limituploadSpeed = status.getSpeedLimit().getUploadSpeed();
        user.setLimitdownload(limitdownloadSpeed);
        user.setLimitupload(limituploadSpeed);

        int currentip = status.getIpCurrent();
        int ipLimit = status.getIpLimit();
        user.setCurrentip(currentip);
        user.setLimitip(ipLimit);

        if(success){
            return new com.tan.trojangoplane.model.pojo.Result(true,"操作成功完成！",user);
        }else {
            return new com.tan.trojangoplane.model.pojo.Result(false,"操作出错！"+info);
        }
    }

    //操作用户
    //增：添加一个用户（密码，限制上传下载速度，限制同时在线ip）
    //修改用户信息（不能改密码，用户连接信息不用改密码）
    //删：根据密码hash删除用户
    public Result operationTrojanUser(CallBack callBack,User user,Integer operation){
        Api.Speed limitspeed = null;
        Api.UserStatus userStatus = null;

        //需要构建两个流，SetUsersRequest流和SetUsersResponse流

        if(user.getLimitdownload() != null && user.getLimitupload() != null){
            limitspeed = Api.Speed.newBuilder()
                    .setDownloadSpeed(user.getLimitdownload())
                    .setUploadSpeed(user.getLimitupload())
                    .build();
        }else if(user.getLimitdownload() != null && user.getLimitupload() == null){
            limitspeed = Api.Speed.newBuilder()
                    .setDownloadSpeed(user.getLimitdownload())
                    .build();
        }else if(user.getLimitdownload() == null && user.getLimitupload() != null){
            limitspeed = Api.Speed.newBuilder()
                    .setUploadSpeed(user.getLimitupload())
                    .build();
        }else if(user.getLimitdownload() == null && user.getLimitupload() == null){
            limitspeed = Api.Speed.newBuilder()
                    .setDownloadSpeed(0)
                    .setUploadSpeed(0)
                    .build();
        }


        if(user.getLimitip() != null && limitspeed != null){
            userStatus = Api.UserStatus.newBuilder()
                    .setUser(Api.User.newBuilder().setPassword(user.getPassword()).build())
                    .setIpLimit(user.getLimitip())
                    .setSpeedLimit(limitspeed)
                    .build();
        }else if(user.getLimitip() == null && limitspeed != null){
            userStatus = Api.UserStatus.newBuilder()
                    .setUser(Api.User.newBuilder().setPassword(user.getPassword()).build())
                    .setSpeedLimit(limitspeed)
                    .build();
        }else if(user.getLimitip() != null && limitspeed == null){
            userStatus = Api.UserStatus.newBuilder()
                    .setUser(Api.User.newBuilder().setPassword(user.getPassword()).build())
                    .setIpLimit(user.getLimitip())
                    .build();
        }else if(user.getLimitip() == null && limitspeed == null){
            userStatus = Api.UserStatus.newBuilder()
                    .setUser(Api.User.newBuilder().setPassword(user.getPassword()).build())
                    .build();
        }


        Api.SetUsersRequest.Operation trojanoperation = null;

        if(operation == 0){
            trojanoperation = Api.SetUsersRequest.Operation.Add;
        }else if(operation == 1){
            trojanoperation = Api.SetUsersRequest.Operation.Delete;
        }else if(operation == 2){
            trojanoperation = Api.SetUsersRequest.Operation.Modify;
        }else {
            return new Result(false,"操作参数出错");
        }

        Api.SetUsersRequest setUsersRequest = Api.SetUsersRequest.newBuilder()
                .setOperation(trojanoperation).setStatus(userStatus).build();

        //判断调用状态。在内部类中被访问，需要加final修饰
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamObserver<Api.SetUsersResponse> responseStreamObserver =
                new StreamObserver<Api.SetUsersResponse>() {
                    @Override
                    public void onNext(Api.SetUsersResponse setUsersResponse) {
                        callBack.setSetUsersResponse(setUsersResponse);
                        //callBack.callBackInstance();
                        //将countDownLatch置0
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("数据出错");
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        countDownLatch.countDown();
                    }
                };

        StreamObserver<Api.SetUsersRequest> requestStreamObserver =
                TrojanServerServiceGrpc.newStub(channel).setUsers(responseStreamObserver);

        requestStreamObserver.onNext(setUsersRequest);

        try {
            //如果在规定时间内没有请求完，则让程序停止
            if(!countDownLatch.await(3, TimeUnit.SECONDS)){
                //3秒内没有返回结果，返回超时
                return new Result(false,"超时！");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //判断调用结束状态
        //不用判断能执行到这里countDownLatch必为0,也就是有了异步的返回结果
        //if(countDownLatch.getCount() == 0)
        //responseStreamObserver.onCompleted();
        Api.SetUsersResponse result = callBack.getSetUsersResponse();
        boolean success = result.getSuccess();
        if(success){
            return new com.tan.trojangoplane.model.pojo.Result(true,"操作成功完成！");
        }else {
            String info = result.getInfo();
            return new com.tan.trojangoplane.model.pojo.Result(false,"操作出错！"+info);
        }

    }

    //单个服务器状况
    public void serverstatus(){
        //总上传流量，总下载流量，总在线ip(接口，暂时不加在线速度)，
        //实时上传速度，实时下载速度，
        //cpu,内存，硬盘，负载
        //总用户数
    }

    //添加用户的时候传不同ip+端口修改不同服务器中的数据


}