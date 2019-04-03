package com.example.naturae_ui.Server

import com.examples.naturaeproto.Naturae
import com.examples.naturaeproto.ServerRequestsGrpc
import io.grpc.ManagedChannelBuilder

class Test(){
    fun printName(){
        val channel = ManagedChannelBuilder.forAddress("naturae.host", 443).useTransportSecurity().build()
        val stub = ServerRequestsGrpc.newBlockingStub(channel)
        val request = Naturae.HelloRequest.newBuilder().setName("Visal").build()
        val result = stub.sayHello(request)
        System.out.println(result.message)
    }
}