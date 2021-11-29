package com.hashicraft.vault.item;

import java.util.List;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class Card extends Item {

  public Card(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
    playerEntity.playSound(SoundEvents.BLOCK_ANVIL_HIT, 1.0F, 1.0F);
    return TypedActionResult.success(playerEntity.getStackInHand(hand));
  }

  @Override
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
    if (!stack.hasNbt()) {
      tooltip.add(new LiteralText("Not valid").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
      return;
    }

    NbtCompound identity = stack.getOrCreateNbt();
    String name = identity.getString("name");
    String data = identity.getString("data");
    String signature = identity.getString("signature");

    tooltip.add(new LiteralText("sub: " + name).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    tooltip
        .add(new LiteralText("key: " + data.substring(0, 32) + "...").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
    tooltip.add(
        new LiteralText("sig: " + signature.substring(0, 32) + "...").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
  }
}