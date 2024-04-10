package com.lncanswer.rpc.registry;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author LNC
 * @version 1.0
 * @description Etcd-Demo
 * @date 2024/4/9 10:17
 */
public class EtcdRegistryDemo {
    public static void main(String[] args) throws ExecutionException,InterruptedException {
        //create client using endpoint
        Client client = Client.builder().endpoints("http://localhost:2379").build();

        //KVClient操作Etcd读写数据
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        //put key
        kvClient.put(key,value);

        //get key
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);

        GetResponse response = getFuture.get();

        //delete key
        kvClient.delete(key).get();

    }
}
