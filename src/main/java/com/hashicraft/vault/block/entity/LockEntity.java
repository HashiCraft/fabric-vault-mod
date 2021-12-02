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
    // TODO: implement.
    return false;
  }

  public String decrypt(String input) {
    // TODO: implement.
    return null;
  }
}
