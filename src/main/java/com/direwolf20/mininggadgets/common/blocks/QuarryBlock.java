package com.direwolf20.mininggadgets.common.blocks;

import com.direwolf20.mininggadgets.common.tiles.QuarryBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class QuarryBlock extends Block {
    public QuarryBlock() {
        super(
                Properties.create(Material.IRON).hardnessAndResistance(2.0f)
        );
    }
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new QuarryBlockTileEntity();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

        TileEntity tile = worldIn.getTileEntity(pos);
        if( tile == null )
            return;

        QuarryBlockTileEntity te = (QuarryBlockTileEntity) tile;
        te.scanAdjacentStorage();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) { //serverOnly
            QuarryBlockTileEntity tile = (QuarryBlockTileEntity) worldIn.getTileEntity(pos);
            if (player.isShiftKeyDown()) {
                if (tile.findMarkers()) {
                    player.sendStatusMessage(new TranslationTextComponent("quarry_marker_pos", tile.getStartPos(), tile.getEndPos()).setStyle(new Style().setColor(TextFormatting.AQUA)), true);
                } else {
                    player.sendStatusMessage(new TranslationTextComponent("quarry_marker_failed").setStyle(new Style().setColor(TextFormatting.RED)), true);
                }
            } else {
                if (tile != null) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, tile, tile.getPos());
                }
            }
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != this) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            ModBlocks.dropInventoryOnReplace(tileEntity, worldIn, pos);
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }
}