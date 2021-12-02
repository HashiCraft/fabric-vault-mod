package com.hashicraft.vault.block;

import java.util.Base64;
import java.util.Random;

import com.hashicraft.vault.Main;
import com.hashicraft.vault.block.entity.LockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Lock extends BlockWithEntity {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final BooleanProperty POWERED = Properties.POWERED;

  public Lock(Settings settings) {
    super(settings);
    setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new LockEntity(pos, state);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING);
    builder.add(POWERED);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(POWERED, false);
  }

  // The block is redstone enabled.
  public boolean emitsRedstonePower(BlockState state) {
    return true;
  }

  // If our state is powered, we emit a redstone signal.
  public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
    return state.get(POWERED) ? 15 : 0;
  }

  // Don't forget to tell our neighbors that we are powered.
  protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
    Direction direction = (Direction) state.get(FACING);
    BlockPos blockPos = pos.offset(direction.getOpposite());
    world.updateNeighbor(blockPos, this, pos);
    world.updateNeighborsExcept(blockPos, this, direction);
  }

  // If we are powered, disable it because we don't want to keep the door open.
  public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    if ((Boolean) state.get(POWERED)) {
      world.setBlockState(pos, (BlockState) state.with(POWERED, false), Block.NOTIFY_LISTENERS);
    }

    this.updateNeighbors(world, pos, state);
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {
    if (world.isClient || state.get(POWERED))
      return ActionResult.SUCCESS;

    ItemStack stack = player.getStackInHand(hand);
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if (blockEntity instanceof LockEntity) {
      LockEntity lock = (LockEntity) blockEntity;

      // Only take action if someone is using a keycard.
      if (stack.isOf(Main.CARD)) {
        // TODO: Get the identity from the keycard.

        // TODO: Verify the signature.

        // TODO: Decrypt the data.
      } else {
        player.sendMessage(new LiteralText("ACCESS DENIED - Keycard required to enter"), true);
      }
    }

    return ActionResult.SUCCESS;
  }
}