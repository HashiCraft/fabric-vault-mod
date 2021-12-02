package com.hashicraft.vault.block;

import com.hashicraft.vault.Main;
import com.hashicraft.vault.block.entity.DispenserEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Dispenser extends BlockWithEntity {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

  public Dispenser(Settings settings) {
    super(settings);
    setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new DispenserEntity(pos, state);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {
    if (world.isClient)
      return ActionResult.SUCCESS;

    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof DispenserEntity) {
      DispenserEntity dispenser = (DispenserEntity) blockEntity;

      ItemStack itemStack = new ItemStack(Main.CARD);
      Direction direction = dispenser.getCachedState().get(FACING);

      String data = dispenser.encrypt(player.getUuid().toString());
      if (data == null) {
        return ActionResult.SUCCESS;
      }

      String signature = dispenser.sign(data);
      if (signature == null) {
        return ActionResult.SUCCESS;
      }

      NbtCompound identity = itemStack.getOrCreateNbt();
      identity.putString("name", player.getName().asString());
      identity.putString("data", data);
      identity.putString("signature", signature);
      itemStack.setNbt(identity);

      dispenser.dispense(world, itemStack, 1, direction);
    }
    return ActionResult.SUCCESS;
  }
}