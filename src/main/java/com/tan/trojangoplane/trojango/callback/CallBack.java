package com.tan.trojangoplane.trojango.callback;

import com.tan.trojangoplane.model.pojo.Result;
import trojan.api.Api;

/**
 * @author : tan
 * @date: 2020/11/3 15:31
 * @description :异步回调
 */
public class CallBack {
    private Api.SetUsersResponse setUsersResponse;
    private Api.GetUsersResponse getUsersResponse;

    public Api.GetUsersResponse getGetUsersResponse() {
        return getUsersResponse;
    }

    public void setGetUsersResponse(Api.GetUsersResponse getUsersResponse) {
        this.getUsersResponse = getUsersResponse;
    }

    public Api.SetUsersResponse getSetUsersResponse(){
        return setUsersResponse;
    }

    public void setSetUsersResponse(Api.SetUsersResponse setUsersResponse) {
        this.setUsersResponse = setUsersResponse;
    }

    public static void callBackStatic(Api.SetUsersResponse setUsersResponse){
        System.out.println(setUsersResponse.getSuccess());
    }

    public void callBackInstance(){
        //回调函数返回值了
        System.out.println(setUsersResponse.getSuccess()+"==="+setUsersResponse.getInfo());
    }

}
