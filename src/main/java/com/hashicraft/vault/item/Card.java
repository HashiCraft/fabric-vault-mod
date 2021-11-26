package com.hashicraft.vault.item;

import java.util.List;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class Card extends Item {
  public Card(Settings settings, String name) {
    super(settings);
  }

  @Override
  public String getTranslationKey() {
    return "item.nomad.nomad_job";
  }

  @Override
  public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
    tooltip.add(new LiteralText("card"));
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
    if (!world.isClient) {

    }
    return TypedActionResult.success(playerEntity.getStackInHand(hand));
  }
}
