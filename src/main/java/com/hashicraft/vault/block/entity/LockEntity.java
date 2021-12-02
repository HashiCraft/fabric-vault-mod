package com.hashicraft.vault.block.entity;

import java.io.IOException;
import java.util.Base64;

import com.github.hashicraft.stateful.blocks.StatefulBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hashicraft.vault.Main;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class LockEntity extends StatefulBlockEntity {
  public LockEntity(BlockPos pos, BlockState state) {
    super(Main.LOCK_ENTITY, pos, state, null);
  }

  public boolean verify(String input, String signature) {
    try {
      // Construct the payload and encode the data for transport.
      String payload = String.format("""
          {
            "hash_algorithm":"sha2-256",
            "signature_algorithm":"pkcs1v15",
            "input":"%s",
            "signature": "%s"
          }
          """, Base64.getEncoder().encodeToString(input.getBytes()), signature);

      // Convert it to JSON.
      StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

      // Create the HTTP request.
      HttpClient httpClient = HttpClientBuilder.create().build();
      HttpPost request = new HttpPost("http://localhost:8100/v1/transit/verify/minecraft");
      request.setEntity(entity);

      // Execute the HTTP request.
      HttpResponse response = httpClient.execute(request);
      String body = EntityUtils.toString(response.getEntity());

      // Check if everything went well.
      if (response.getStatusLine().getStatusCode() != 200) {
        System.out.println(body);
        return false;
      }

      // Get the validity from the response.
      JsonObject data = new JsonParser().parse(body).getAsJsonObject();
      return data.get("data").getAsJsonObject().get("valid").getAsBoolean();
    } catch (IOException e) {
      System.out.println("ERROR: " + e.getMessage());
      return false;
    }
  }

  public String decrypt(String input) {
    try {
      // Construct the payload.
      String payload = String.format("""
          {
          "ciphertext": "%s"
          }
          """, input);
    
      // Convert it to JSON.
      StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
    
      // Create the HTTP request.
      HttpClient httpClient = HttpClientBuilder.create().build();
      HttpPost request = new HttpPost("http://localhost:8100/v1/transit/decrypt/minecraft");
      request.setEntity(entity);
    
      // Execute the HTTP request.
      HttpResponse response = httpClient.execute(request);
      String body = EntityUtils.toString(response.getEntity());
    
      // Check if everything went well.
      if (response.getStatusLine().getStatusCode() != 200) {
        System.out.println(body);
        return null;
      }
    
      // Get the ciphertext from the response.
      JsonObject data = new JsonParser().parse(body).getAsJsonObject();
      return data.get("data").getAsJsonObject().get("plaintext").getAsString();
    } catch (IOException e) {
      System.out.println("ERROR: " + e.getMessage());
      return null;
    }
  }
}
