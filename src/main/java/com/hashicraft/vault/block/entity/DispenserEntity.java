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
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DispenserEntity extends StatefulBlockEntity {
  public DispenserEntity(BlockPos pos, BlockState state) {
    super(Main.DISPENSER_ENTITY, pos, state, null);
  }

  public String encrypt(String input) {
    try {
      // Construct the payload and encode the data for transport.
      String payload = String.format("""
          {
          "plaintext": "%s"
          }
          """, Base64.getEncoder().encodeToString(input.getBytes()));

      // Convert it to JSON.
      StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

      // Create the HTTP request.
      HttpClient httpClient = HttpClientBuilder.create().build();
      HttpPost request = new HttpPost("http://localhost:8100/v1/transit/encrypt/minecraft");
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
      return data.get("data").getAsJsonObject().get("ciphertext").getAsString();
    } catch (IOException e) {
      System.out.println("ERROR: " + e.getMessage());
      return null;
    }
  }

  public String sign(String input) {
    try {
      // Construct the payload.
      String payload = String.format("""
          {
            "hash_algorithm":"sha2-256",
            "signature_algorithm":"pkcs1v15",
            "input":"%s"
          }
          """, Base64.getEncoder().encodeToString(input.getBytes()));

      // Convert it to JSON.
      StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

      // Create the HTTP request.
      HttpClient httpClient = HttpClientBuilder.create().build();
      HttpPost request = new HttpPost("http://localhost:8100/v1/transit/sign/minecraft");
      request.setEntity(entity);

      // Execute the HTTP request.
      HttpResponse response = httpClient.execute(request);
      String body = EntityUtils.toString(response.getEntity());

      // Check if everything went well.
      if (response.getStatusLine().getStatusCode() != 200) {
        System.out.println(body);
        return null;
      }

      // Get the signature from the response.
      JsonObject data = new JsonParser().parse(body).getAsJsonObject();
      return data.get("data").getAsJsonObject().get("signature").getAsString();
    } catch (IOException e) {
      System.out.println("ERROR: " + e.getMessage());
      return null;
    }
  }

  public void dispense(World world, ItemStack stack, int offset, Direction side) {
    BlockPointerImpl pointer = new BlockPointerImpl(MinecraftClient.getInstance().getServer().getOverworld(), pos);
    double x = pointer.getX() + 0.7D * (double) side.getOffsetX();
    double y = pointer.getY() + 0.7D * (double) side.getOffsetY();
    double z = pointer.getZ() + 0.7D * (double) side.getOffsetZ();

    if (side.getAxis() == Direction.Axis.Y) {
      y -= 0.125D;
    } else {
      y -= 0.15625D;
    }

    ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);
    double g = world.random.nextDouble() * 0.1D + 0.2D;
    itemEntity.setVelocity(
        world.random.nextGaussian() * 0.007499999832361937D * (double) offset + (double) side.getOffsetX() * g,
        world.random.nextGaussian() * 0.007499999832361937D * (double) offset + 0.20000000298023224D,
        world.random.nextGaussian() * 0.007499999832361937D * (double) offset + (double) side.getOffsetZ() * g);
    world.spawnEntity(itemEntity);
  }
}
