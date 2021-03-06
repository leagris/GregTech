package gregtech.integration.jei.multiblock.infos;

import com.google.common.collect.Lists;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockInfo;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.electric.multiblockpart.MetaTileEntityRotorHolder;
import gregtech.common.metatileentities.multi.electric.generator.MetaTileEntityLargeTurbine;
import gregtech.integration.jei.multiblock.MultiblockInfoPage;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class LargeTurbineInfo extends MultiblockInfoPage {

    public final MetaTileEntityLargeTurbine turbine;

    public LargeTurbineInfo(MetaTileEntityLargeTurbine turbine) {
        this.turbine = turbine;
    }

    @Override
    public MultiblockControllerBase getController() {
        return turbine;
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        MetaTileEntityHolder holder = new MetaTileEntityHolder();
        holder.setMetaTileEntity(MetaTileEntities.ROTOR_HOLDER[GTValues.EV]);
        holder.getMetaTileEntity().setFrontFacing(EnumFacing.WEST);
        ItemStack rotorStack = MetaItems.TURBINE.getStackForm(Materials.Darmstadtium, null);
        ((MetaTileEntityRotorHolder) holder.getMetaTileEntity()).getRotorInventory().setStackInSlot(0, rotorStack);
        MultiblockShapeInfo.Builder shapeInfo = MultiblockShapeInfo.builder()
                .aisle("CCCC", "CIOC", "CCCC")
                .aisle("CCCC", "R##D", "CCCC")
                .aisle("CCCC", "CSCC", "CCCC")
                .where('S', turbine, EnumFacing.SOUTH)
                .where('C', turbine.turbineType.casingState)
                .where('R', new BlockInfo(MetaBlocks.MACHINE.getDefaultState(), holder))
                .where('D', MetaTileEntities.ENERGY_OUTPUT_HATCH[GTValues.EV], EnumFacing.EAST)
                .where('#', Blocks.AIR.getDefaultState())
                .where('I', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.HV], EnumFacing.NORTH);
        if(turbine.turbineType.hasOutputHatch) {
            shapeInfo.where('O', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.EV], EnumFacing.NORTH);
        } else {
            shapeInfo.where('O', turbine.turbineType.casingState);
        }
        return Lists.newArrayList(shapeInfo.build());
    }

    @Override
    public String[] getDescription() {
        return new String[] {I18n.format("gregtech.multiblock.large_turbine.description")};
    }

}
