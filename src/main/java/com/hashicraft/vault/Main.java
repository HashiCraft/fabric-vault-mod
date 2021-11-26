package com.hashicraft.vault;

import java.io.File;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.hashicraft.vault.block.Dispenser;
import com.hashicraft.vault.block.entity.DispenserEntity;
import com.hashicraft.vault.item.Card;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Main implements ModInitializer {
  public static final String MODID = "vault";
  public static final String MOD_NAME = "Vault";

  public static final Identifier DISPENSER_ID = identifier("dispenser");
  public static final Dispenser DISPENSER = new Dispenser(
      FabricBlockSettings.of(Material.METAL).hardness(4.0f).nonOpaque());
  public static BlockEntityType<DispenserEntity> DISPENSER_ENTITY;

  public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(identifier("general"),
      () -> new ItemStack(DISPENSER));

  @Override
  public void onInitialize() {
    Registry.register(Registry.BLOCK, DISPENSER_ID, DISPENSER);
    Registry.register(Registry.ITEM, DISPENSER_ID, new BlockItem(DISPENSER, new Item.Settings().group(ITEM_GROUP)));
    DISPENSER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, DISPENSER_ID,
        FabricBlockEntityTypeBuilder.create(DispenserEntity::new, DISPENSER).build(null));
  }

  public static Identifier identifier(String path) {
    return new Identifier(MODID, path);
  }

}