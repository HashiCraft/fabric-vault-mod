package com.hashicraft.vault.block.entity;

import com.github.hashicraft.stateful.blocks.StatefulBlockEntity;
import com.hashicraft.vault.Main;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenserEntity extends StatefulBlockEntity {
  public DispenserEntity(BlockPos pos, BlockState state) {
    super(Main.DISPENSER_ENTITY, pos, state, null);
  }

  public static void tick(World world, BlockPos blockPos, BlockState state, DispenserEntity entity) {
    StatefulBlockEntity.tick(world, blockPos, state, entity);
    if (!world.isClient) {
      entity.update(state);
    }
  }

  private void update(BlockState state) {

  }
}
