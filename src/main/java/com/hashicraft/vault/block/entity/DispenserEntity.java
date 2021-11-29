package com.hashicraft.vault.block.entity;

import java.io.IOException;
import java.util.Base64;

import com.github.hashicraft.stateful.blocks.StatefulBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hashicraft.vault.Main;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

  public String encrypt(String token, String input) {
    // TODO - implement.
    return null;
  }

  public String sign(String token, String input) {
    // TODO - implement.
    return null;
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
