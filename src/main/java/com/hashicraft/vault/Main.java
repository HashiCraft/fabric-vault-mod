package com.hashicraft.vault;

import com.hashicraft.vault.block.Dispenser;
import com.hashicraft.vault.block.Lock;
import com.hashicraft.vault.block.entity.DispenserEntity;
import com.hashicraft.vault.block.entity.LockEntity;
import com.hashicraft.vault.item.Card;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Main implements ModInitializer {
  public static final String MODID = "vault";
  public static final String MOD_NAME = "Vault";

  public static final Identifier DISPENSER_ID = identifier("dispenser");
  public static final Dispenser DISPENSER = new Dispenser(
      FabricBlockSettings.of(Material.METAL).hardness(4.0f).nonOpaque());
  public static BlockEntityType<DispenserEntity> DISPENSER_ENTITY;

  public static final Identifier LOCK_ID = identifier("lock");
  public static final Lock LOCK = new Lock(FabricBlockSettings.of(Material.METAL).hardness(4.0f).nonOpaque());
  public static BlockEntityType<LockEntity> LOCK_ENTITY;

  public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(identifier("general"),
      () -> new ItemStack(DISPENSER));

  public static final Identifier CARD_ID = new Identifier(MODID, "card");
  public static Item CARD = new Card(new Item.Settings().group(ITEM_GROUP));

  public static final String CONFIG_LOCATION = MODID;

  @Override
  public void onInitialize() {
    Registry.register(Registry.BLOCK, DISPENSER_ID, DISPENSER);
    Registry.register(Registry.ITEM, DISPENSER_ID, new BlockItem(DISPENSER, new Item.Settings().group(ITEM_GROUP)));
    DISPENSER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, DISPENSER_ID,
        FabricBlockEntityTypeBuilder.create(DispenserEntity::new, DISPENSER).build(null));

    Registry.register(Registry.BLOCK, LOCK_ID, LOCK);
    Registry.register(Registry.ITEM, LOCK_ID, new BlockItem(LOCK, new Item.Settings().group(ITEM_GROUP)));
    LOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, LOCK_ID,
        FabricBlockEntityTypeBuilder.create(LockEntity::new, LOCK).build(null));

    Registry.register(Registry.ITEM, CARD_ID, CARD);
  }

  public static Identifier identifier(String path) {
    return new Identifier(MODID, path);
  }
}